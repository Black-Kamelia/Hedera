package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.TooManyPasswordResetRequestsException
import com.kamelia.hedera.core.ValidationScope
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.validate
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.mail.MailService
import com.kamelia.hedera.rest.auth.CheckResetPasswordTokenDTO
import com.kamelia.hedera.rest.auth.ResetPasswordDTO
import com.kamelia.hedera.rest.auth.ResetPasswordRequestDTO
import com.kamelia.hedera.rest.auth.ResetPasswordTokenDTO
import com.kamelia.hedera.util.launchPeriodic
import io.ktor.http.*
import io.ktor.util.logging.*
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ResetPasswordTokenContainer(
    val userId: UUID,
    val expiration: Instant,
)

sealed interface TokenCheckResult {
    data class ValidToken(val container: ResetPasswordTokenContainer) : TokenCheckResult
    data object TokenNotFound : TokenCheckResult
    data object TokenExpired : TokenCheckResult
}

object PasswordResetService {

    private val LOGGER = KtorSimpleLogger("PasswordResetService")
    private val PURGE_INTERVAL = 5.minutes

    private val mutex = Mutex()
    private val resetPasswordTokens = mutableMapOf<String, ResetPasswordTokenContainer>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var pruneJob: Job? = null

    fun startPruning() {
        if (pruneJob != null) return
        pruneJob = coroutineScope.launchPeriodic(PURGE_INTERVAL) {
            val now = System.currentTimeMillis()
            var count = 0
            mutex.withLock {
                resetPasswordTokens.entries.removeIf {
                    val b = it.value.expiration.toEpochMilli() < now
                    if (b) count++
                    b
                }
            }
            if (count > 0) LOGGER.info("Pruned $count reset password tokens")
        }
    }

    private suspend fun generateToken(userId: UUID): String = mutex.withLock {
        // Remove all previous requests for this user
        resetPasswordTokens.entries
            .filter { it.value.userId == userId }
            .forEach { resetPasswordTokens.remove(it.key) }

        val token = UUID.randomUUID().toString().replace("-", "")
        resetPasswordTokens[token] = ResetPasswordTokenContainer(
            userId = userId,
            expiration = Instant.now().plusSeconds(60 * 10) // 10 minutes
        )
        return token
    }

    suspend fun requestResetPasswordToken(
        dto: ResetPasswordRequestDTO
    ): Response<Nothing> = Connection.transaction {
        val user = User.findByEmail(dto.email) ?: return@transaction Response.ok()

        val token = generateToken(user.id.value)
        MailService.sendResetPasswordMail(user, token)

        Response.ok()
    }

    private suspend fun checkToken(token: String): TokenCheckResult = mutex.withLock {
        val tokenContainer = resetPasswordTokens[token]
        val now = Instant.now()

        if (tokenContainer == null) {
            TokenCheckResult.TokenNotFound
        } else if (!tokenContainer.expiration.isAfter(now)) {
            // if token is expired for more than 10 minutes, remove it
            if (tokenContainer.expiration.plusSeconds(60 * 10).isBefore(now)) {
                resetPasswordTokens.remove(token)
            }
            TokenCheckResult.TokenExpired
        } else {
            TokenCheckResult.ValidToken(tokenContainer)
        }
    }

    suspend fun checkResetPasswordToken(
        dto: CheckResetPasswordTokenDTO
    ): Response<ResetPasswordTokenDTO> = validate {
        val result = validateToken(dto.token)
        catchErrors()
        val tokenContainer = (result as TokenCheckResult.ValidToken).container

        Response.ok(
            ResetPasswordTokenDTO(
                userId = tokenContainer.userId,
                expiration = tokenContainer.expiration.toString(),
            )
        )
    }

    private suspend fun removeResetPasswordToken(token: String): Boolean = mutex.withLock {
        return resetPasswordTokens.remove(token) != null
    }

    suspend fun resetPassword(
        dto: ResetPasswordDTO
    ): Response<Nothing> = Connection.transaction {
        validate {
            val result = validateToken(dto.token)
            catchErrors()
            val tokenContainer = (result as TokenCheckResult.ValidToken).container

            val user = User[tokenContainer.userId]
            user.updatePassword(UserPasswordUpdateDTO(newPassword = dto.password))

            removeResetPasswordToken(dto.token)
            Response.ok()
        }
    }

    private suspend fun ValidationScope.validateToken(token: String): TokenCheckResult {
        val result = checkToken(token)

        if (result is TokenCheckResult.TokenNotFound) {
            raiseError("token", Errors.Users.ResetPassword.TOKEN_NOT_FOUND, HttpStatusCode.Unauthorized)
        }
        if (result is TokenCheckResult.TokenExpired) {
            raiseError("token", Errors.Users.ResetPassword.TOKEN_EXPIRED, HttpStatusCode.Unauthorized)
        }

        return result
    }

}