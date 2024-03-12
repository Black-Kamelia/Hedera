package com.kamelia.hedera.rest.auth

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.SESSION_ID_CLAIM
import com.kamelia.hedera.core.Session
import com.kamelia.hedera.core.USER_ID_CLAIM
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.util.toUUID
import com.kamelia.hedera.util.withReentrantLock
import java.time.Instant
import java.util.*
import kotlinx.coroutines.sync.Mutex


interface SessionStore {
    suspend fun createSession(userId: UUID, userState: UserState): Session
    suspend fun verify(userId: UUID, sessionId: UUID): UserState?
    suspend fun removeSession(userId: UUID, sessionId: UUID)
    suspend fun removeAllSessionsExcept(userId: UUID, sessionId: UUID)
    suspend fun removeAllSessions(userId: UUID)

    /**
     * Recreate the session with a new tokens pair
     */
    suspend fun refreshSession(userId: UUID, sessionId: UUID): Session?
    suspend fun updateUserState(userId: UUID, userState: UserState)
}

object SessionManager {

    private val store: SessionStore = InMemorySessionStore

    private val mutex = Mutex()

    suspend fun createSession(userId: UUID, userState: UserState): Session = mutex.withReentrantLock {
        store.createSession(userId, userState)
    }

    suspend fun verify(accessToken: String): UserState? = mutex.withReentrantLock {
        val (userId, sessionId) = decodeJWT(accessToken)
        store.verify(userId, sessionId)
    }

    suspend fun refreshSession(refreshToken: String): Session? = mutex.withReentrantLock {
        val (userId, sessionId) = decodeJWT(refreshToken)
        store.refreshSession(userId, sessionId)
    }

    suspend fun logout(accessToken: String) = mutex.withReentrantLock {
        val (userId, sessionId) = decodeJWT(accessToken)
        store.removeSession(userId, sessionId)
    }

    suspend fun logoutAll(accessToken: String) = mutex.withReentrantLock {
        val (userId, _) = decodeJWT(accessToken)
        store.removeAllSessions(userId)
    }

    suspend fun updateSession(userState: UserState) = mutex.withReentrantLock {
        store.updateUserState(userState.uuid, userState)
    }

    private fun decodeJWT(token: String): Pair<UUID, UUID> {
        val decoded = JWT.decode(token)
        val userId = decoded.getClaim(USER_ID_CLAIM).asString().toUUID()
        val sessionId = decoded.getClaim(SESSION_ID_CLAIM).asString().toUUID()
        return userId to sessionId
    }

}

object InMemorySessionStore : SessionStore {

    private val userIdToSession = HashMap<UUID, UserSessions>()

    private fun getSessions(userId: UUID): UserSessions =
        userIdToSession[userId] ?: throw ExpiredOrInvalidTokenException()

    override suspend fun createSession(userId: UUID, userState: UserState): Session =
        userIdToSession.computeIfAbsent(userId) { UserSessions(userState) }.createSession()

    override suspend fun verify(userId: UUID, sessionId: UUID): UserState? =
        getSessions(userId).verify(sessionId)

    override suspend fun removeSession(userId: UUID, sessionId: UUID): Unit =
        getSessions(userId).removeSession(sessionId)

    override suspend fun removeAllSessionsExcept(userId: UUID, sessionId: UUID): Unit =
        getSessions(userId).removeAllSessionsExcept(sessionId)

    override suspend fun removeAllSessions(userId: UUID): Unit =
        getSessions(userId).removeAllSessions()

    override suspend fun refreshSession(userId: UUID, sessionId: UUID): Session? =
        getSessions(userId).refreshSession(sessionId)

    override suspend fun updateUserState(userId: UUID, userState: UserState) {
        getSessions(userId).updateSession(userState)
    }

    private class UserSessions(
        userState: UserState
    ) {

        var userState: UserState = userState
            private set

        private var sessionIdToSession = HashMap<UUID, Session>()

        fun getSession(sessionId: UUID): Session {
            return sessionIdToSession[sessionId] ?: throw ExpiredOrInvalidTokenException()
        }

        fun removeSession(sessionId: UUID) {
            sessionIdToSession.remove(sessionId)
        }

        fun removeAllSessionsExcept(sessionId: UUID) {
            val toSave = sessionIdToSession[sessionId] ?: return removeAllSessions()
            sessionIdToSession = HashMap<UUID, Session>().apply { put(sessionId, toSave) }
        }

        fun removeAllSessions() {
            sessionIdToSession = HashMap()
        }

        fun createSession(): Session {
            val sessionId = UUID.randomUUID()
            return Session.from(userState.uuid, sessionId).also {
                sessionIdToSession[sessionId] = it
            }
        }

        fun verify(sessionId: UUID): UserState? = if (sessionId !in sessionIdToSession) {
            userState
        } else {
            null
        }

        fun refreshSession(sessionId: UUID): Session? =
            sessionIdToSession.computeIfPresent(sessionId) { _, _ -> Session.from(userState.uuid, sessionId) }

        fun updateSession(userState: UserState) {
            this.userState = userState
        }
    }
}


data class UserState(
    val uuid: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val enabled: Boolean,
    val forceChangePassword: Boolean,
    val currentDiskQuota: Long,
    val maximumDiskQuota: Long,
    val createdAt: Instant,
) {

    fun toUserRepresentationDTO() = UserRepresentationDTO(
        uuid,
        username,
        email,
        role,
        enabled,
        forceChangePassword,
        currentDiskQuota,
        if (maximumDiskQuota > 0L) currentDiskQuota.toDouble() / maximumDiskQuota.toDouble() else 0.0,
        maximumDiskQuota,
        createdAt.toString(),
    )
}


fun User.toUserState() = UserState(
    id.value,
    username,
    email,
    role,
    enabled,
    forceChangePassword,
    currentDiskQuota,
    maximumDiskQuota,
    createdAt
)

