package com.kamelia.hedera.core.auth.store

import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.UserState
import java.util.*

interface SessionStore {
    /**
     * Get the user state or null if the user does not exist
     * @param userId the id of the user
     * @return the user state or null if the user does not exist
     */
    suspend fun getUserOrNull(userId: UUID): UserState?

    /**
     * Create a new session for the user with the given user state
     * @param userId the id of the user
     * @param userState the state of the user
     * @return the created session
     */
    suspend fun createSession(userId: UUID, userState: UserState): Session

    /**
     * Verify the token and return the user state or null if the token is invalid/expired
     * @param token the token to verify
     * @return the user state or null if the token is invalid/expired
     */
    suspend fun verify(token: String): UserState?

    /**
     * Verify the refresh token and return true if it is valid
     * @param token the token to verify
     * @return true if the token is valid
     */
    suspend fun verifyRefresh(token: String): Boolean

    /**
     * Remove the session with the given id for the user with the given id
     * @param userId the id of the user
     * @param sessionId the id of the session
     */
    suspend fun removeSession(userId: UUID, sessionId: UUID)

    /**
     * Remove all sessions for the user with the given id except the one with the given id
     * @param userId the id of the user
     * @param sessionId the id of the session to keep
     */
    suspend fun removeAllSessionsExcept(userId: UUID, sessionId: UUID)

    /**
     * Remove all sessions for the user with the given id
     * @param userId the id of the user
     */
    suspend fun removeAllSessions(userId: UUID)

    /**
     * Recreate the session with a new tokens pair
     * @param userId the id of the user
     * @param sessionId the id of the session
     * @return the new session
     */
    suspend fun refreshSession(userId: UUID, sessionId: UUID): Session?

    /**
     * Update the user state for the user with the given id
     * @param userId the id of the user
     * @param userState the new state of the user
     */
    suspend fun updateUserState(userId: UUID, userState: UserState)

    /**
     * Remove all expired sessions regardless of the user
     */
    suspend fun purgeExpiredSessions()
}
