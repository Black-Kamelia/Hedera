package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.authTestApplication
import com.kamelia.hedera.client
import com.kamelia.hedera.core.auth.Session
import com.kamelia.hedera.core.auth.SessionManager
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.user.UserUpdateDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Sessions edge cases tests")
class SessionsEdgeCasesTests {

    @DisplayName("Authenticate using invalid access token")
    @Test
    fun authenticateUsingInvalidAccessToken() = testApplication {
        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            bearerAuth("invalidToken")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Authenticate using expired access token")
    @Test
    fun authenticateUsingExpiredAccessToken() = authTestApplication {
        val (_, tokens) = login("sessions.user", "password")
        delay(1050)

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Authenticate using stale access token")
    @Test
    fun authenticateUsingStaleAccessToken() = authTestApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val refreshResponse = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)

        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refresh using invalid refresh token")
    @Test
    fun refreshUsingInvalidAccessToken() = testApplication {
        val response = client().post("/api/refresh") {
            bearerAuth("invalidToken")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refresh using expired refresh token")
    @Test
    fun refreshUsingExpiredAccessToken() = authTestApplication {
        val (_, tokens) = login("sessions.user", "password")
        delay(2050)

        val response = client().post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refresh using stale refresh token")
    @Test
    fun refreshUsingStaleAccessToken() = authTestApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val refreshResponse = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)

        val response = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refresh gives new working tokens")
    @Test
    fun refreshGivesNewWorkingTokens() = testApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val refreshResponse = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)
        val session = Json.decodeFromString<Session>(refreshResponse.bodyAsText())

        val response1 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            bearerAuth(session.accessToken.token)
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        val response2 = client.post("/api/refresh") {
            bearerAuth(session.refreshToken.token)
        }
        assertEquals(HttpStatusCode.Created, response2.status)
    }

    @DisplayName("Refresh gives different tokens")
    @Test
    fun refreshGivesDifferentTokens() = testApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val refreshResponse = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)
        val session = Json.decodeFromString<Session>(refreshResponse.bodyAsText())

        assertNotEquals(tokens?.accessToken, session.accessToken.token)
        assertNotEquals(tokens?.refreshToken, session.refreshToken.token)
    }

    @DisplayName("Refresh invalidates old tokens")
    @Test
    fun refreshInvalidatesOldTokens() = testApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val refreshResponse = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)

        val response1 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Log out all sessions invalidates access tokens")
    @Test
    fun logOutAllSessionsInvalidatesAccessTokens() = testApplication {
        val (_, tokens1) = login("sessions.user", "password")
        val (_, tokens2) = login("sessions.user", "password")
        val client = client()

        val logoutResponse = client.post("/api/logout/all") {
            tokens1?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NoContent, logoutResponse.status)

        val response1 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens1?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens2?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Log out all sessions invalidates refresh tokens")
    @Test
    fun logOutAllSessionsInvalidatesRefreshTokens() = testApplication {
        val (_, tokens1) = login("sessions.user", "password")
        val (_, tokens2) = login("sessions.user", "password")
        val client = client()

        val logoutResponse = client.post("/api/logout/all") {
            tokens1?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NoContent, logoutResponse.status)

        val response1 = client.post("/api/refresh") {
            tokens1?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client.post("/api/refresh") {
            tokens2?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Log out session invalidates its access token")
    @Test
    fun logOutSessionInvalidatesAccessToken() = testApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val logoutResponse = client.post("/api/logout") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NoContent, logoutResponse.status)

        val response1 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)
    }

    @DisplayName("Log out session invalidates its refresh token")
    @Test
    fun logOutSessionInvalidatesRefreshToken() = testApplication {
        val (_, tokens) = login("sessions.user", "password")
        val client = client()

        val logoutResponse = client.post("/api/logout") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NoContent, logoutResponse.status)

        val response1 = client.post("/api/refresh") {
            tokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)
    }

    @DisplayName("Disabling user logs them out")
    @Test
    fun disablingUserLogsThemOut() = testApplication {
        val (_, tokens) = login("authentication.owner", "password")
        val (_, userTokens) = login("sessions.disable.user", "password")
        val client = client()

        val response1 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        val postResponse = client.post("/api/users/00000000-000a-0000-0001-000000000003/deactivate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, postResponse.status)

        val response2 = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Updating user updates session accordingly")
    @Test
    fun updatingUserUpdatesSessionAccordingly() = testApplication {
        val (_, tokens) = login("authentication.owner", "password")
        val (_, userTokens) = login("sessions.update.user", "password")
        val client = client()

        val session1 = SessionManager.verify(userTokens!!.accessToken)!!
        assertNotEquals("modified.email@test.local", session1.email)

        val patchResponse = client.patch("/api/users/00000000-000a-0000-0001-000000000004") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(
                email = "modified.email@test.local"
            ))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, patchResponse.status)

        val session2 = SessionManager.verify(userTokens.accessToken)!!
        assertEquals("modified.email@test.local", session2.email)
    }
}