package com.kamelia.hedera.plugins

import com.kamelia.hedera.websocket.webSocketRoutes
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds.toJavaDuration()
        timeout = 15.seconds.toJavaDuration()
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(HederaJsonModule)
    }

    routing {
        webSocketRoutes()
    }
}
