package com.kamelia.hedera.core.auth.store

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.UserState
import com.kamelia.hedera.core.auth.sessionId
import com.kamelia.hedera.core.auth.userId
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.withReentrantLock
import kotlinx.coroutines.sync.Mutex
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

object InMemorySessionStore : SessionStore {

    private val mutex = Mutex()
    private var userIdToSession = HashMap<UUID, UserSessions>()

    override suspend fun getUserOrNull(userId: UUID): UserState? = mutex.withReentrantLock {
        userIdToSession[userId]?.userState
    }

    override suspend fun createSession(userId: UUID, userState: UserState): Session = mutex.withReentrantLock {
        userIdToSession.computeIfAbsent(userId) { UserSessions(userState) }.createSession()
    }

    override suspend fun verify(token: String): UserState? = mutex.withReentrantLock {
        val decoded = JWT.decode(token)
        userIdToSession[decoded.userId]?.verify(decoded.sessionId, token)
    }

    override suspend fun verifyRefresh(token: String): Boolean {
        val decoded = JWT.decode(token)
        return userIdToSession[decoded.userId]?.verifyRefresh(decoded.sessionId, token) ?: false
    }

    override suspend fun removeSession(userId: UUID, sessionId: UUID): Unit = mutex.withReentrantLock {
        userIdToSession[userId]?.removeSession(sessionId)
    }

    override suspend fun removeAllSessionsExcept(userId: UUID, sessionId: UUID): Unit = mutex.withReentrantLock {
        userIdToSession[userId]?.removeAllSessionsExcept(sessionId)
    }

    override suspend fun removeAllSessions(userId: UUID): Unit = mutex.withReentrantLock {
        userIdToSession[userId]?.removeAllSessions()
    }

    override suspend fun refreshSession(userId: UUID, sessionId: UUID): Session? = mutex.withReentrantLock {
        userIdToSession[userId]?.refreshSession(sessionId)
    }

    override suspend fun updateUserState(userId: UUID, userState: UserState): Unit = mutex.withReentrantLock {
        userIdToSession[userId]?.updateSession(userState)
    }

    override suspend fun purgeExpiredSessions() = mutex.withReentrantLock {
        val now = System.currentTimeMillis()
        userIdToSession = userIdToSession.entries.stream()
            .filter { (_, sessions) -> sessions.purgeExpiredSessions(now) }
            .toHashMap()
    }

    private class UserSessions(
        userState: UserState
    ) {

        private var sessionIdToSession = HashMap<UUID, Session>(Environment.maximumSessionsPerUser)
        var userState: UserState = userState
            private set

        fun removeSession(sessionId: UUID) {
            sessionIdToSession.remove(sessionId)
        }

        fun removeAllSessionsExcept(sessionId: UUID) {
            val toSave = sessionIdToSession[sessionId] ?: return removeAllSessions()
            sessionIdToSession = hashMapOf(sessionId to toSave)
        }

        fun removeAllSessions() {
            sessionIdToSession = hashMapOf()
        }

        fun createSession(): Session {
            if (sessionIdToSession.size == Environment.maximumSessionsPerUser) {
                sessionIdToSession.entries.stream()
                    .min(Comparator.comparingLong { it.value.lastUsed })
                    .map { it.key }
                    .ifPresent { sessionIdToSession.remove(it) }
            }

            val sessionId = UUID.randomUUID()
            return Session.from(userState.uuid, sessionId).also {
                sessionIdToSession[sessionId] = it
            }
        }

        fun verify(sessionId: UUID, token: String): UserState? =
            if (sessionId in sessionIdToSession && sessionIdToSession[sessionId]?.accessToken?.token == token) {
                userState
            } else {
                null
            }

        fun verifyRefresh(sessionId: UUID, token: String): Boolean =
            sessionId in sessionIdToSession && sessionIdToSession[sessionId]?.refreshToken?.token == token

        fun refreshSession(sessionId: UUID): Session? =
            sessionIdToSession.computeIfPresent(sessionId) { _, _ -> Session.from(userState.uuid, sessionId) }

        fun updateSession(userState: UserState) {
            this.userState = userState
        }

        fun purgeExpiredSessions(now: Long): Boolean {
            sessionIdToSession = sessionIdToSession.entries.stream()
                .filter { (_, session) -> session.refreshToken.expiration > now }
                .toHashMap()
            return sessionIdToSession.isNotEmpty()
        }
    }
}

private fun <E : Map.Entry<K, V>, K, V> Stream<E>.toHashMap(): HashMap<K, V> = collect(Collectors.toMap(
    { it.key },
    { it.value },
    { _, _ -> throw AssertionError() },
    { HashMap() }
))
