package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.response.respondNoSuccess
import com.kamelia.hedera.rest.auth.authRoutes
import com.kamelia.hedera.rest.configuration.globalConfigurationRoutes
import com.kamelia.hedera.rest.file.filesRoutes
import com.kamelia.hedera.rest.file.rawFileCustomLinkRoute
import com.kamelia.hedera.rest.file.rawFileRoute
import com.kamelia.hedera.rest.setting.userSettingsRoutes
import com.kamelia.hedera.rest.token.personalTokensRoutes
import com.kamelia.hedera.rest.user.userRoutes
import com.kamelia.hedera.util.Environment.isDev
import com.kamelia.hedera.util.Environment.isProd
import com.kamelia.hedera.util.Environment.isTest
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            authRoutes()
            userRoutes()
            userSettingsRoutes()
            filesRoutes()
            personalTokensRoutes()
            globalConfigurationRoutes()
        }

        rawFileRoute()
        rawFileCustomLinkRoute()

        when {
            isDev -> get("/") {
                call.respondRedirect("http://localhost:3000")
            }

            isTest -> get("/") {
                call.respondNoSuccess(Response.notFound())
            }

            isProd -> singlePageApplication {
                useResources = true
                vue("static")
            }
        }
    }
}
