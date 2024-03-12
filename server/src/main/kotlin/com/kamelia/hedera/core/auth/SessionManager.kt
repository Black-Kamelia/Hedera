package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.kamelia.hedera.core.auth.store.InMemorySessionStore
import com.kamelia.hedera.core.auth.store.SessionStore
import com.kamelia.hedera.util.toUUID
import com.kamelia.hedera.util.withReentrantLock
import java.util.*
import kotlinx.coroutines.sync.Mutex

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
