package com.kamelia.hedera

import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.database.configureLiquibase
import com.kamelia.hedera.plugins.configureAuthentication
import com.kamelia.hedera.plugins.configureCORS
import com.kamelia.hedera.plugins.configureExceptionAdvisors
import com.kamelia.hedera.plugins.configureRouting
import com.kamelia.hedera.plugins.configureSerialization
import com.kamelia.hedera.plugins.configureWebSockets
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.configuration.GlobalConfigurationService
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.Environment.isDev
import com.kamelia.hedera.util.MimeTypes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import java.util.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.yaml
fun Application.module() = runBlocking {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    Environment.application = this@module
    if (isDev) log.info("Running in development mode")

    Connection.init()
    MimeTypes.init()
    GlobalConfigurationService.init()

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
