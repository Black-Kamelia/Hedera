package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.core.pageable.FilterObject
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.file.FilePageDTO
import com.kamelia.hedera.rest.file.FileRepresentationDTO
import com.kamelia.hedera.rest.file.FileSizeDTO
import com.kamelia.hedera.rest.file.FileUpdateDTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.file.toSizeDTO
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

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
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(userId, responseDto.owner.id)
            assertEquals("test.txt", responseDto.name)
            assertEquals("text/plain", responseDto.mimeType)
            assertEquals(20L.toSizeDTO(), responseDto.size)
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
            assertEquals(userId, responseDto.owner.id)
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
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val error = Json.decodeFromString<MessageKeyDTO>(response.bodyAsText())
        assertEquals(error.key, Errors.Headers.MISSING_HEADER)
        assertEquals(error.parameters!!["header"], "content-type")
    }

    @DisplayName("Uploading a file with no file")
    @Test
    fun uploadFileWithNoFile() = testApplication {
        val (_, tokens) = login("user1", "password")
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {}) {
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val error = Json.decodeFromString<MessageKeyDTO>(response.bodyAsText())
        assertContains(error.key, Errors.Uploads.MISSING_FILE)
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
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
        val error = Json.decodeFromString<MessageKeyDTO>(response.bodyAsText())
        assertContains(error.key, Errors.Uploads.EMPTY_FILE_NAME)
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
                bearerAuth(it.accessToken)
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
                bearerAuth(it.accessToken)
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
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(statusCode, response.status)
    }

    @DisplayName("Filtering files by name and type")
    @Test
    fun filesFiltering2() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(
                PageDefinitionDTO(
                    filters = listOf(
                        listOf(
                            FilterObject(
                                field = "name",
                                operator = "fuzzy",
                                value = "filtering2"
                            )
                        ),
                        listOf(
                            FilterObject(
                                field = "mimeType",
                                operator = "eq",
                                value = "application/pdf"
                            )
                        )
                    )
                )
            )
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        assertEquals(1, responseDto.page.items.size)
        assertTrue(responseDto.page.items.all { it.name.contains("filtering2") })
        assertEquals("filtering2_2.pdf", responseDto.page.items[0].name)
    }

    @DisplayName("Filtering files")
    @ParameterizedTest(name = "Filtering files by {0} (operator: {1})")
    @MethodSource
    fun filteringFiles(
        field: String,
        operator: String,
        value: String,
        expectedResult: (FilePageDTO) -> Unit,
    ) = testApplication {
        val (tokens, _) = userFilters
        val client = client()

        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(filters = listOf(listOf(FilterObject(field, operator, value)))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseDto = Json.decodeFromString(FilePageDTO.serializer(), response.bodyAsText())
        expectedResult(responseDto)
    }

    @DisplayName("Filtering files on unknown field")
    @Test
    fun filteringFilesUnknownField() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(filters = listOf(listOf(FilterObject("unknown", "eq", "fail")))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val messageKeyDTO = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Filters.UNKNOWN_FIELD, messageKeyDTO.key)
    }

    @DisplayName("Filtering files with unknown operator")
    @Test
    fun filteringFilesUnknownOperator() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(filters = listOf(listOf(FilterObject("name", "unknown", "fail")))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val messageKeyDTO = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Filters.ILLEGAL_FILTER, messageKeyDTO.key)
    }

    @DisplayName("Filtering files with negative page number should fail")
    @Test
    fun filesFilteringNegativePage() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            parameter("page", "-1")
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseDto = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_NUMBER, responseDto.key)
    }

    @DisplayName("Filtering files with malformed page number should fail")
    @Test
    fun filesFilteringMalformedPage() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            parameter("page", "abc")
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseDto = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_NUMBER, responseDto.key)
    }

    @DisplayName("Filtering files with negative page size should fail")
    @Test
    fun filesFilteringNegativeSize() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            parameter("pageSize", "-1")
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseDto = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_SIZE, responseDto.key)
    }

    @DisplayName("Filtering files with malformed page size should fail")
    @Test
    fun filesFilteringMalformedSize() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            parameter("pageSize", "abc")
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseDto = Json.decodeFromString(MessageKeyDTO.serializer(), response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_SIZE, responseDto.key)
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user1: TestUser
        private lateinit var user2: TestUser
        private lateinit var userFilters: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                superadmin = Pair(
                    login("owner1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin1", "password").second ?: throw Exception("Login failed"),
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
                userFilters = Pair(
                    login("user_filters", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000010")
                )
            }
        }

        @JvmStatic
        fun uploadFile(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("superadmin", superadmin), HttpStatusCode.Created),
            Arguments.of(Named.of("admin", admin), HttpStatusCode.Created),
            Arguments.of(Named.of("regular user", user1), HttpStatusCode.Created),
            Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
        )

        @JvmStatic
        fun uploadFileToken(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "0f2577e20ca8466b89724d2cfb56e2db",
                Named.of("superadmin", UUID.fromString("00000000-0000-0000-0000-000000000001")),
                HttpStatusCode.Created
            ),
            Arguments.of(
                "a9b42b75a4774e41b6391e7724c05f77",
                Named.of("admin", UUID.fromString("00000000-0000-0000-0000-000000000002")),
                HttpStatusCode.Created
            ),
            Arguments.of(
                "8da63c40d5534a50b69e44f4b6789712",
                Named.of("regular user", UUID.fromString("00000000-0000-0000-0000-000000000003")),
                HttpStatusCode.Created
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

        @JvmStatic
        fun filteringFiles(): Stream<Arguments> {
            val date = Instant.parse("1970-01-05T00:00:00.00000Z")

            return Stream.of(
                Arguments.of("name", "like", "filtering1_1.png", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.name == "filtering1_1.png" })
                }),
                Arguments.of("name", "nlike", "filtering1_1.png", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.name != "filtering1_1.png" })
                }),
                Arguments.of("name", "fuzzy", "filtering", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.name.contains("filtering") })
                }),

                Arguments.of("mimeType", "like", "image/png", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.mimeType == "image/png" })
                }),
                Arguments.of("mimeType", "nlike", "image/png", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.mimeType != "image/png" })
                }),

                Arguments.of("size", "eq", "1000;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size == FileSizeDTO(1000.0, 0) })
                }),
                Arguments.of("size", "ne", "1000;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size != FileSizeDTO(1000.0, 0) })
                }),
                Arguments.of("size", "gt", "800;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size.value > 800 })
                }),
                Arguments.of("size", "lt", "800;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size.value < 800 })
                }),
                Arguments.of("size", "ge", "800;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size.value >= 800 })
                }),
                Arguments.of("size", "le", "800;0", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.size.value <= 800 })
                }),

                Arguments.of("createdAt", "eq", "1970-01-05T00:00:00.000000Z", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.equals(date)
                    })
                }),
                Arguments.of("createdAt", "ne", date.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.none {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.equals(date)
                    })
                }),
                Arguments.of("createdAt", "gt", date.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.isAfter(date)
                    })
                }),
                Arguments.of("createdAt", "lt", date.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.isBefore(date)
                    })
                }),
                Arguments.of("createdAt", "ge", date.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.isAfter(date) || createdAt.equals(date)
                    })
                }),
                Arguments.of("createdAt", "le", date.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all {
                        val createdAt = Instant.parse(it.createdAt)
                        createdAt.isBefore(date) || createdAt.equals(date)
                    })
                }),
            )
        }
    }
}
