package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.appendFile
import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.core.TokenPair
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
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

typealias TestUser = Pair<TokenPair?, UUID>

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileTest {

    companion object {

        private val fileCodes: MutableMap<UUID, String> = mutableMapOf()

        private lateinit var user1: TestUser
        private lateinit var user2: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                user1 = Pair(
                    login("user1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
                )
                user2 = Pair(
                    login("user2", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000002")
                )
            }
        }

        @JvmStatic
        fun `Upload file`(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("regular user 1", user1), HttpStatusCode.OK),
            Arguments.of(Named.of("regular user 2", user2), HttpStatusCode.OK),
            Arguments.of(Named.of("a guest", guest), HttpStatusCode.Unauthorized),
        )

        @JvmStatic
        fun `Edit file`(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("the file owner", user1),
                UUID.fromString("00000000-0000-0000-0002-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                UUID.fromString("00000000-0000-0000-0002-000000000002"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("a guest", guest),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun `Delete file`(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("the file owner", user1),
                UUID.fromString("00000000-0000-0000-0004-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                UUID.fromString("00000000-0000-0000-0004-000000000002"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("a guest", guest),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun `Download private file`(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("the file owner", user1),
                fileCodes[user1.second],
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                fileCodes[user1.second],
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("a guest", guest),
                fileCodes[user1.second],
                HttpStatusCode.NotFound
            ),
        )
    }

    @ParameterizedTest(name = "Uploading a file as {0} is {1}")
    @MethodSource
    @Order(1)
    fun `Upload file`(user: TestUser, statusCode: HttpStatusCode) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(userId, responseDto.ownerId)
            assertEquals("text/plain", responseDto.mimeType)
            fileCodes[userId] = responseDto.code
        }
    }

    @ParameterizedTest(name = "Editing a file as {0} is {2}")
    @MethodSource
    @Order(2)
    fun `Edit file`(user: TestUser, fileId: UUID, statusCode: HttpStatusCode) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/files/$fileId") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(statusCode, response.status)
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

    @ParameterizedTest(name = "Deleting a file as {0} is {2}")
    @MethodSource
    @Order(4)
    fun `Delete file`(user: TestUser, fileId: UUID, statusCode: HttpStatusCode) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/files/$fileId") {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(statusCode, response.status)
    }

    @ParameterizedTest(name = "Downloading a private file as {0} is {2}")
    @MethodSource
    @Order(5)
    fun `Download private file`(user: TestUser, fileCode: String, statusCode: HttpStatusCode) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/files/$fileCode") {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            assertEquals(this::class.java.getResource("/test_files/test.txt")!!.readText(), response.bodyAsText())
        }
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
