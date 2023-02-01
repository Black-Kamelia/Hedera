package com.kamelia.hedera.plugins

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.UUIDSerializer
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                polymorphic(DTO::class) {
                    subclass(UserRepresentationDTO::class)
                }
                contextual(UUIDSerializer)
            }
        })
    }
}
