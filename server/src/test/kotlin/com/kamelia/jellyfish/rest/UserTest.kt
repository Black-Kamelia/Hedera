package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.login
import com.kamelia.jellyfish.rest.user.UserDTO
import com.kamelia.jellyfish.rest.user.UserPasswordUpdateDTO
import com.kamelia.jellyfish.rest.user.UserRepresentationDTO
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.UserUpdateDTO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserTest {

    @Test
    @Order(1)
    fun `Sign up`() = testApplication {
        val newUserDto = UserDTO(
            username = "test",
            password = "Test0@aaa",
            email = "test@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(newUserDto.username, responseDto.username)
        assertEquals(newUserDto.email, responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)

        client.delete("/api/users/${responseDto.id}")
    }

    @Test
    @Order(2)
    fun `Sign up with existing email`() = testApplication {
        val dto = UserDTO(
            username = "test",
            password = "Test0@aaa",
            email = "admin@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    @Order(3)
    fun `Sign up with existing username`() = testApplication {
        val dto = UserDTO(
            username = "admin",
            password = "Test0@aaa",
            email = "test@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    @Order(4)
    fun `Update username`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    username = "newUsername"
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), responseDto.id)
        assertEquals("newUsername", responseDto.username)
        assertEquals("user1@test.com", responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)
        assertTrue { responseDto.enabled }
    }

    @Test
    @Order(5)
    fun `Update email address`() = testApplication {
        val (status, tokens) = login("user2", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000002") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    email = "newEmail@test.com"
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), responseDto.id)
        assertEquals("user2", responseDto.username)
        assertEquals("newEmail@test.com", responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)
        assertTrue { responseDto.enabled }
    }

    @Test
    @Order(6)
    fun `Update password with correct old password`() = testApplication {
        val (status, tokens) = login("newUsername", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000001/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "password",
                    "pwdSecure100$"
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())
    }

    @Test
    @Order(7)
    fun `Update password with wrong old password`() = testApplication {
        val (status, tokens) = login("user2", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000002/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "wrongPassword",
                    "newPassword"
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    @Order(8)
    fun `Update unknown user`() = testApplication {
        val (status, tokens) = login("admin", "admin")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-00000000000f") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    email = "newEmail@test.com"
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status, response.bodyAsText())
    }

    @Test
    @Order(9)
    fun `Delete existing user`() = testApplication {
        val (status, tokens) = login("admin", "admin")
        println(Hasher.hash("admin"))
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.delete("/api/users/00000000-0000-0000-0000-000000000000") {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(10)
    fun `Delete unknown user`() = testApplication {
        val (status, tokens) = login("admin", "admin")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.delete("/api/users/ffffffff-ffff-ffff-ffff-ffffffffffff") {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
