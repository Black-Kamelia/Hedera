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
import io.ktor.util.*
import io.ktor.websocket.*
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID, session: String) = keepAlive(userId,
    // Define event listeners here
    defineEventListener(UserEvents.userUpdatedEvent) { onUserUpdate(userId, it) },
    defineEventListener(UserEvents.userForcefullyLoggedOutEvent) { onUserForcefullyLoggedOut(userId, session, it) },
)

private const val USER_UPDATED = "user-updated"
private suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent(USER_UPDATED, user)
}

data class UserForcefullyLoggedOutPayload(val userId: UUID, val reason: String, val exceptedSessions: List<String> = emptyList())
private const val USER_FORCEFULLY_LOGGED_OUT = "user-forcefully-logged-out"
private suspend fun WebSocketServerSession.onUserForcefullyLoggedOut(currentId: UUID, session: String, payload: UserForcefullyLoggedOutPayload) {
    if (payload.userId != currentId || session in payload.exceptedSessions) return
    sendEvent(USER_FORCEFULLY_LOGGED_OUT, UserForcefullyLoggedOutDTO(currentId, payload.reason))
    forcefullyClose(USER_FORCEFULLY_LOGGED_OUT)
}

private const val INVALID_USER_ID = "invalid-user-id"
private const val USER_CONNECTED = "user-connected"
private const val CONNECTION_CLOSED_BY_CLIENT = "connection-closed-by-client"
private const val INVALID_FRAME = "invalid-frame"
private suspend fun WebSocketServerSession.keepAlive(userId: UUID, vararg closers: () -> Unit) {
    val terminator: () -> Unit = { closers.forEach { it() } }
    val user = SessionManager.getUserOrNull(userId) ?: return forcefullyClose(INVALID_USER_ID, terminator)

    sendEvent(USER_CONNECTED, user.toUserRepresentationDTO())
    pingPong(terminator)
    gracefullyClose(CONNECTION_CLOSED_BY_CLIENT, terminator)
}

private suspend fun WebSocketServerSession.pingPong(terminator: () -> Unit) {
    for (frame in incoming) when (frame) {
        is Frame.Ping -> send(Frame.Pong(frame.buffer.copy()))
        is Frame.Text -> {
            val text = frame.readText()
            when (text.lowercase()) {
                "close" -> break
                "ping" -> send(Frame.Text("pong"))
                else -> forcefullyClose(INVALID_FRAME, terminator)
            }
        }
        else -> forcefullyClose(INVALID_FRAME, terminator)
    }
}
