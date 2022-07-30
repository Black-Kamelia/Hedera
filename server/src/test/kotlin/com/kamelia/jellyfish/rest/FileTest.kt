package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.appendFile
import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.login
import com.kamelia.jellyfish.rest.core.pageable.FilterObject
import com.kamelia.jellyfish.rest.core.pageable.PageDefinitionDTO
import com.kamelia.jellyfish.rest.core.pageable.SortDirection
import com.kamelia.jellyfish.rest.core.pageable.SortObject
import com.kamelia.jellyfish.rest.file.FilePageDTO
import com.kamelia.jellyfish.rest.file.FileRepresentationDTO
import com.kamelia.jellyfish.rest.file.FileUpdateDTO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

private lateinit var code: String

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FileTest {

    @Test
    @Order(1)
    fun `Upload image`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), responseDto.ownerId)
        assertEquals("text/plain", responseDto.mimeType)
        code = responseDto.code
    }

    @Test
    @Order(2)
    fun `Edit file`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.patch("/api/files/00000000-0000-0000-0000-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(3)
    fun `Check edited file changed name`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.get("/api/files/paged") {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        val bar = responseDto
            .page
            .items
            .firstOrNull {
                it.id == UUID.fromString("00000000-0000-0000-0000-000000000001")
            }?.name
        assertEquals("bar.txt", bar)
    }

    @Test
    @Order(4)
    fun `Delete file`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.delete("/api/files/00000000-0000-0000-0000-000000000000") {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(5)
    fun `Download file`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.get("/api/files/$code") {
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(this::class.java.getResource("/test_files/test.txt")!!.readText(), response.bodyAsText())
    }

    @Test
    @Order(6)
    fun `Filter files by name and sort by size descending`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.get("/api/files/paged") {
            contentType(ContentType.Application.Json)
            setBody(
                PageDefinitionDTO(
                    filters = listOf(
                        listOf(
                            FilterObject(
                                field = "name",
                                operator = "like",
                                value = "%test%"
                            )
                        )
                    ),
                    sorter = listOf(
                        SortObject(
                            field = "size",
                            direction = SortDirection.DESC
                        )
                    )
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        assertEquals(2, responseDto.page.items.size)
        assertTrue(responseDto.page.items.all { it.name.contains("test") })
        assertEquals("test.pdf", responseDto.page.items[0].name)
        assertEquals("test.txt", responseDto.page.items[1].name)
    }

    @Test
    @Order(7)
    fun `Filter files by name and type`() = testApplication {
        val (status, tokens) = login("user1", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.get("/api/files/paged") {
            contentType(ContentType.Application.Json)
            setBody(
                PageDefinitionDTO(
                    filters = listOf(
                        listOf(
                            FilterObject(
                                field = "name",
                                operator = "like",
                                value = "%test%"
                            )
                        ),
                        listOf(
                            FilterObject(
                                field = "mime_type",
                                operator = "eq",
                                value = "application/pdf"
                            )
                        )
                    )
                )
            )
            bearerAuth(tokens!!.token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        assertEquals(1, responseDto.page.items.size)
        assertTrue(responseDto.page.items.all { it.name == "test.pdf" })
    }
}
