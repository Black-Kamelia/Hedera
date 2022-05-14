package com.kamelia.jellyfish.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            // TODO: Add routes here
        }
        static("/") {
            resource("/", "static/index.html")
            resources("static")
        }
    }
}
