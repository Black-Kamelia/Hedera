package com.kamelia.hedera.core.auth.store

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.UserState
import java.util.*

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
    }
}