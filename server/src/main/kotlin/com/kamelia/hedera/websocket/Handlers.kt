package com.kamelia.hedera.websocket

import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.defineEventListener
import com.kamelia.hedera.util.gracefullyClose
import com.kamelia.hedera.util.sendEvent
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID) {
    val closers = mutableListOf<() -> Unit>()

    // Define listeners here
    closers += defineEventListener(UserEvents.userUpdatedEvent) { onUserUpdate(userId, it) }

    keepAlive(closers)
}

private const val USER_UPDATED = "user-updated"
private suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent<UserRepresentationDTO>(USER_UPDATED, user)
}

private const val CONNECTION_CLOSED_BY_CLIENT = "connection-closed-by-client"
private suspend fun WebSocketServerSession.keepAlive(closers: List<() -> Unit>) {
    for (frame in incoming) {
        frame as? Frame.Text ?: continue
        val text = frame.readText()
        if (text.lowercase() == "close") break
    }

    gracefullyClose(CONNECTION_CLOSED_BY_CLIENT) {
        closers.forEach { it() }
    }
}