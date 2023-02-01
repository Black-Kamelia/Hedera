package com.kamelia.jellyfish.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.core.Response
import com.kamelia.jellyfish.core.TokenData
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.Users
import io.ktor.server.auth.Principal
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object SessionManager {

    private val PURGE_INTERVAL = 5.minutes

    private val lock = Any()

    private val sessions = mutableMapOf<String, Session>()
    private val refreshTokens = mutableMapOf<String, TokenData>()
    private val loggedUsers = mutableMapOf<UUID, UserState>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var pruneJob: Job? = null

    fun startPruning() = synchronized(coroutineScope) {
        if (pruneJob != null) return
        pruneJob = coroutineScope.launch {
            while (isActive) {
                delay(PURGE_INTERVAL)
                val now = System.currentTimeMillis()
                synchronized(lock) {
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

    fun stopPruning() = synchronized(coroutineScope) {
        check(pruneJob != null) { "Pruning not started" }
        pruneJob?.cancel()
        pruneJob = null
    }

    private fun generateTokens(user: User): TokenData = synchronized(lock) {
        val userState = loggedUsers.computeIfAbsent(user.id.value) {
            UserState(user.id.value, user.username, user.email, user.role)
        }
        val tokenData = TokenData.from(user)
        val session = Session(userState, tokenData)

        sessions[tokenData.accessToken] = session
        refreshTokens[tokenData.refreshToken] = tokenData
        return tokenData
    }

    fun updateSession(userId: UUID, user: User): Unit = synchronized(lock) {
        loggedUsers[userId]?.apply {
            username = user.username
            email = user.email
            role = user.role
        }
    }

    suspend fun login(username: String, password: String): Response<TokenData, List<ErrorDTO>> {
        val unauthorized = Response.unauthorized("errors.auth.verify.unauthorized")
        val user = Users.findByUsername(username) ?: return unauthorized

        if (!Hasher.verify(password, user.password).verified) {
            return unauthorized
        }

        return Response.ok(generateTokens(user))
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, List<ErrorDTO>> {
        val user = Users.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        return Response.ok(generateTokens(user))
    }

    fun verify(token: String): UserState? = synchronized(lock) {
        return sessions[token]?.user
    }

    fun verifyRefresh(token: String): Unit = synchronized(lock) {
        refreshTokens[token] ?: throw ExpiredOrInvalidTokenException()
    }

    fun logout(token: String): Unit = synchronized(lock) {
        val session = sessions.remove(token) ?: throw ExpiredOrInvalidTokenException()
        refreshTokens.remove(session.tokenData.refreshToken)
    }

    fun logoutAll(user: User): Unit = synchronized(lock) {
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
) : Principal

data class Session(
    val user: UserState,
    val tokenData: TokenData,
)
