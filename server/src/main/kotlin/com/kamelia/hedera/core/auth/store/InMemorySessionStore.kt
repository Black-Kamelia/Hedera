package com.kamelia.hedera.core.auth.store

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.auth.SESSION_ID_CLAIM
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.USER_ID_CLAIM
import com.kamelia.hedera.core.auth.UserState
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.toUUID
import com.kamelia.hedera.util.withReentrantLock
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlinx.coroutines.sync.Mutex

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
        val userId = decoded.getClaim(USER_ID_CLAIM).asString().toUUID()
        val sessionId = decoded.getClaim(SESSION_ID_CLAIM).asString().toUUID()
        userIdToSession[userId]?.verify(sessionId, token)
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
        if (!userState.enabled) {
            removeAllSessions(userId)
        } else {
            userIdToSession[userId]?.updateSession(userState)
        }
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
            sessionIdToSession = HashMap<UUID, Session>().apply { put(sessionId, toSave) }
        }

        fun removeAllSessions() {
            sessionIdToSession = HashMap()
        }

        fun createSession(): Session {
            if (sessionIdToSession.size == Environment.maximumSessionsPerUser) {
                val oldestSession = sessionIdToSession.entries.stream()
                    .min(Comparator.comparingLong { it.value.lastUsed })
                    .map { it.key }
                    .orElseThrow { AssertionError("Oldest session not found") }
                sessionIdToSession.remove(oldestSession)
            }

            val sessionId = UUID.randomUUID()
            return Session.from(userState.uuid, sessionId).also {
                sessionIdToSession[sessionId] = it
            }
        }

        fun verify(sessionId: UUID, token: String): UserState? = if (sessionId in sessionIdToSession) {
            if (sessionIdToSession[sessionId]?.accessToken?.token != token) {
                null
            } else {
                userState
            }
        } else {
            null
        }

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

fun <E : Map.Entry<K, V>, K, V> Stream<E>.toHashMap(): HashMap<K, V> = collect(Collectors.toMap(
    { it.key },
    { it.value },
    { _, _ -> throw AssertionError() },
    { HashMap() }
))
