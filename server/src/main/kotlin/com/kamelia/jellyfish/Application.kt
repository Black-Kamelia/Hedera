package com.kamelia.jellyfish

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.plugins.configureRouting
import com.kamelia.jellyfish.plugins.configureSerialization

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    configureRouting()
    configureSerialization()
    Connection.init()
}
