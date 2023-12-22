package com.kamelia.hedera.util

import com.kamelia.hedera.core.Event
import com.kamelia.hedera.rest.core.DTO
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import kotlinx.serialization.Serializable

suspend fun <E> WebSocketServerSession.defineEventListener(event: Event<E>, sessionId: UUID, listener: suspend (E) -> Unit): () -> Unit {
    var closer: (() -> Unit)? = null
    runCatching {
        closer = event.subscribe(listener, sessionId)
    }.onFailure {
        violentlyClose("An error occurred on the server", closer)
    }
    return closer!!
}

suspend fun WebSocketServerSession.forcefullyClose(
    reason: String,
    closer: (() -> Unit)? = null
) = closeWithReason(reason, CloseReason.Codes.VIOLATED_POLICY, closer)

suspend fun WebSocketServerSession.violentlyClose(
    reason: String,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, CloseReason.Codes.INTERNAL_ERROR, closer)

suspend fun WebSocketServerSession.gracefullyClose(
    reason: String,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, CloseReason.Codes.NORMAL, closer)

suspend fun WebSocketServerSession.closeWithReason(
    reason: String,
    type: CloseReason.Codes,
    closer: (() -> Unit)? = null,
) {
    close(CloseReason(type, reason))
    closer?.invoke()
}

@Serializable
data class WebSocketMessage<E>(
    val type: String,
    val data: E,
) : DTO

suspend inline fun <reified E> WebSocketServerSession.sendEvent(type: String, data: E) {
    sendSerialized(WebSocketMessage(type, data))
}
