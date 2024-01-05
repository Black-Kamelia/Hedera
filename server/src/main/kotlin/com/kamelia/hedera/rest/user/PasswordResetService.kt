package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.response.ActionResponse
import com.kamelia.hedera.core.response.asMessage
import com.kamelia.hedera.core.validate
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.mail.MailService
import com.kamelia.hedera.rest.auth.ResetPasswordDTO
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.util.launchPeriodic
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

    private suspend fun generateResetPasswordToken(userId: UUID): String = mutex.withLock {
        val token = UUID.randomUUID().toString().replace("-", "")
        resetPasswordTokens[token] = ResetPasswordTokenContainer(
            userId = userId,
            expiration = Instant.now().plusSeconds(60 * 10) // 10 minutes
        )
        return token
    }

    suspend fun requestResetPasswordToken(
        dto: ResetPasswordDTO
    ): ActionResponse<Nothing> = Connection.transaction {
        validate {
            val user = User.findByEmail(dto.email)

            if (user == null) {
                raiseError("email", Errors.Users.NOT_FOUND)
            }

            catchErrors()

            val token = generateResetPasswordToken(user!!.id.value)
            MailService.sendResetPasswordMail(user, token)

            ActionResponse.ok(
                title = Actions.Users.RequestPasswordReset.success.title,
            )
        }
    }

    suspend fun checkResetPasswordToken(token: String, userId: UUID): Boolean = mutex.withLock {
        val tokenContainer = resetPasswordTokens[token] ?: return false
        return tokenContainer.userId == userId && tokenContainer.expiration.isAfter(Instant.now())
    }

    suspend fun removeResetPasswordToken(token: String): Boolean = mutex.withLock {
        return resetPasswordTokens.remove(token) != null
    }

}