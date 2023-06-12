package com.kamelia.hedera.plugins

import com.kamelia.hedera.rest.auth.authRoutes
import com.kamelia.hedera.rest.file.filesRoutes
import com.kamelia.hedera.rest.user.userRoutes
import com.kamelia.hedera.util.Environment.isDev
import com.kamelia.hedera.util.Environment.isProd
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.*
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        route("/api") {
            configureCORS()

            authRoutes()
            userRoutes()
            filesRoutes()
        }

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
