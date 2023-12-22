package com.kamelia.hedera.rest2

import com.kamelia.hedera.GlobalConfigurationSetup
import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.core.pageable.FilterObject
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.core.pageable.SortDirection
import com.kamelia.hedera.rest.core.pageable.SortObject
import com.kamelia.hedera.rest.file.FilePageDTO
import com.kamelia.hedera.rest.file.FileRepresentationDTO
import com.kamelia.hedera.rest.file.FileUpdateDTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.user.UserRepresentationDTO
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
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(GlobalConfigurationSetup::class)
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
        testFolder.resolve("upload").deleteRecursively()
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
            assertEquals(20L, responseDto.size)
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

    @DisplayName("Uploading a file with deleted token")
    @Test
    fun uploadFileDeletedToken() = testApplication {
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "52713337ddd140e8adf8e9c10a5ccb12")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
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
        val error = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(error.title.key, Errors.Headers.MISSING_HEADER)
        assertEquals(error.title.parameters!!["header"], "Content-Type")
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
        val error = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.MISSING_FILE, error.title.key)
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
        val error = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.EMPTY_FILE_NAME, error.title.key)
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

    @DisplayName("Editing a file''s name")
    @ParameterizedTest(name = "Editing {1} file''s name as {0} is {3}")
    @MethodSource("editPrivateFile", "editUnlistedFile", "editPublicFile")
    fun editFileName(
        user: TestUser,
        fileVisibility: String,
        fileId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.put("/api/files/$fileId/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Editing an unknown file''s name")
    @Test
    fun editUnknownFileName() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.put("/api/files/00000000-0000-0000-0000-000000000000/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Editing a file''s visibility")
    @ParameterizedTest(name = "Editing {1} file''s visibility as {0} is {3}")
    @MethodSource("editPrivateFile", "editUnlistedFile", "editPublicFile")
    fun editFileVisibility(
        user: TestUser,
        fileVisibility: String,
        fileId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.put("/api/files/$fileId/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = FileVisibility.PRIVATE))
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(FileVisibility.PRIVATE, responseDto.payload!!.visibility)
        }
    }

    @DisplayName("Editing an unknown file''s visibility")
    @Test
    fun editUnknownFileVisibility() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.put("/api/files/00000000-0000-0000-0000-000000000000/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = FileVisibility.PRIVATE))
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
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

    @DisplayName("Deleting an unknown file")
    @Test
    fun deleteUnknownFile() = testApplication {
        val (tokens, _) = user1
        val client = client()
        val response = client.delete("/api/files/00000000-0000-0000-0000-000000000000") {
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
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

    @DisplayName("Sorting files")
    @ParameterizedTest(name = "Sorting files by {0} (direction: {1})")
    @MethodSource
    fun sortingFiles(
        field: String,
        direction: SortDirection,
        expectedResult: (FilePageDTO) -> Unit,
    ) = testApplication {
        val (tokens, _) = userFilters
        val client = client()

        val response = client.post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(sorter = listOf(SortObject(field, direction))))
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

        val messageKeyDTO = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Filters.UNKNOWN_FIELD, messageKeyDTO.title.key)
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

        val messageKeyDTO = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Filters.ILLEGAL_FILTER, messageKeyDTO.title.key)
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
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_NUMBER, responseDto.title.key)
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
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_NUMBER, responseDto.title.key)
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
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_SIZE, responseDto.title.key)
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
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Pagination.INVALID_PAGE_SIZE, responseDto.title.key)
    }

    @DisplayName("Getting file from disabled user should fail")
    @Test
    fun getFileFromDisabledUser() = testApplication {
        val client = client()

        val control = client.get("/api/files/0000_00_01")
        assertEquals(HttpStatusCode.OK, control.status)

        val response = client.get("/api/files/0006_00_01")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Uploading file when quota is reached")
    @Test
    fun uploadFileOnReachedQuota() = testApplication {
        val (_, tokens) = login("upload_exceed_quota", "password")
        val client = client()

        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            tokens?.let {
                bearerAuth(it.accessToken)
            }
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.INSUFFICIENT_DISK_QUOTA, responseDto.title.key)
    }

    @DisplayName("Uploading a file increases disk quota")
    @Test
    fun uploadFileIncreasesQuota() = testApplication {
        val (_, tokens) = login("upload_increase_quota", "password")
        val client = client()

        val userBeforeUpload = client.get("/api/users/00000000-0000-0017-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        val userBeforeDto = Json.decodeFromString<UserRepresentationDTO>(userBeforeUpload.bodyAsText())
        assertEquals(1000, userBeforeDto.currentDiskQuota)

        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val userAfterUpload = client.get("/api/users/00000000-0000-0017-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        val userAfterDto = Json.decodeFromString<UserRepresentationDTO>(userAfterUpload.bodyAsText())
        assertTrue(1000 <= userAfterDto.currentDiskQuota)
    }

    @DisplayName("Deleting a file decreases disk quota")
    @Test
    fun deleteFileDecreasesQuota() = testApplication {
        val (_, tokens) = login("delete_decrease_quota", "password")
        val client = client()

        val userBeforeDelete = client.get("/api/users/00000000-0000-0018-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        val userBeforeDto = Json.decodeFromString<UserRepresentationDTO>(userBeforeDelete.bodyAsText())
        assertEquals(1000, userBeforeDto.currentDiskQuota)

        val response = client.delete("/api/files/00000000-0000-0007-0000-000000000001") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val userAfterDelete = client.get("/api/users/00000000-0000-0018-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        val userAfterDto = Json.decodeFromString<UserRepresentationDTO>(userAfterDelete.bodyAsText())
        assertTrue(1000 >= userAfterDto.currentDiskQuota)
    }

    @DisplayName("Setting a custom link to a file")
    @Test
    fun setCustomLink() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.put("/api/files/00000000-0000-0008-0000-000000000001/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "add-custom-link"))
        }
        val fileDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("add-custom-link", fileDto.payload!!.customLink)
    }

    @DisplayName("Setting a custom link to a file with null link")
    @Test
    fun setCustomLinkNull() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.put("/api/files/00000000-0000-0008-0000-000000000004/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO())
        }
        val fileDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(Errors.Files.CustomLink.MISSING_SLUG, fileDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Setting a custom link to a file with invalid link")
    @Test
    fun setCustomLinkInvalid() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.put("/api/files/00000000-0000-0008-0000-000000000005/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "not_a_VALID_l1nk.."))
        }
        val fileDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(Errors.Files.CustomLink.INVALID_FORMAT, fileDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Setting a custom link to a file with existing custom link")
    @Test
    fun setCustomLinkAlreadyExisting() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.put("/api/files/00000000-0000-0008-0000-000000000006/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "test-link"))
        }
        val fileDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(Errors.Files.CustomLink.ALREADY_EXISTS, fileDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Accessing a file with a custom link")
    @Test
    fun accessFileWithCustomLink() = testApplication {
        val client = client()

        val response = client.get("/c/test-link")

        println(response)
        println(response.bodyAsText())

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @DisplayName("Accessing a file with a custom link that does not exist")
    @Test
    fun accessFileWithNonExistingCustomLink() = testApplication {
        val client = client()

        val response = client.get("/:non-existing-link")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Deleting a custom link from a file")
    @Test
    fun removeCustomLink() = testApplication {
        val (tokens, _) = user1
        val client = client()

        val response = client.delete("/api/files/00000000-0000-0008-0000-000000000003/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        val fileDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
        assertNull(fileDto.payload!!.customLink)
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
        fun uploadFileEToken(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "0000000000000000000000000000000100000000000000000000000000000000",
                Named.of("superadmin", UUID.fromString("00000000-0000-0000-0000-000000000001")),
                HttpStatusCode.Created
            ),
            Arguments.of(
                "0000000000000000000000000000000200000000000000000000000000000000",
                Named.of("admin", UUID.fromString("00000000-0000-0000-0000-000000000002")),
                HttpStatusCode.Created
            ),
            Arguments.of(
                "0000000000000000000000000000000300000000000000000000000000000000",
                Named.of("regular user", UUID.fromString("00000000-0000-0000-0000-000000000003")),
                HttpStatusCode.Created
            ),
            Arguments.of(
                "0000000000000000000000000000000000000000000000000000000000000000",
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
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.name == "filtering1_1.png" })
                }),
                Arguments.of("name", "nlike", "filtering1_1.png", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.name != "filtering1_1.png" })
                }),
                Arguments.of("name", "fuzzy", "filtering", { dto: FilePageDTO ->
                    assertFalse(dto.page.items.isEmpty())
                    assertTrue(dto.page.items.all { it.name.contains("filtering") })
                }),

                Arguments.of("mimeType", "like", "image/png", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.mimeType == "image/png" })
                }),
                Arguments.of("mimeType", "nlike", "image/png", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.mimeType != "image/png" })
                }),

                Arguments.of("size", "eq", "1000", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.size == 1000L })
                }),
                Arguments.of("size", "ne", "1000", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.size != 1000L })
                }),
                Arguments.of("size", "gt", "800", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.size > 800 })
                }),
                Arguments.of("size", "lt", "800", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.size < 800 })
                }),
                Arguments.of("size", "ge", "800", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.size >= 800 })
                }),
                Arguments.of("size", "le", "800", { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.size <= 800 })
                }),

                Arguments.of("visibility", "eq", FileVisibility.PRIVATE.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 1)
                    assertTrue(dto.page.items.all { it.visibility == FileVisibility.PRIVATE })
                }),
                Arguments.of("visibility", "ne", FileVisibility.PRIVATE.toString(), { dto: FilePageDTO ->
                    assertTrue(dto.page.items.size == 2)
                    assertTrue(dto.page.items.all { it.visibility != FileVisibility.PRIVATE })
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

        @JvmStatic
        fun sortingFiles(): Stream<Arguments> = Stream.of(
            Arguments.of("name", SortDirection.ASC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf("filtering1_1.png", "filtering1_2.pdf", "filtering1_3.png"),
                    dto.page.items.map { it.name }
                )
            }),
            Arguments.of("name", SortDirection.DESC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf("filtering1_3.png", "filtering1_2.pdf", "filtering1_1.png"),
                    dto.page.items.map { it.name }
                )
            }),

            Arguments.of("mimeType", SortDirection.ASC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf("application/pdf", "image/png", "image/png"),
                    dto.page.items.map { it.mimeType }
                )
            }),
            Arguments.of("mimeType", SortDirection.DESC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf("image/png", "image/png", "application/pdf"),
                    dto.page.items.map { it.mimeType }
                )
            }),

            Arguments.of("size", SortDirection.ASC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf(500, 800, 1000),
                    dto.page.items.map { it.size }
                )
            }),
            Arguments.of("size", SortDirection.DESC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf(1000, 800, 500),
                    dto.page.items.map { it.size }
                )
            }),

            Arguments.of("createdAt", SortDirection.ASC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf(
                        Instant.parse("1970-01-01T00:00:00.000000Z"),
                        Instant.parse("1970-01-05T00:00:00.000000Z"),
                        Instant.parse("1970-01-10T00:00:00.000000Z"),
                    ),
                    dto.page.items.map { Instant.parse(it.createdAt) }
                )
            }),
            Arguments.of("createdAt", SortDirection.DESC, { dto: FilePageDTO ->
                assertTrue(dto.page.items.size == 3)
                assertContentEquals(
                    listOf(
                        Instant.parse("1970-01-10T00:00:00.000000Z"),
                        Instant.parse("1970-01-05T00:00:00.000000Z"),
                        Instant.parse("1970-01-01T00:00:00.000000Z"),
                    ),
                    dto.page.items.map { Instant.parse(it.createdAt) }
                )
            }),
        )
    }
}
