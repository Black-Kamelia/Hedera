package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.*
import com.kamelia.hedera.rest.user.*
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

    private suspend fun generateTokens(user: User): TokenData = mutex.withLock {
        val userState = loggedUsers.computeIfAbsent(user.id.value) {
            UserState(user.id.value, user.username, user.email, user.role, user.enabled)
        }
        val tokenData = TokenData.from(user)
        val session = Session(userState, tokenData)

        sessions[tokenData.accessToken] = session
        refreshTokens[tokenData.refreshToken] = tokenData
        return tokenData
    }

    suspend fun updateSession(userId: UUID, user: User): Unit = mutex.withLock {
        loggedUsers[userId]?.apply {
            username = user.username
            email = user.email
            role = user.role
            enabled = user.enabled
        }

        if (!user.enabled) {
            UserEvents.userForcefullyLoggedOutEvent.emit(UserForcefullyLoggedOutDTO(
                userId = user.id.value,
                reason = "force-logout.accounts.disabled",
            ))
            unlockedLogoutAll(user)
        }
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

    suspend fun verify(token: String): UserState? = mutex.withLock {
        return sessions[token]?.user
    }

    suspend fun verifyRefresh(token: String): Unit = mutex.withLock {
        refreshTokens[token] ?: throw ExpiredOrInvalidTokenException()
    }

    suspend fun logout(token: String): Unit = mutex.withLock {
        val session = sessions.remove(token) ?: throw ExpiredOrInvalidTokenException()
        refreshTokens.remove(session.tokenData.refreshToken)
    }

    private fun unlockedLogoutAll(user: User) {
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

    suspend fun logoutAll(user: User) = mutex.withLock {
        UserEvents.userForcefullyLoggedOutEvent.emit(UserForcefullyLoggedOutDTO(
            userId = user.id.value,
            reason = "force-logout.all",
        ))
        unlockedLogoutAll(user)
    }
}

data class UserState(
    var uuid: UUID,
    var username: String,
    var email: String,
    var role: UserRole,
    var enabled: Boolean,
) : Principal

data class Session(
    val user: UserState,
    val tokenData: TokenData,
)
