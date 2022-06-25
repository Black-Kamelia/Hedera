package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.rest.user.UserDTO
import com.kamelia.jellyfish.rest.user.UserPasswordUpdateDTO
import com.kamelia.jellyfish.rest.user.UserResponseDTO
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.UserUpdateDTO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.util.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun ApplicationTestBuilder.client() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

class UserTest {

    @Test
    fun `Sign up`() = testApplication {
        val newUserDto = UserDTO(
            username = "test",
            password = "test",
            email = "test@test.com"
        )
        val client = client()
        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseDto = Json.decodeFromString(UserResponseDTO.serializer(), response.bodyAsText())
        assertEquals(newUserDto.username, responseDto.username)
        assertEquals(newUserDto.email, responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)

        client.delete("/api/users/${responseDto.id}")
    }

    @Test
    fun `Sign up with existing email`() = testApplication {
        val dto = UserDTO(
            username = "test",
            password = "test",
            email = "admin@test.com"
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `Sign up with existing username`() = testApplication {
        val dto = UserDTO(
            username = "admin",
            password = "test",
            email = "test@test.com"
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `Update username`() = testApplication {
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    username = "newUsername"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(UserResponseDTO.serializer(), response.bodyAsText())
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), responseDto.id)
        assertEquals("newUsername", responseDto.username)
        assertEquals("user1@test.com", responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)
        assertTrue { responseDto.enabled }
    }

    @Test
    fun `Update email address`() = testApplication {
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000002") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    email = "newEmail@test.com"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(UserResponseDTO.serializer(), response.bodyAsText())
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), responseDto.id)
        assertEquals("user2", responseDto.username)
        assertEquals("newEmail@test.com", responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)
        assertTrue { responseDto.enabled }
    }

    @Test
    fun `Update password with correct old password`() = testApplication {
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000001/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "password",
                    "newPassword"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `Update password with wrong old password`() = testApplication {
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-000000000002/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "wrongPassword",
                    "newPassword"
                )
            )
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `Update unknown user`() = testApplication {
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-00000000000f") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    email = "newEmail@test.com"
                )
            )
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `Delete existing user`() = testApplication {
        val client = client()
        val response = client.delete("/api/users/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `Delete unknown user`() = testApplication {
        val client = client()
        val response = client.delete("/api/users/ffffffff-ffff-ffff-ffff-ffffffffffff")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
