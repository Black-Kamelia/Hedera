package com.kamelia.hedera.websocket

import com.kamelia.hedera.rest.user.UserEvents
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.defineEventListener
import com.kamelia.hedera.util.sendEvent
import io.ktor.server.websocket.*
import java.util.*

suspend fun WebSocketServerSession.handleSession(userId: UUID) {
    defineEventListener(UserEvents.userUpdatedEvent) { onUserUpdate(userId, it) }
}

private const val USER_UPDATED = "user-updated"
suspend fun WebSocketServerSession.onUserUpdate(currentId: UUID, user: UserRepresentationDTO) {
    if (user.id != currentId) return
    sendEvent<UserRepresentationDTO>(USER_UPDATED, user)
}
