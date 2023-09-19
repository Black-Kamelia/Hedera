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
import jdk.internal.org.jline.utils.Colors.s
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID, session: String) = keepAlive(userId, session,
    // Define event listeners here
    defineEventListener(UserEvents.userUpdatedEvent, session) { onUserUpdate(userId, it) },
    defineEventListener(UserEvents.userForcefullyLoggedOutEvent, session) { onUserForcefullyLoggedOut(userId, it) },
)

private const val USER_UPDATED = "user-updated"
private suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent(USER_UPDATED, user)
}

private const val USER_FORCEFULLY_LOGGED_OUT = "user-forcefully-logged-out"
private suspend fun WebSocketServerSession.onUserForcefullyLoggedOut(currentId: UUID, payload: UserForcefullyLoggedOutDTO) {
    if (payload.userId != currentId) return
    sendEvent(USER_FORCEFULLY_LOGGED_OUT, UserForcefullyLoggedOutDTO(currentId, payload.reason))
    forcefullyClose(USER_FORCEFULLY_LOGGED_OUT)
}

private const val INVALID_USER_ID = "invalid-user-id"
private const val USER_CONNECTED = "user-connected"
private const val CONNECTION_CLOSED_BY_CLIENT = "connection-closed-by-client"
private const val INVALID_FRAME = "invalid-frame"
private suspend fun WebSocketServerSession.keepAlive(userId: UUID, session: String, vararg closers: () -> Unit) {
    val terminator: () -> Unit = { closers.forEach { it() } }
    val user = SessionManager.getUserOrNull(userId) ?: return forcefullyClose(INVALID_USER_ID, session, terminator)

    sendEvent(USER_CONNECTED, user.toUserRepresentationDTO())
    pingPong(session, terminator)
    gracefullyClose(CONNECTION_CLOSED_BY_CLIENT, session, terminator)
}

private suspend fun WebSocketServerSession.pingPong(session: String, terminator: () -> Unit) {
    for (frame in incoming) when (frame) {
        is Frame.Ping -> send(Frame.Pong(frame.buffer.copy()))
        is Frame.Text -> {
            val text = frame.readText()
            when (text.lowercase()) {
                "close" -> break
                "ping" -> send(Frame.Text("pong"))
                else -> forcefullyClose(INVALID_FRAME, session, terminator)
            }
        }
        else -> forcefullyClose(INVALID_FRAME, session, terminator)
    }
}
