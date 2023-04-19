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

suspend fun DefaultWebSocketServerSession.forcefullyClose(reason: String, closer: (() -> Unit)? = null) {
    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, reason))
    closer?.invoke()
}

suspend fun DefaultWebSocketServerSession.violentlyClose(reason: String, closer: (() -> Unit)? = null) {
    close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, reason))
    closer?.invoke()
}

suspend fun DefaultWebSocketServerSession.gracefullyClose(reason: String, closer: (() -> Unit)? = null) {
    close(CloseReason(CloseReason.Codes.NORMAL, reason))
    closer?.invoke()
}