package com.kamelia.hedera

import com.kamelia.hedera.rest.auth.LoginDTO
import com.kamelia.hedera.rest.auth.SessionOpeningDTO
import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.UUIDSerializer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.util.*
import java.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

data class TokenPair(val accessToken: String, val refreshToken: String)
typealias TestUser = Pair<TokenPair?, UUID>

fun ApplicationTestBuilder.client() = createClient {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                polymorphic(DTO::class) {
                    subclass(UserRepresentationDTO::class)
                }
                contextual(UUIDSerializer)
            }
            ignoreUnknownKeys = true
        })
    }
}

suspend fun ApplicationTestBuilder.login(
    username: String,
    password: String,
): Pair<HttpResponse, TokenPair?> {
    val dto = LoginDTO(username, password)
    val response = client().post("/api/login") {
        contentType(ContentType.Application.Json)
        setBody(dto)
    }
    val body = if (response.status == HttpStatusCode.Created) {
        Json.decodeFromString(SessionOpeningDTO.serializer(), response.bodyAsText())
    } else {
        System.err.println(response.bodyAsText())
        null
    }
    return response to body?.tokens?.let { TokenPair(it.accessToken.token, it.refreshToken.token) }
}

suspend fun ApplicationTestBuilder.loginBlocking(
    username: String,
    password: String,
): Pair<HttpResponse, TokenPair?> {
    val dto = LoginDTO(username, password)
    val response = runBlocking {
         client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
    }
    val body = if (response.status == HttpStatusCode.Created) {
        Json.decodeFromString(SessionOpeningDTO.serializer(), response.bodyAsText())
    } else {
        System.err.println(response.bodyAsText())
        null
    }
    return response to body?.tokens?.let { TokenPair(it.accessToken.token, it.refreshToken.token) }
}

fun FormBuilder.appendFile(path: String, name: String, type: String, key: String = "file") = append(
    key,
    this::class.java.getResource(path)!!.readBytes(),
    Headers.build {
        append(HttpHeaders.ContentType, type)
        append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
    }
)

@KtorDsl
fun authTestApplication(
    block: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    environment {
        config = ApplicationConfig("application-auth-test.yaml")
    }
    block()
}
