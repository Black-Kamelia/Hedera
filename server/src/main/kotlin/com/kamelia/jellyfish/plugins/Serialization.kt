package com.kamelia.jellyfish.plugins

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.user.UserResponseDTO
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                polymorphic(DTO::class) {
                    subclass(UserResponseDTO::class)
                }
            }
        })
    }
}
