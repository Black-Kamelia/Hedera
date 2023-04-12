package com.kamelia.hedera.plugins

import com.kamelia.hedera.util.Environment.isDev
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        if (isDev) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Get)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }
    }
}