package com.kamelia.hedera.websocket

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

suspend fun DefaultWebSocketServerSession.handleSession(userId: UUID) {

}

private suspend fun DefaultWebSocketServerSession.violentClose(reason: String, closer: () -> Unit) {
    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, reason))
    closer()
}

private suspend fun DefaultWebSocketServerSession.gracefullyClose(reason: String, closer: () -> Unit) {
    close(CloseReason(CloseReason.Codes.NORMAL, reason))
    closer()
}