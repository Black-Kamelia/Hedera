package com.kamelia.hedera.core.auth.store

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.UserState
import com.kamelia.hedera.util.withReentrantLock
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlinx.coroutines.sync.Mutex

object InMemorySessionStore : SessionStore {

    private val mutex = Mutex()
    private var userIdToSession = HashMap<UUID, UserSessions>()

    private fun getSessions(userId: UUID): UserSessions =
        userIdToSession[userId] ?: throw ExpiredOrInvalidTokenException()

    override suspend fun createSession(userId: UUID, userState: UserState): Session = mutex.withReentrantLock {
        userIdToSession.computeIfAbsent(userId) { UserSessions(userState) }.createSession()
    }

    override suspend fun verify(userId: UUID, sessionId: UUID): UserState? = mutex.withReentrantLock {
        getSessions(userId).verify(sessionId)
    }

    override suspend fun removeSession(userId: UUID, sessionId: UUID): Unit = mutex.withReentrantLock {
        getSessions(userId).removeSession(sessionId)
    }

    override suspend fun removeAllSessionsExcept(userId: UUID, sessionId: UUID): Unit = mutex.withReentrantLock {
        getSessions(userId).removeAllSessionsExcept(sessionId)
    }

    override suspend fun removeAllSessions(userId: UUID): Unit = mutex.withReentrantLock {
        getSessions(userId).removeAllSessions()
    }

    override suspend fun refreshSession(userId: UUID, sessionId: UUID): Session? = mutex.withReentrantLock {
        getSessions(userId).refreshSession(sessionId)
    }

    override suspend fun updateUserState(userId: UUID, userState: UserState) = mutex.withReentrantLock {
        getSessions(userId).updateSession(userState)
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

        private var sessionIdToSession = HashMap<UUID, Session>()
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
            val sessionId = UUID.randomUUID()
            return Session.from(userState.uuid, sessionId).also {
                sessionIdToSession[sessionId] = it
            }
        }

        fun verify(sessionId: UUID): UserState? = if (sessionId in sessionIdToSession) {
            userState
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
