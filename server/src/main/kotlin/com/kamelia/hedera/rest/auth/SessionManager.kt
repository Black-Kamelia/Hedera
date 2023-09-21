package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.rest.setting.toRepresentationDTO
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserForcefullyLoggedOutDTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest.user.toRepresentationDTO
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.launchPeriodic
import com.kamelia.hedera.util.withReentrantLock
import io.ktor.server.auth.*
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

object SessionManager {

    private val PURGE_INTERVAL = 5.minutes

    private val mutex = Mutex()

    private val sessions = mutableMapOf<String, Session>()
    private val refreshTokens = mutableMapOf<String, TokenData>()
    private val loggedUsers = mutableMapOf<UUID, UserState>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var pruneJob: Job? = null

    fun startPruning() {
        if (pruneJob != null) return
        pruneJob = coroutineScope.launchPeriodic(PURGE_INTERVAL) {
            val now = System.currentTimeMillis()
            mutex.withReentrantLock {
                sessions.entries.removeIf {
                    it.value.tokenData.accessTokenExpiration < now
                }
                loggedUsers.entries.removeIf {
                    sessions.values.none { session -> session.user.uuid == it.key }
                }
                refreshTokens.entries.removeIf {
                    it.value.refreshTokenExpiration < now
                }
            }
        }
    }

    fun stopPruning() = coroutineScope.launch {
        check(pruneJob != null) { "Pruning not started" }
        pruneJob?.cancel()
        pruneJob = null
    }

    private suspend fun generateTokens(
        user: User,
        sessionId: UUID? = null,
        refreshToken: String? = null
    ): TokenData = mutex.withReentrantLock {
        require(sessionId != null || refreshToken != null) { "Either sessionId or token must be provided" }
        require(sessionId == null || refreshToken == null) { "Either sessionId or token must be provided" }

        val userState = loggedUsers.computeIfAbsent(user.id.value) {
            UserState(
                user.id.value,
                user.username,
                user.email,
                user.role,
                user.enabled,
                user.forceChangePassword,
                user.currentDiskQuota,
                user.maximumDiskQuota,
                user.createdAt
            )
        }
        val tokenData = TokenData.from(user)

        if (sessionId == null) {
            val oldTokens = refreshTokens[refreshToken] ?: throw ExpiredOrInvalidTokenException()
            val session = sessions[oldTokens.accessToken] ?: throw ExpiredOrInvalidTokenException()
            sessions[tokenData.accessToken] = session.copy(user = userState, tokenData = tokenData)
        } else {
            val session = Session(sessionId, userState, tokenData)
            sessions[tokenData.accessToken] = session
        }

        println("Generating tokens for session ${sessions[tokenData.accessToken]!!.sessionId}")

        refreshTokens[tokenData.refreshToken] = tokenData
        tokenData
    }

    suspend fun updateSession(userId: UUID, user: User): Unit = mutex.withReentrantLock {
        loggedUsers[userId]?.apply {
            username = user.username
            email = user.email
            role = user.role
            enabled = user.enabled
            forceChangePassword = user.forceChangePassword
            currentDiskQuota = user.currentDiskQuota
            maximumDiskQuota = user.maximumDiskQuota
        }

        if (!user.enabled) {
            logoutAll(user, "disabled")
        } else {
            UserEvents.userUpdatedEvent(user.toRepresentationDTO())
        }
    }

    suspend fun getUserOrNull(userId: UUID): UserState? = mutex.withReentrantLock {
        loggedUsers[userId]
    }

    suspend fun login(username: String, password: String): Response<SessionOpeningDTO> {
        val unauthorized = Response.unauthorized(Errors.Auth.INVALID_CREDENTIALS)
        val user = User.findByUsername(username) ?: return unauthorized
        val sessionId = UUID.randomUUID()

        if (!Hasher.verify(password, user.password).verified) {
            delay(Environment.loginThrottle)
            return unauthorized
        }

        if (!user.enabled) {
            return Response.forbidden(Errors.Auth.ACCOUNT_DISABLED)
        }

        return Response.created(SessionOpeningDTO(
            sessionId = sessionId,
            tokens = generateTokens(user, sessionId = sessionId),
            user = user.toRepresentationDTO(),
            userSettings = user.settings.toRepresentationDTO()
        ))
    }

    suspend fun refresh(jwt: Payload, token: String): Response<TokenData> {
        val user = User.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        return Response.created(generateTokens(user, refreshToken = token))
    }

    suspend fun verify(token: String): Session? = mutex.withReentrantLock {
        sessions[token]
    }

    suspend fun verifyRefresh(token: String): Unit = mutex.withReentrantLock {
        refreshTokens[token] ?: throw ExpiredOrInvalidTokenException()
    }

    suspend fun logout(token: String): Unit = mutex.withReentrantLock {
        val session = sessions.remove(token) ?: throw ExpiredOrInvalidTokenException()
        refreshTokens.remove(session.tokenData.refreshToken)
    }

    suspend fun logoutAll(user: User, reason: String = "logout_all") = mutex.withReentrantLock {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = user.id.value,
                reason = reason,
            )
        )
        sessions.entries.filter {
            it.value.user.uuid == user.id.value
        }.forEach {
            refreshTokens.entries.removeIf { refreshEntry ->
                refreshEntry.value.accessToken == it.key
            }
            sessions.remove(it.key)
        }
        loggedUsers.remove(user.id.value)
    }

    suspend fun logoutAllExceptCurrent(user: User, token: String, sessionId: UUID, reason: String) = mutex.withReentrantLock {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = user.id.value,
                reason = reason,
            ),
            ignoredSessions = listOf(sessionId)
        )
        sessions.entries.filter {
            it.value.user.uuid == user.id.value && it.key != token
        }.forEach {
            refreshTokens.entries.removeIf { refreshEntry ->
                refreshEntry.value.accessToken == it.key
            }
            sessions.remove(it.key)
        }
    }
}

data class UserState(
    val uuid: UUID,
    var username: String,
    var email: String,
    var role: UserRole,
    var enabled: Boolean,
    var forceChangePassword: Boolean,
    var currentDiskQuota: Long,
    var maximumDiskQuota: Long,
    val createdAt: Instant,
) : Principal {

    fun toUserRepresentationDTO() = UserRepresentationDTO(
        uuid,
        username,
        email,
        role,
        enabled,
        forceChangePassword,
        currentDiskQuota,
        if(maximumDiskQuota > 0L) currentDiskQuota.toDouble() / maximumDiskQuota.toDouble() else 0.0,
        maximumDiskQuota,
        createdAt.toString(),
    )
}

data class Session(
    val sessionId: UUID,
    val user: UserState,
    val tokenData: TokenData,
)
