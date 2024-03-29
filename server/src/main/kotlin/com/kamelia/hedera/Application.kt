package com.kamelia.hedera

import com.kamelia.hedera.core.auth.SessionManager
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.database.configureLiquibase
import com.kamelia.hedera.plugins.configureAuthentication
import com.kamelia.hedera.plugins.configureCORS
import com.kamelia.hedera.plugins.configureExceptionAdvisors
import com.kamelia.hedera.plugins.configureFreemarker
import com.kamelia.hedera.plugins.configureRouting
import com.kamelia.hedera.plugins.configureSerialization
import com.kamelia.hedera.plugins.configureWebSockets
import com.kamelia.hedera.rest.configuration.GlobalConfigurationService
import com.kamelia.hedera.rest.thumbnail.ThumbnailService
import com.kamelia.hedera.rest.user.PasswordResetService
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.EnvironmentValues
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

    Environment = EnvironmentValues(this@module.environment.config)
    if (Environment.isDev) log.info("Running in development mode")

    Connection.init()
    MimeTypes.init()
    GlobalConfigurationService.init()
    ThumbnailService.init()

    configureExceptionAdvisors()
    configureLiquibase()
    configureAuthentication()
    configureSerialization()
    configureCORS()
    configureRouting()
    configureWebSockets()
    configureFreemarker()
    install(AutoHeadResponse)

    SessionManager.startPruning()
    PasswordResetService.startPruning()
}
