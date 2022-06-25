package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.rest.user.UserDTO
import com.kamelia.jellyfish.rest.user.UserResponseDTO
import com.kamelia.jellyfish.rest.user.UserRole
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

fun ApplicationTestBuilder.client() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

class UserTest {

    @Test
    fun `Signing up`() = testApplication {
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
    fun `Signing up with existing email`() = testApplication {
        val dto = UserDTO(
            username = "test",
            password = "test",
            email = "admin@admin.com"
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `Signing up with existing username`() = testApplication {
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