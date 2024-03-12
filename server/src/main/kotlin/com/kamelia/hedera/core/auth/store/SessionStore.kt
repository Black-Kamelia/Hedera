package com.kamelia.hedera.core.auth.store

import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.UserState
import java.util.*

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

    suspend fun purgeExpiredSessions()
}
