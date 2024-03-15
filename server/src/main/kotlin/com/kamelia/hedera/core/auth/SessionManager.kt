package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
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

    suspend fun verifyRefresh(refreshToken: String): Boolean =
        store.verifyRefresh(refreshToken)

    suspend fun refreshSession(refreshToken: String): Session? {
        val (userId, sessionId) = decodeJWT(refreshToken)
        return store.refreshSession(userId, sessionId)
    }

    suspend fun logout(accessToken: String) {
        val (userId, sessionId) = decodeJWT(accessToken)
        store.removeSession(userId, sessionId)
    }

    suspend fun logoutAllExcept(userId: UUID, sessionId: UUID, reason: String = "logout_all") {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(userId, reason),
            ignoredSessions = listOf(sessionId)
        )
        store.removeAllSessionsExcept(userId, sessionId)
    }

    suspend fun logoutAll(userId: UUID, reason: String = "logout_all") {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(userId,reason)
        )
        store.removeAllSessions(userId)
    }

    suspend fun updateSession(userState: UserState) {
        //if (!userState.enabled) {
        //    logoutAll(userState.uuid, "account_disabled")
        //    return
        //}
        UserEvents.userUpdatedEvent(userState.toUserRepresentationDTO())
        store.updateUserState(userState.uuid, userState)
    }

    fun startPruning() =
        CoroutineScope(Dispatchers.Default).launchPeriodic(Environment.sessionPurgePeriod.milliseconds) {
            store.purgeExpiredSessions()
        }

    private fun decodeJWT(token: String): Pair<UUID, UUID> {
        val decoded = JWT.decode(token)
        return decoded.userId to decoded.sessionId
    }

}
