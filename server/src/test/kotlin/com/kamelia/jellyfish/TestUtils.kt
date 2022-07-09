package com.kamelia.jellyfish

import com.kamelia.jellyfish.core.TokenPair
import com.kamelia.jellyfish.rest.auth.LoginDTO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json

fun ApplicationTestBuilder.client() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

suspend fun ApplicationTestBuilder.login(
    username: String,
    password: String,
): Pair<HttpStatusCode, TokenPair?> {
    val dto = LoginDTO(username, password)
    val response = client().post("/api/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(dto)
    }
    val body = if (response.status == HttpStatusCode.OK) {
        Json.decodeFromString(TokenPair.serializer(), response.bodyAsText())
    } else {
        System.err.println(response.bodyAsText())
        null
    }
    return response.status to body
}
