package com.kamelia.hedera.util

import com.kamelia.hedera.core.Event
import io.ktor.server.websocket.*
import io.ktor.websocket.*

suspend fun <E> WebSocketServerSession.defineEventListener(event: Event<E>, listener: suspend (E) -> Unit) {
    var closer: (() -> Unit)? = null
    try {
        closer = event.subscribe(listener)
    } finally {
        violentlyClose("An error occurred on the server", closer)
    }
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


const val TYPE = "type"
const val DATA = "data"
suspend inline fun <reified E> WebSocketServerSession.sendEvent(type: String, data: E) {
    sendSerialized(mapOf(
        TYPE to type,
        DATA to data
    ))
}