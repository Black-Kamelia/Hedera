package com.kamelia.hedera.plugins

import com.kamelia.hedera.util.Environment.isDev
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*

fun Route.configureCORS() {
    install(CORS) {
        if (isDev) {
            HttpMethod.DefaultMethods.forEach(::allowMethod)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            exposeHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            anyHost()
        }
    }
}