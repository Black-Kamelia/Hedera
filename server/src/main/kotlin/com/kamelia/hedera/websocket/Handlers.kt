package com.kamelia.hedera.websocket

import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserForcefullyLoggedOutDTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.defineEventListener
import com.kamelia.hedera.util.forcefullyClose
import com.kamelia.hedera.util.gracefullyClose
import com.kamelia.hedera.util.sendEvent
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID) = keepAlive(userId,
    // Define event listeners here
    defineEventListener(UserEvents.userUpdatedEvent) { onUserUpdate(userId, it) },
    defineEventListener(UserEvents.userForcefullyLoggedOutEvent) { onUserForcefullyLoggedOut(userId, it) },
)

private const val USER_UPDATED = "user-updated"
private suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent(USER_UPDATED, user)
}

private const val USER_FORCEFULLY_LOGGED_OUT = "user-forcefully-logged-out"
private suspend fun WebSocketServerSession.onUserForcefullyLoggedOut(currentId: UUID, payload: UserForcefullyLoggedOutDTO) {
    println("Hello")
    if (payload.userId != currentId) return
    println("Bye")
    sendEvent(USER_FORCEFULLY_LOGGED_OUT, payload)
}

private const val INVALID_USER_ID = "invalid-user-id"
private const val USER_CONNECTED = "user-connected"
private const val CONNECTION_CLOSED_BY_CLIENT = "connection-closed-by-client"
private suspend fun WebSocketServerSession.keepAlive(userId: UUID, vararg closers: () -> Unit) {
    val user = SessionManager.getUserOrNull(userId) ?: return forcefullyClose(INVALID_USER_ID) {
        closers.forEach { it() }
    }

    sendEvent(USER_CONNECTED, user.toUserRepresentationDTO())

    for (frame in incoming) {
        if (frame !is Frame.Text) continue
        val text = frame.readText()
        if (text.lowercase() == "close") break
    }

    gracefullyClose(CONNECTION_CLOSED_BY_CLIENT) {
        closers.forEach { it() }
    }
}
