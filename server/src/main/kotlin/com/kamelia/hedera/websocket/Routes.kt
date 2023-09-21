package com.kamelia.hedera.websocket

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.respondNoError
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.forcefullyClose
import com.kamelia.hedera.util.userOrNull
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.webSocketRoutes() = route("/") {
    authenticate(AuthJwt) {
        getWebSocketToken()
    }

    socketRoute()
}

private fun Route.getWebSocketToken() = get("/api/ws") {
    val principal = call.userOrNull() ?: throw ExpiredOrInvalidTokenException()
    val token = createWebsocketToken(principal.state, principal.sessionId)
    call.respondNoError(Response.created(mapOf("token" to token)))
}

private fun Route.socketRoute() = webSocket("/ws") {
    val token = call.request.queryParameters["token"]
        ?: return@webSocket forcefullyClose("Missing token")

    val userId = validateWebsocketToken(token)
        ?: return@webSocket forcefullyClose("Invalid or expired token")

    handleSession(userId, token)
}
