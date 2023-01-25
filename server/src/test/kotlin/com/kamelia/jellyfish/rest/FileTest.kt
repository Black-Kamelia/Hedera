package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.TestUser
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
import com.kamelia.jellyfish.rest.file.FileVisibility
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.stream.Stream
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContains
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileTest {

    private lateinit var testFolder: File

    @BeforeAll
    fun initTests() {
        val testFolderPath = Path.of("_tests")
        testFolder = if (Files.exists(testFolderPath)) {
            testFolderPath.toFile()
        } else {
            Files.createDirectory(testFolderPath).toFile()
        }

        val uploadFolder = File(
            FileTest::class.java.getResource("/upload")?.toURI()
                ?: throw Exception("Missing resource folder")
        )

        uploadFolder.copyRecursively(testFolder.resolve("upload"), true)
    }

    @AfterAll
    fun cleanUp() {
        testFolder.deleteRecursively()
    }

    @DisplayName("Uploading a file")
    @ParameterizedTest(name = "Uploading a file as {0} is {1}")
    @MethodSource
    fun uploadFile(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
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
        }
    }

    @DisplayName("Uploading a file with token")
    @ParameterizedTest(name = "Uploading a file with token as {1} is {2}")
    @MethodSource
    fun uploadFileToken(
        token: String,
        userId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", token)
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(userId, responseDto.ownerId)
            assertEquals("text/plain", responseDto.mimeType)
        }
    }

    @DisplayName("Uploading a file with incorrect content type")
    @Test
    fun uploadFileWithIncorrectContentType() = testApplication {
        val (_, tokens) = login("user1", "password")
        val client = client()
        val response = client.submitForm("/api/files/upload") {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val errors = Json.decodeFromString<List<String>>(response.bodyAsText())
        assertContains(errors, "errors.headers.missing.`content-type`")
    }

    @DisplayName("Uploading a file with no file")
    @Test
    fun uploadFileWithNoFile() = testApplication {
        val (_, tokens) = login("user1", "password")
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {}) {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val errors = Json.decodeFromString<List<String>>(response.bodyAsText())
        assertContains(errors, "errors.uploads.missing_file")
    }

    @DisplayName("Uploading a file with an empty name")
    @Test
    fun uploadFileWithEmptyName() = testApplication {
        val (_, tokens) = login("user1", "password")
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "", "text/plain")
        }) {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val errors = Json.decodeFromString<List<String>>(response.bodyAsText())
        assertContains(errors, "errors.file.name.empty")
    }

    @DisplayName("Downloading a file")
    @ParameterizedTest(name = "Downloading {1} file as {0} is {3}")
    @MethodSource("downloadPrivateFile", "downloadUnlistedFile", "downloadPublicFile")
    fun downloadFile(
        user: TestUser,
        fileVisibility: String,
        fileCode: String,
        statusCode: HttpStatusCode,
    ) = testApplication {
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

    @DisplayName("Editing a file")
    @ParameterizedTest(name = "Editing {1} file as {0} is {3}")
    @MethodSource("editPrivateFile", "editUnlistedFile", "editPublicFile")
    fun editFile(
        user: TestUser,
        fileVisibility: String,
        fileId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
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
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals("bar.txt", responseDto.name)
        }
    }

    @DisplayName("Deleting a file")
    @ParameterizedTest(name = "Deleting {1} file as {0} is {3}")
    @MethodSource("deletePrivateFile", "deleteUnlistedFile", "deletePublicFile")
    fun deleteFile(
        user: TestUser,
        fileVisibility: String,
        fileId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/files/$fileId") {
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(statusCode, response.status)
    }

    @DisplayName("Filtering files by name and sort by size descending")
    @Test
    fun filesFiltering1() = testApplication {
        val (tokens, _) = user1
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
                                value = "%filtering1%"
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
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        assertEquals(2, responseDto.page.items.size)
        assertTrue(responseDto.page.items.all { it.name.contains("filtering1") })
        assertEquals("filtering1_2.png", responseDto.page.items[0].name)
        assertEquals("filtering1_1.png", responseDto.page.items[1].name)
    }

    @DisplayName("Filtering files by name and type")
    @Test
    fun filesFiltering2() = testApplication {
        val (tokens, _) = user1
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
                                value = "%filtering2%"
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
            tokens?.let {
                bearerAuth(it.token)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        assertEquals(1, responseDto.page.items.size)
        assertTrue(responseDto.page.items.all { it.name.contains("filtering2") })
        assertEquals("filtering2_2.pdf", responseDto.page.items[0].name)
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user1: TestUser
        private lateinit var user2: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                superadmin = Pair(
                    login("owner", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000002")
                )
                user1 = Pair(
                    login("user1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000003")
                )
                user2 = Pair(
                    login("user2", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000004")
                )
            }
        }

        @JvmStatic
        fun uploadFile(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("superadmin", superadmin), HttpStatusCode.OK),
            Arguments.of(Named.of("admin", admin), HttpStatusCode.OK),
            Arguments.of(Named.of("regular user", user1), HttpStatusCode.OK),
            Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
        )

        @JvmStatic
        fun uploadFileToken(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "0f2577e20ca8466b89724d2cfb56e2db",
                Named.of("superadmin", UUID.fromString("00000000-0000-0000-0000-000000000001")),
                HttpStatusCode.OK
            ),
            Arguments.of(
                "a9b42b75a4774e41b6391e7724c05f77",
                Named.of("admin", UUID.fromString("00000000-0000-0000-0000-000000000002")),
                HttpStatusCode.OK
            ),
            Arguments.of(
                "8da63c40d5534a50b69e44f4b6789712",
                Named.of("regular user", UUID.fromString("00000000-0000-0000-0000-000000000003")),
                HttpStatusCode.OK
            ),
            Arguments.of(
                "00000000000000000000000000000000",
                Named.of("guest", UUID.fromString("00000000-0000-0000-0000-000000000000")),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun editPrivateFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0001-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0001-000000000002"),
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0001-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0001-000000000004"),
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0001-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun editUnlistedFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0002-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0002-000000000002"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0002-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0002-000000000004"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0002-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun editPublicFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0003-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0003-000000000002"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0003-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0003-000000000004"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0003-0003-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun downloadPrivateFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PRIVATE.toString().lowercase(),
                "0002_01_03",
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PRIVATE.toString().lowercase(),
                "0002_01_03",
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PRIVATE.toString().lowercase(),
                "0002_01_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PRIVATE.toString().lowercase(),
                "0002_01_03",
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PRIVATE.toString().lowercase(),
                "0002_01_03",
                HttpStatusCode.NotFound
            ),
        )

        @JvmStatic
        fun downloadUnlistedFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.UNLISTED.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.UNLISTED.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.UNLISTED.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.UNLISTED.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.UNLISTED.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
        )

        @JvmStatic
        fun downloadPublicFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PUBLIC.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PUBLIC.toString().lowercase(),
                "0002_02_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PUBLIC.toString().lowercase(),
                "0002_03_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PUBLIC.toString().lowercase(),
                "0002_03_03",
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PUBLIC.toString().lowercase(),
                "0002_03_03",
                HttpStatusCode.OK
            ),
        )

        @JvmStatic
        fun deletePrivateFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0001-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0001-000000000002"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0001-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0001-000000000004"),
                HttpStatusCode.NotFound
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PRIVATE.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0001-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun deleteUnlistedFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0002-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0002-000000000002"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0002-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0002-000000000004"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.UNLISTED.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0002-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun deletePublicFile(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadmin),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0003-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", admin),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0003-000000000002"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("file owner", user1),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0003-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("another user", user2),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0003-000000000004"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("guest", guest),
                FileVisibility.PUBLIC.toString().lowercase(),
                UUID.fromString("00000000-0000-0004-0003-000000000005"),
                HttpStatusCode.Unauthorized
            ),
        )
    }
}
