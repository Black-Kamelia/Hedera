package com.kamelia.jellyfish

import com.kamelia.jellyfish.core.TokenData
import com.kamelia.jellyfish.rest.auth.LoginDTO
import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.user.UserRepresentationDTO
import com.kamelia.jellyfish.util.UUIDSerializer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

typealias TestUser = Pair<TokenData?, UUID>

fun ApplicationTestBuilder.client() = createClient {
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

suspend fun ApplicationTestBuilder.login(
    username: String,
    password: String,
): Pair<HttpStatusCode, TokenData?> {
    val dto = LoginDTO(username, password)
    val response = client().post("/api/login") {
        contentType(ContentType.Application.Json)
        setBody(dto)
    }
    val body = if (response.status == HttpStatusCode.OK) {
        Json.decodeFromString(TokenData.serializer(), response.bodyAsText())
    } else {
        System.err.println(response.bodyAsText())
        null
    }
    return response.status to body
}

suspend fun ApplicationTestBuilder.loginBlocking(
    username: String,
    password: String,
): Pair<HttpStatusCode, TokenData?> {
    val dto = LoginDTO(username, password)
    val response = runBlocking {
         client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
    }
    val body = if (response.status == HttpStatusCode.OK) {
        Json.decodeFromString(TokenData.serializer(), response.bodyAsText())
    } else {
        System.err.println(response.bodyAsText())
        null
    }
    return response.status to body
}

fun FormBuilder.appendFile(path: String, name: String, type: String, key: String = "file") = append(
    key,
    this::class.java.getResource(path)!!.readBytes(),
    Headers.build {
        append(HttpHeaders.ContentType, type)
        append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
    }
)