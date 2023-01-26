package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.login
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthTest {

    @DisplayName("Logging in with correct credentials")
    @Test
    @Order(1)
    fun loggingIn() = testApplication {
        val (status, _) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
    }

    @DisplayName("Logging in with incorrect credentials")
    @Test
    @Order(2)
    fun loggingInIncorrect() = testApplication {
        val (status, _) = login("user1", "wrong")
        assertEquals(HttpStatusCode.Unauthorized, status)
    }

    @DisplayName("Refreshing token")
    @Test
    @Order(3)
    fun refreshToken() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val response = client().patch("/api/login") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
