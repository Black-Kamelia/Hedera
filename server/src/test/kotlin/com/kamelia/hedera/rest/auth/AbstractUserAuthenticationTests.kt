package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.rest.user.PasswordResetService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUserAuthenticationTests(
    user: TestUser,
    private val expectedResults: UserAuthenticationExpectedResults,
    private val input: UserAuthenticationTestsInput,
) : AbstractAuthenticationTests(user, expectedResults, input) {

    @DisplayName("Log in")
    @Test
    fun login() = testApplication {
        val (username, password) = input.login

        val response = client().post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO(username, password))
        }
        assertEquals(expectedResults.login, response.status)

        if (response.status == HttpStatusCode.Created) {
            val responseDto = Json.decodeFromString<SessionOpeningDTO>(response.bodyAsText())
            assertEquals(username, responseDto.user.username)
        }
    }

    /*
    @DisplayName("Request reset password link")
    @Test
    fun requestResetPasswordLink() = testApplication {
        val response = client().post("/api/request-reset-password") {
            contentType(ContentType.Application.Json)
            setBody(ResetPasswordRequestDTO(input.resetPassword))
        }
        assertEquals(expectedResults.login, response.status)
    }
     */
}
