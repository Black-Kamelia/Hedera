package com.kamelia.jellyfish.plugins

import com.kamelia.jellyfish.util.Environment.isDev
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        if (isDev) {
            allowHost("localhost:3000")
        }
    }
}