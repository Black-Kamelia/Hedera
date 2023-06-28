package com.kamelia.hedera.plugins

import com.kamelia.hedera.rest.auth.authRoutes
import com.kamelia.hedera.rest.file.filesRoutes
import com.kamelia.hedera.rest.file.rawFileRoute
import com.kamelia.hedera.rest.user.userRoutes
import com.kamelia.hedera.util.Environment.isDev
import com.kamelia.hedera.util.Environment.isProd
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            authRoutes()
            userRoutes()
            filesRoutes()
        }

        rawFileRoute()

        when {
            isDev -> get("/") {
                call.respondRedirect("http://localhost:3000")
            }

            isProd -> singlePageApplication {
                useResources = true
                vue("static")
            }
        }
    }
}
