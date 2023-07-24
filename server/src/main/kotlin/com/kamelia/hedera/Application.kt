package com.kamelia.hedera

import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.database.configureLiquibase
import com.kamelia.hedera.plugins.*
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.Environment.isDev
import com.kamelia.hedera.util.MimeTypes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import java.time.Instant
import java.util.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.yaml
fun Application.module() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    Environment.application = this
    if (isDev) log.info("Running in development mode")
    Connection.init()
    MimeTypes.init()
    configureExceptionAdvisors()
    configureLiquibase()
    configureAuthentication()
    configureSerialization()
    configureCORS()
    configureRouting()
    configureWebSockets()
    install(AutoHeadResponse)

    SessionManager.startPruning()
}
