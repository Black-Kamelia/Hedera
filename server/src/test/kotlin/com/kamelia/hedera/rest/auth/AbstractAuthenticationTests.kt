package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.loginBlocking
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractAuthenticationTests(
    private val user: TestUser,
    private val expectedResults: AuthenticationExpectedResults,
    private val input: AuthenticationTestsInput,
) {

    @DisplayName("Refresh session")
    @Test
    fun refreshSession() = testApplication {
        val (username, password) = input.login
        val (_, tokens) = loginBlocking(username, password)
        val client = client()

        val response = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(expectedResults.refreshSession, response.status)

        if (response.status == HttpStatusCode.Created) {
            val responseDto = Json.decodeFromString<Session>(response.bodyAsText())
            assertNotEquals(tokens?.refreshToken, responseDto.refreshToken)
            assertNotEquals(tokens?.accessToken, responseDto.accessToken)
        }

        val response2 = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Logout current session")
    @Test
    fun logout() = testApplication {
        val (username, password) = input.login
        val (_, tokens) = loginBlocking(username, password)
        val client = client()

        val response = client.post("/api/logout") {
            tokens?.let { bearerAuth(it.accessToken) }
        }

        assertEquals(expectedResults.logoutSession, response.status)

        val response2 = client.post("/api/logout") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Logout all sessions")
    @Test
    fun logoutAll() = testApplication {
        val (username, password) = input.login
        val (_, tokens1) = loginBlocking(username, password)
        val (_, tokens2) = loginBlocking(username, password)
        val client = client()

        val response = client.post("/api/logout/all") {
            tokens1?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.logoutAllSessions, response.status)

        val response2 = client.post("/api/logout") {
            tokens1?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
        val response3 = client.post("/api/logout") {
            tokens2?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response3.status)
    }
}
