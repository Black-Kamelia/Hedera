package com.kamelia.hedera.websocket

import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.defineEventListener
import com.kamelia.hedera.util.gracefullyClose
import com.kamelia.hedera.util.sendEvent
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID) = keepAlive(
    // Define event listeners here
    defineEventListener(UserEvents.userUpdatedEvent) { onUserUpdate(userId, it) },
)

private const val USER_UPDATED = "user-updated"
private suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent(USER_UPDATED, user)
}

private const val CONNECTION_CLOSED_BY_CLIENT = "connection-closed-by-client"
private suspend fun WebSocketServerSession.keepAlive(vararg closers: () -> Unit) {
    for (frame in incoming) {
        if (frame !is Frame.Text) continue
        val text = frame.readText()
        if (text.lowercase() == "close") break
    }

    gracefullyClose(CONNECTION_CLOSED_BY_CLIENT) {
        closers.forEach { it() }
    }
}
