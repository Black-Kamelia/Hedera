package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.auth.store.InMemorySessionStore
import com.kamelia.hedera.core.auth.store.SessionStore
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.launchPeriodic
import com.kamelia.hedera.util.toUUID
import com.kamelia.hedera.util.withReentrantLock
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex

object SessionManager {

    private val store: SessionStore = InMemorySessionStore

    suspend fun createSession(userId: UUID, userState: UserState): Session =
        store.createSession(userId, userState)

    suspend fun verify(accessToken: String): UserState?  {
        return store.verify(accessToken)
    }

    suspend fun refreshSession(refreshToken: String): Session?  {
        val (userId, sessionId) = decodeJWT(refreshToken)
        return store.refreshSession(userId, sessionId)
    }

    suspend fun logout(accessToken: String)  {
        val (userId, sessionId) = decodeJWT(accessToken)
        return store.removeSession(userId, sessionId)
    }

    suspend fun logoutAll(accessToken: String)  {
        val (userId, _) = decodeJWT(accessToken)
        return store.removeAllSessions(userId)
    }

    suspend fun updateSession(userState: UserState)  {
        return store.updateUserState(userState.uuid, userState)
    }

    fun startPruning() = CoroutineScope(Dispatchers.Default).launchPeriodic(Environment.sessionPurgePeriod.milliseconds) {
        store.purgeExpiredSessions()
    }

    private fun decodeJWT(token: String): Pair<UUID, UUID> {
        val decoded = JWT.decode(token)
        val userId = decoded.getClaim(USER_ID_CLAIM).asString().toUUID()
        val sessionId = decoded.getClaim(SESSION_ID_CLAIM).asString().toUUID()
        return userId to sessionId
    }

}
