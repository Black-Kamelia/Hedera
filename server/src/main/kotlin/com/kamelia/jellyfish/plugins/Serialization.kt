package com.kamelia.jellyfish.plugins

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.core.pageable.PageableDTO
import com.kamelia.jellyfish.rest.user.UserResponseDTO
import com.kamelia.jellyfish.util.findAnnotated
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                polymorphic(DTO::class, UserResponseDTO::class, UserResponseDTO.serializer())
                /*
                findAnnotated(PageableDTO::class).forEach {
                    println("=> $it")
                    if (it.isInstance(DTO::class)) {
                        @Suppress("UNCHECKED_CAST")
                        polymorphic(DTO::class, it as KClass<DTO>, it.serializer())
                    }
                }
                 */
            }
        })
    }
}
