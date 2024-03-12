package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.auth.store.InMemorySessionStore
import com.kamelia.hedera.core.auth.store.SessionStore
import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserForcefullyLoggedOutDTO
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.launchPeriodic
import com.kamelia.hedera.util.toUUID
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object SessionManager {

    private val store: SessionStore = InMemorySessionStore

    suspend fun getUserOrNull(userId: UUID): UserState? =
        store.getUserOrNull(userId)

    suspend fun createSession(userId: UUID, userState: UserState): Session =
        store.createSession(userId, userState)

    suspend fun verify(accessToken: String): UserState? =
        store.verify(accessToken)

    suspend fun refreshSession(refreshToken: String): Session? {
        val (userId, sessionId) = decodeJWT(refreshToken)
        return store.refreshSession(userId, sessionId)
    }

    suspend fun logout(accessToken: String) {
        val (userId, sessionId) = decodeJWT(accessToken)
        return store.removeSession(userId, sessionId)
    }

    suspend fun logoutAllExcept(userId: UUID, sessionId: UUID, reason: String = "logout_all") {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = userId,
                reason = reason,
            ),
            ignoredSessions = listOf(sessionId)
        )

        return store.removeAllSessionsExcept(userId, sessionId)
    }

    suspend fun logoutAll(userId: UUID, reason: String = "logout_all") {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = userId,
                reason = reason,
            )
        )
        return store.removeAllSessions(userId)
    }

    suspend fun updateSession(userState: UserState) {
        return store.updateUserState(userState.uuid, userState)
    }

    fun startPruning() =
        CoroutineScope(Dispatchers.Default).launchPeriodic(Environment.sessionPurgePeriod.milliseconds) {
            store.purgeExpiredSessions()
        }

    private fun decodeJWT(token: String): Pair<UUID, UUID> {
        val decoded = JWT.decode(token)
        val userId = decoded.getClaim(USER_ID_CLAIM).asString().toUUID()
        val sessionId = decoded.getClaim(SESSION_ID_CLAIM).asString().toUUID()
        return userId to sessionId
    }

}
