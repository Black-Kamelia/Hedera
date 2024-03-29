package com.kamelia.hedera.plugins

import com.kamelia.hedera.util.UUIDSerializer
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val HederaJsonModule = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(UUIDSerializer)
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(HederaJsonModule)
    }
}
