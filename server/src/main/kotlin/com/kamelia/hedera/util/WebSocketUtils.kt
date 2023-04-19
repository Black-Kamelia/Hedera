package com.kamelia.hedera.util

import com.kamelia.hedera.core.Event
import io.ktor.server.websocket.*
import io.ktor.websocket.*

suspend fun <E> DefaultWebSocketServerSession.defineEventListener(event: Event<E>, listener: (E) -> Unit) {
    var closer: (() -> Unit)? = null
    try {
        closer = event.subscribe(listener)
    } finally {
        violentlyClose("An error occurred on the server", closer)
    }
}

suspend fun DefaultWebSocketServerSession.forcefullyClose(
    reason: String,
    closer: (() -> Unit)? = null
) = closeWithReason(reason, CloseReason.Codes.VIOLATED_POLICY, closer)

suspend fun DefaultWebSocketServerSession.violentlyClose(
    reason: String,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, CloseReason.Codes.INTERNAL_ERROR, closer)

suspend fun DefaultWebSocketServerSession.gracefullyClose(
    reason: String,
    closer: (() -> Unit)? = null,
) = closeWithReason(reason, CloseReason.Codes.NORMAL, closer)

suspend fun DefaultWebSocketServerSession.closeWithReason(
    reason: String,
    type: CloseReason.Codes,
    closer: (() -> Unit)? = null,
) {
    close(CloseReason(type, reason))
    closer?.invoke()
}