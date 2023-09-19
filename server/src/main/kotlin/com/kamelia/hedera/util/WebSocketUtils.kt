package com.kamelia.hedera.util

import com.kamelia.hedera.core.Event
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.core.DTO
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable

suspend fun <E> WebSocketServerSession.defineEventListener(event: Event<E>, session: String, listener: suspend (E) -> Unit): () -> Unit {
    var closer: (() -> Unit)? = null
    runCatching {
        closer = event.subscribe(listener, session)
    }.onFailure {
        violentlyClose("An error occurred on the server", session, closer)
    }
    return closer!!
}

suspend fun WebSocketServerSession.forcefullyClose(
    reason: String,
    session: String? = null,
    closer: (() -> Unit)? = null
) = closeWithReason(reason, session, CloseReason.Codes.VIOLATED_POLICY, closer)

suspend fun WebSocketServerSession.violentlyClose(
    reason: String,
    session: String? = null,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, session, CloseReason.Codes.INTERNAL_ERROR, closer)

suspend fun WebSocketServerSession.gracefullyClose(
    reason: String,
    session: String? = null,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, session, CloseReason.Codes.NORMAL, closer)

suspend fun WebSocketServerSession.closeWithReason(
    reason: String,
    session: String? = null,
    type: CloseReason.Codes,
    closer: (() -> Unit)? = null,
) {
    if (session != null) SessionManager.removeWebsocketSession(session)
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
