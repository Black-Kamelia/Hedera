package com.kamelia.jellyfish.plugins

import com.kamelia.jellyfish.util.Environment.isDev
import com.kamelia.jellyfish.util.Environment.isProd
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            // TODO: Add routes here
            get("hello") {
                call.respondText("'Hello from Ktor !'")
            }
        }

        when {
            this@configureRouting.isDev -> get("/") {
                call.respondRedirect("http://localhost:3000")
            }
            this@configureRouting.isProd -> {
                static("/") {
                    resource("/", "static/index.html")
                    resources("static")
                }
            }
        }
    }
}
