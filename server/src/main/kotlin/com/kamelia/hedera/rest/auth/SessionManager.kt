package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.*
import com.kamelia.hedera.rest.user.*
import com.kamelia.hedera.util.withReentrantLock
import io.ktor.server.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import kotlin.time.Duration.Companion.minutes

object SessionManager {

    private val PURGE_INTERVAL = 5.minutes

    private val mutex = Mutex()

    private val sessions = mutableMapOf<String, Session>()
    private val refreshTokens = mutableMapOf<String, TokenData>()
    private val loggedUsers = mutableMapOf<UUID, UserState>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var pruneJob: Job? = null

    fun startPruning() = coroutineScope.launch {
        if (pruneJob != null) return@launch
        pruneJob = coroutineScope.launch {
            while (isActive) {
                delay(PURGE_INTERVAL)
                val now = System.currentTimeMillis()
                mutex.withLock {
                    sessions.entries.removeIf {
                        it.value.tokenData.accessTokenExpiration < now
                    }
                    loggedUsers.entries.removeIf {
                        !sessions.values.any { session -> session.user.uuid == it.key }
                    }
                    refreshTokens.entries.removeIf { it.value.refreshTokenExpiration < now }
                }
            }
        }
    }

    fun stopPruning() = coroutineScope.launch {
        check(pruneJob != null) { "Pruning not started" }
        pruneJob?.cancel()
        pruneJob = null
    }

    private suspend fun generateTokens(user: User): TokenData = mutex.withReentrantLock {
        val userState = loggedUsers.computeIfAbsent(user.id.value) {
            UserState(user.id.value, user.username, user.email, user.role, user.enabled, user.uploadToken)
        }
        val tokenData = TokenData.from(user)
        val session = Session(userState, tokenData)

        sessions[tokenData.accessToken] = session
        refreshTokens[tokenData.refreshToken] = tokenData
        tokenData
    }

    suspend fun updateSession(userId: UUID, user: User): Unit = mutex.withReentrantLock {
        loggedUsers[userId]?.apply {
            username = user.username
            email = user.email
            role = user.role
            enabled = user.enabled
            uploadToken = user.uploadToken
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

    suspend fun login(username: String, password: String): Response<TokenData, ErrorDTO> {
        val unauthorized = Response.unauthorized(Errors.Auth.INVALID_CREDENTIALS)
        val user = Users.findByUsername(username) ?: return unauthorized

        if (!Hasher.verify(password, user.password).verified) {
            return unauthorized
        }

        if (!user.enabled) {
            return Response.forbidden(Errors.Auth.ACCOUNT_DISABLED)
        }

        return Response.ok(generateTokens(user))
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, ErrorDTO> {
        val user = Users.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        return Response.ok(generateTokens(user))
    }

    suspend fun verify(token: String): UserState? = mutex.withReentrantLock {
        sessions[token]?.user
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
}

data class UserState(
    var uuid: UUID,
    var username: String,
    var email: String,
    var role: UserRole,
    var enabled: Boolean,
    var uploadToken: String,
) : Principal {

    fun toUserRepresentationDTO() = UserRepresentationDTO(
        uuid,
        username,
        email,
        role,
        enabled,
        uploadToken,
    )
}

data class Session(
    val user: UserState,
    val tokenData: TokenData,
)
