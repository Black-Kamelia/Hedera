package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.response.MessageDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AuthenticationEdgeCasesTests {

    @DisplayName("Login with no username")
    @Test
    fun loginWithNoUsername() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("", "password"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Login with no password")
    @Test
    fun loginWithNoPassword() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("user", ""))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Login with invalid username")
    @Test
    fun loginWithInvalidUsername() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("invalid_userNAME@test", "password"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Login with unknown user")
    @Test
    fun loginWithUnknownUser() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("unknown.user", "password"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Login with disabled user")
    @Test
    fun loginWithDisabledUser() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("authentication.user.disabled", "password"))
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Auth.ACCOUNT_DISABLED, responseDto.title.key)
    }

    @DisplayName("Login with incorrect password")
    @Test
    fun loginWithIncorrectPassword() = testApplication {
        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("authentication.owner", "incorrectpassword"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

}