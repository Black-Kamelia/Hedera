package com.kamelia.hedera.websocket

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.respondNoError
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.authenticatedUser
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.webSocketRoutes() = route("/ws") {
    authenticate(AuthJwt) {
        getWebSocketToken()
    }

    socketRoute()
}

private fun Route.getWebSocketToken() = get("/token") {
    val user = authenticatedUser ?: throw ExpiredOrInvalidTokenException()
    val token = createToken(user)
    call.respondNoError(Response.ok(mapOf("token" to token)))
}

private fun Route.socketRoute() = webSocket("/") {
    val token = call.request.queryParameters["token"]
    if (token == null) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No token"))
        return@webSocket
    }

    val userId = validateToken(token)
    if (userId == null) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid or expired token"))
        return@webSocket
    }

    handleSession(userId)
}
