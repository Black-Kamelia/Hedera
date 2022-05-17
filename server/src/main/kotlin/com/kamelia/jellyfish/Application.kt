package com.kamelia.jellyfish

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.plugins.configureCORS
import com.kamelia.jellyfish.plugins.configureRouting
import com.kamelia.jellyfish.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    configureRouting()
    configureSerialization()
    configureCORS()
    Connection.init()
}
