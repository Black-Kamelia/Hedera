package com.kamelia.hedera.rest2

import com.kamelia.hedera.authTestApplication
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.login
import com.kamelia.hedera.loginBlocking
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest.user.UserUpdateDTO
import com.kamelia.hedera.util.Environment
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthTest {

    @DisplayName("Logging in with incorrect username")
    @Test
    fun loggingInIncorrectUsername() = testApplication {
        val (response, _) = login("wrongUser", "password")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Logging in with incorrect password")
    @Test
    fun loggingInIncorrectPassword() = testApplication {
        val startTime = System.currentTimeMillis()
        val (response, _) = login("user1", "wrongPassword")
        val endTime = System.currentTimeMillis()
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(endTime - startTime >= Environment.loginThrottle, "Login throttle not respected")

        val error = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Auth.INVALID_CREDENTIALS, error.title.key)
    }

    @DisplayName("Logging in with disabled user")
    @Test
    fun loggingInDisabledUser() = testApplication {
        val (response, _) = login("userDisabled", "password")
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val error = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Auth.ACCOUNT_DISABLED, error.title.key)
    }

    @DisplayName("Performing protected request with valid access token")
    @Test
    fun useValidAccessToken() = testApplication {
        val (loginResponse, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @DisplayName("Performing protected request with expired access token")
    @Test
    fun useExpiredAccessToken() = authTestApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        delay(2000L)

        val response = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refreshing session within expiration time")
    @Test
    fun refreshSessionWithinTime() = authTestApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        delay(500L)

        val response = client().post("/api/refresh") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @DisplayName("Refreshing session after expiration time")
    @Test
    fun refreshSessionAfterTime() = authTestApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        delay(3000L)

        val response = client().post("/api/refresh") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @DisplayName("Refreshing session gives different tokens")
    @Test
    fun refreshSessionGivesDifferentToken() = testApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().post("/api/refresh") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val newTokens = Json.decodeFromString(TokenData.serializer(), response.bodyAsText())

        assertNotEquals(tokens.accessToken, newTokens.accessToken)
        assertNotEquals(tokens.refreshToken, newTokens.refreshToken)
    }

    @DisplayName("Refreshing session gives working new tokens")
    @Test
    fun refreshSessionGivesWorkingTokens() = testApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().post("/api/refresh") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val newTokens = Json.decodeFromString<TokenData>(response.bodyAsText())

        val testResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(newTokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, testResponse.status)
    }

    @DisplayName("Refreshing session invalidates old tokens")
    @Test
    fun refreshSessionInvalidatesOldTokens() = testApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().post("/api/refresh") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val testResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, testResponse.status)
    }

    @DisplayName("Logging out invalidates access token")
    @Test
    fun logOutInvalidateAccessToken() = testApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val preLogoutResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.OK, preLogoutResponse.status)

        val response = client().post("/api/logout") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.NoContent, response.status)

        val postLogoutResponse = client().get("/api/users/00000000-0000-0000-0000-000000000003") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, postLogoutResponse.status)
    }

    @DisplayName("Logging out invalidates refresh token")
    @Test
    fun logOutInvalidateRefreshToken() = testApplication {
        val (loginResponse, tokens) = loginBlocking("user1", "password")
        check(tokens != null) { "Tokens should not be null" }
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().post("/api/logout") {
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.NoContent, response.status)

        val refreshResponse = client().post("/api/refresh") {
            bearerAuth(tokens.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, refreshResponse.status)
    }

    @DisplayName("Logging out all invalidates every access token")
    @Test
    fun logOutAllInvalidatesEveryAccessToken() = testApplication {
        val (_, tokens1) = loginBlocking("user1", "password")
        check(tokens1 != null) { "Tokens should not be null" }
        val (_, tokens2) = loginBlocking("user1", "password")
        check(tokens2 != null) { "Tokens should not be null" }

        val response = client().post("/api/logout/all") {
            bearerAuth(tokens1.accessToken)
        }
        assertEquals(HttpStatusCode.NoContent, response.status)

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
        val (_, tokens1) = loginBlocking("user1", "password")
        check(tokens1 != null) { "Tokens should not be null" }
        val (_, tokens2) = loginBlocking("user1", "password")
        check(tokens2 != null) { "Tokens should not be null" }

        val response = client().post("/api/logout/all") {
            bearerAuth(tokens1.accessToken)
        }
        assertEquals(HttpStatusCode.NoContent, response.status)

        val response1 = client().post("/api/refresh") {
            bearerAuth(tokens1.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response1.status)

        val response2 = client().post("/api/refresh") {
            bearerAuth(tokens2.refreshToken)
        }
        assertEquals(HttpStatusCode.Unauthorized, response2.status)
    }

    @DisplayName("Session updates accordingly to user")
    @Test
    fun sessionUpdatesAccordinglyToUser() = testApplication {
        val (loginResponse, tokens) = login("auth_update_user", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)

        val response = client().patch("/api/users/00000000-0001-0001-0000-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = "new_username"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val userState = SessionManager.verify(tokens!!.accessToken) ?: fail("User state should not be null")
        assertEquals("new_username", userState.user.username)
    }

    @DisplayName("Session updates role when promoting user")
    @Test
    fun sessionUpdatesRoleAtPromotion() = testApplication {
        val (ownerLoginResponse, ownerTokens) = login("test-auth-2-owner", "password")
        assertEquals(HttpStatusCode.Created, ownerLoginResponse.status)
        val (userLoginResponse, userTokens) = login("test-auth-2-user", "password")
        assertEquals(HttpStatusCode.Created, userLoginResponse.status)

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
        val (ownerLoginResponse, ownerTokens) = login("test-auth-3-owner", "password")
        assertEquals(HttpStatusCode.Created, ownerLoginResponse.status)
        val (adminLoginResponse, adminTokens) = login("test-auth-3-admin", "password")
        assertEquals(HttpStatusCode.Created, adminLoginResponse.status)

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

    @DisplayName("Disabling a user logs them out")
    @Test
    fun disablingUserLogsThemOut() = testApplication {
        val (ownerLoginResponse, ownerTokens) = loginBlocking("test-auth-4-owner", "password")
        assertEquals(HttpStatusCode.Created, ownerLoginResponse.status)
        val (userLoginResponse, userTokens) = loginBlocking("test-auth-4-regular", "password")
        assertEquals(HttpStatusCode.Created, userLoginResponse.status)

        /* Perform a request to ensure the user is logged in */
        val response = client().get("/api/users/00000000-0001-0004-0000-000000000002") {
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        /* Disabling the user as owner */
        val updateResponse = client().post("/api/users/00000000-0001-0004-0000-000000000002/deactivate") {
            ownerTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        /* The session should be expired */
        val testResponse = client().get("/api/users/00000000-0001-0004-0000-000000000002") {
            userTokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, testResponse.status)

        /* The refresh token should be expired */
        val refreshResponse = client().post("/api/refresh") {
            userTokens?.let { bearerAuth(it.refreshToken) }
        }
        assertEquals(HttpStatusCode.Unauthorized, refreshResponse.status)
    }

}
