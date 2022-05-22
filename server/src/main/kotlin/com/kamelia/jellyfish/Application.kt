package com.kamelia.jellyfish

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.plugins.configureCORS
import com.kamelia.jellyfish.plugins.configureRouting
import com.kamelia.jellyfish.plugins.configureSerialization
import com.kamelia.jellyfish.util.Environment.isDev
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    if (isDev) log.info("Running in development mode")
    configureRouting()
    configureSerialization()
    configureCORS()
    Connection.init()
}
