package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.core.TokenData
import com.kamelia.jellyfish.login
import com.kamelia.jellyfish.loginBlocking
import com.kamelia.jellyfish.rest.auth.SessionManager
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.UserUpdateDTO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthTest {

    @DisplayName("Logging in with incorrect username")
    @Test
    fun loggingInIncorrectUsername() = testApplication {
        val (status, _) = login("wrongUser", "password")
        assertEquals(HttpStatusCode.Unauthorized, status)
    }

    @DisplayName("Logging in with incorrect password")
    @Test
    fun loggingInIncorrectPassword() = testApplication {
        val (status, _) = login("user1", "wrongPassword")
        assertEquals(HttpStatusCode.Unauthorized, status)
    }

    @DisplayName("Performing protected request with valid access token")
    @Test
    fun useValidAccessToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)

        val response = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @DisplayName("Performing protected request with expired access token")
    @Test
    fun useExpiredAccessToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.OK, status)

        delay(2000L)

        val response = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refreshing session within expiration time")
    @Test
    fun refreshSessionWithinTime() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.OK, status)

        delay(500L)

        val response = client().patch("/api/login") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @DisplayName("Refreshing session after expiration time")
    @Test
    fun refreshSessionAfterTime() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.OK, status)

        delay(3000L)

        val response = client().patch("/api/login") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refreshing session gives different tokens")
    @Test
    fun refreshSessionGivesDifferentToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.OK, status)

        val response = client().patch("/api/login") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val newTokens = Json.decodeFromString(TokenData.serializer(), response.bodyAsText())

        assertNotEquals(tokens.accessToken, newTokens.accessToken)
        assertNotEquals(tokens.refreshToken, newTokens.refreshToken)
    }

    @DisplayName("Refreshing session gives working new tokens")
    @Test
    fun refreshSessionGivesWorkingTokens() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.OK, status)

        val response = client().patch("/api/login") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val testResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, testResponse.status)
    }

    @DisplayName("Logging out invalidates access token")
    @Test
    fun logOutInvalidateAccessToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.OK, status)

        val preLogoutResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, preLogoutResponse.status)

        val response = client().delete("/api/login") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val postLogoutResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, postLogoutResponse.status)
    }

    @DisplayName("Logging out invalidates refresh token")
    @Test
    fun logOutInvalidateRefreshToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.OK, status)

        val response = client().delete("/api/login") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val refreshResponse = client().patch("/api/login") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, refreshResponse.status)
    }

    @DisplayName("Logging out all invalidates every access token")
    @Test
    fun logOutAllInvalidatesEveryAccessToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (_, tokens1) = loginBlocking("user1", "password")
        check(tokens1 != null) { "Tokens should not be null" }
        val (_, tokens2) = loginBlocking("user1", "password")
        check(tokens2 != null) { "Tokens should not be null" }

        val response = client().delete("/api/login/all") {
            bearerAuth(tokens1.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val response1 = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens1.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens2.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Logging out all invalidates every refresh token")
    @Test
    fun logOutAllInvalidatesEveryRefreshToken() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (_, tokens1) = loginBlocking("user1", "password")
        check(tokens1 != null) { "Tokens should not be null" }
        val (_, tokens2) = loginBlocking("user1", "password")
        check(tokens2 != null) { "Tokens should not be null" }

        val response = client().delete("/api/login/all") {
            bearerAuth(tokens1.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val response1 = client().patch("/api/login") {
            bearerAuth(tokens1.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client().patch("/api/login") {
            bearerAuth(tokens2.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Session updates accordingly to user")
    @Test
    fun sessionUpdatesAccordinglyToUser() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (status, tokens) = login("auth_update_user", "password")
        assertEquals(HttpStatusCode.OK, status)

        val response = client().patch("/api/users/00000000-0001-0001-0000-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = "newUsername"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val userState = SessionManager.verify(tokens!!.accessToken) ?: fail("User state should not be null")
        assertEquals("newUsername", userState.username)
    }

    @DisplayName("Session updates role when promoting user")
    @Test
    fun sessionUpdatesRoleAtPromotion() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (ownerStatus, ownerTokens) = login("test-auth-2-owner", "password")
        assertEquals(HttpStatusCode.OK, ownerStatus)
        val (userStatus, userTokens) = login("test-auth-2-user", "password")
        assertEquals(HttpStatusCode.OK, userStatus)

        val response1 = client().get("/api/users/00000000-0000-0000-0000-000000000001") {
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response1.status)

        val response2 = client().patch("/api/users/00000000-0001-0002-0000-000000000002") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = UserRole.ADMIN))
            ownerTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response2.status)

        val response3 = client().get("/api/users/00000000-0000-0000-0000-000000000001") {
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response3.status)
    }

    @DisplayName("Session updates role when demoting user")
    @Test
    fun sessionUpdatesRoleAtDemotion() = testApplication {
        environment {
            config = ApplicationConfig("application-auth-test.conf")
        }

        val (ownerStatus, ownerTokens) = login("test-auth-3-owner", "password")
        assertEquals(HttpStatusCode.OK, ownerStatus)
        val (adminStatus, adminTokens) = login("test-auth-3-admin", "password")
        assertEquals(HttpStatusCode.OK, adminStatus)

        val response1 = client().get("/api/users/00000000-0000-0000-0000-000000000001") {
            adminTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        val response2 = client().patch("/api/users/00000000-0001-0003-0000-000000000002") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = UserRole.REGULAR))
            ownerTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response2.status)

        val response3 = client().get("/api/users/00000000-0000-0000-0000-000000000001") {
            adminTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response3.status)
    }
}
