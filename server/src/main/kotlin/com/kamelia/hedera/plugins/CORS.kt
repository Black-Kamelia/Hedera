package com.kamelia.hedera.plugins

import com.kamelia.hedera.util.Environment.isDev
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        if (isDev) {
            HttpMethod.DefaultMethods.forEach(::allowMethod)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }
    }
}