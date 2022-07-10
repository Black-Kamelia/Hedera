package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.login
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthTest {

    @Test
    @Order(1)
    fun `Logging in with correct credentials`() = testApplication {
        val (status, _) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
    }

    @Test
    @Order(2)
    fun `Logging in with incorrect credentials`() = testApplication {
        val (status, _) = login("user1", "wrong")
        assertEquals(HttpStatusCode.Unauthorized, status)
    }

    @Test
    @Order(3)
    fun `Refreshing token`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val response = client().patch("/api/login") {
            bearerAuth(tokens!!.refreshToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
