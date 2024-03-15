package com.kamelia.hedera.rest.file

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.core.pageable.FilterObject
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.core.pageable.SortDirection
import com.kamelia.hedera.rest.core.pageable.SortObject
import com.kamelia.hedera.uuid
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Files edge cases tests")
class FilesEdgeCasesTests {

    @DisplayName("Upload file using token with no token")
    @Test
    fun uploadFileUsingTokenWithNoToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        })
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Headers.MISSING_HEADER, responseDto.title.key)
    }

    @DisplayName("Upload file using token with empty token")
    @Test
    fun uploadFileUsingTokenWithEmptyToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.INVALID_TOKEN, responseDto.title.key)
    }

    @DisplayName("Upload file using token with unknown token")
    @Test
    fun uploadFileUsingTokenWithUnknownToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "c149465a314a43d0a4fe75bb8d38695c2b0f015550e549a2848fa08b1bf10ea5")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.INVALID_TOKEN, responseDto.title.key)
    }

    @DisplayName("Upload file using token with deleted token")
    @Test
    fun uploadFileUsingTokenWithDeletedToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "0000000110000000000100000000000000000000000000000000000000000000")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.INVALID_TOKEN, responseDto.title.key)
    }

    @DisplayName("Upload file using token with undeleted token")
    @Test
    fun uploadFileUsingTokenWithUndeletedToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "0000000110000000000200000000000000000000000000000000000000000000")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.INVALID_TOKEN, responseDto.title.key)
    }

    @DisplayName("Upload file using token with invalid token")
    @Test
    fun uploadFileUsingTokenWithInvalidToken() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "0000000100010000000100000000000011111111111111111111111111111111")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.INVALID_TOKEN, responseDto.title.key)
    }

    @DisplayName("Upload file using token with no file")
    @Test
    fun uploadFileUsingTokenWithNoFile() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {}) {
            header("Upload-Token", "0000000100010000000100000000000000000000000000000000000000000000")
        }
        println(response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.MISSING_FILE, responseDto.title.key)
    }

    @DisplayName("Upload file with no name")
    @Test
    fun uploadFileWithNoName() = testApplication {
        val (tokens, _) = user

        val response = client().submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", null, "text/plain")
        }) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        /*
        Theoretically untestable because of the way the form data is handled
        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.MISSING_FILE_NAME, responseDto.title.key)
         */
    }

    @DisplayName("Upload file with name too long")
    @Test
    fun uploadFileWithNameTooLong() = testApplication {
        val (tokens, _) = user

        val response = client().submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "a".repeat(512), "text/plain")
        }) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.Name.NAME_TOO_LONG, responseDto.title.key)
    }

    @DisplayName("Upload file with no file")
    @Test
    fun uploadFileWithNoFile() = testApplication {
        val (tokens, _) = user

        val response = client().submitFormWithBinaryData("/api/files/upload", formData {}) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.MISSING_FILE, responseDto.title.key)
    }

    @DisplayName("Upload file with wrong type")
    @Test
    fun uploadFileWithWrongType() = testApplication {
        // TODO ????
    }

    @DisplayName("Upload file with empty name")
    @Test
    fun uploadFileWithEmptyName() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "", "text/plain")
        }) {
            header("Upload-Token", "0000000100010000000100000000000000000000000000000000000000000000")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Uploads.EMPTY_FILE_NAME, responseDto.title.key)
    }

    @DisplayName("Upload file while quota is exceeded")
    @Test
    fun uploadFileWhileExceededQuota() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", "0000000100010000000100000000000100000000000000000000000000000000")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.INSUFFICIENT_DISK_QUOTA, responseDto.title.key)
    }

    @DisplayName("Upload file bigger than quota")
    @Test
    fun uploadFileBiggerThanQuota() = testApplication {
        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/larger_test.txt", "larger_test.txt", "text/plain")
        }) {
            header("Upload-Token", "0000000100010000000100000000000100000000000000000000000000000000")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.INSUFFICIENT_DISK_QUOTA, responseDto.title.key)
    }

    @DisplayName("Rename unknown file")
    @Test
    fun renameUnknownFile() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/${UUID(0, 0)}/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Rename file with no name")
    @Test
    fun renameFileWithNoName() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0001-000000000001/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.Name.MISSING_NAME, responseDto.fields!!["name"]!!.key)
    }

    @DisplayName("Rename file with empty name")
    @Test
    fun renameFileWithEmptyName() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0001-000000000001/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = ""))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.Name.EMPTY_NAME, responseDto.fields!!["name"]!!.key)
    }

    @DisplayName("Rename file with name too long")
    @Test
    fun renameFileWithNameTooLong() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0005-000000000001/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "a".repeat(300)))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.Name.NAME_TOO_LONG, responseDto.fields!!["name"]!!.key)
    }

    @DisplayName("Edit unknown file's visibility")
    @Test
    fun editUnknownFileVisibility() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/${UUID(0, 0)}/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = FileVisibility.UNLISTED))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Edit file's visibility with wrong visibility")
    @Test
    fun editFileVisibilityWithWrongVisibility() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(FileUpdateDTO(visibility = FileVisibility.UNLISTED)).replace("UNLISTED", "WRONG")

        val response = client().put("/api/files/00000001-000a-0000-0002-000000000001/visibility") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.BAD_REQUEST_RAW, responseDto.title.key)
    }

    @DisplayName("Edit file's visibility with no visibility")
    @Test
    fun editFileVisibilityWithNoVisibility() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0002-000000000002/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.Visibility.MISSING_VISIBILITY, responseDto.fields!!["visibility"]!!.key)
    }

    @DisplayName("Set unknown file custom link")
    @Test
    fun setUnknownFileCustomLink() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/${UUID(0, 0)}/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "unknown-custom-link"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Set file custom link with no link")
    @Test
    fun setFileCustomLinkWithNoLink() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0003-000000000001/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.CustomLink.MISSING_SLUG, responseDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Set file custom link with invalid link")
    @Test
    fun setFileCustomLinkWithInvalidLink() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0003-000000000001/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "invalid link_so@wrong"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.CustomLink.INVALID_FORMAT, responseDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Set file custom link with already existing link")
    @Test
    fun setFileCustomLinkWithAlreadyExistingLink() = testApplication {
        val (tokens, _) = user

        val response = client().put("/api/files/10000001-000a-0000-0003-000000000001/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = "existing-link"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.CustomLink.ALREADY_EXISTS, responseDto.fields!!["customLink"]!!.key)
    }

    @DisplayName("Delete unknown file")
    @Test
    fun deleteUnknownFile() = testApplication {
        val (tokens, _) = user

        val response = client().delete("/api/files/${UUID(0, 0)}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("View unknown file")
    @Test
    fun viewUnknownFile() = testApplication {
        val (tokens, _) = user

        val response = client().get("/api/files/0000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("View file belonging to deactivated user")
    @Test
    fun viewFileBelongingToDeactivatedUser() = testApplication {
        val (tokens, _) = user

        val response = client().get("/api/files/01a0040000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Files.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("View file using unknown custom link")
    @Test
    fun viewFileUsingUnknownCustomLink() = testApplication {
        val response = client().get("/c/unknown-custom-link")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Filter files on unknown field")
    @Test
    fun filterFilesOnUnknownField() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                filters = listOf(
                    listOf(
                        FilterObject(field = "unknown", operator = "eq", value = "test")
                    )
                )
            )
        )

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter files with malformed filter")
    @Test
    fun filterFilesWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"filters":[[{"malformed"}]]}"""

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter files with unknown operator")
    @Test
    fun filterFilesWithUnknownOperator() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                filters = listOf(
                    listOf(
                        FilterObject(field = "name", operator = "unknown", value = "test")
                    )
                )
            )
        )

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files on unknown field")
    @Test
    fun sortFilesOnUnknownField() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(
                PageDefinitionDTO(
                    sorter = listOf(SortObject(field = "unknown"))
                )
            )
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files with malformed filter")
    @Test
    fun sortFilesWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"sorter":[{"malformed"}]}"""

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files with unknown direction")
    @Test
    fun sortFilesWithUnknownDirection() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                sorter = listOf(
                    SortObject(field = "name", direction = SortDirection.DESC)
                )
            )
        ).replace("DESC", "WRONG")

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        println(response.bodyAsText())
    }

    @DisplayName("Paginate files with negative page")
    @Test
    fun paginateFilesWithNegativePage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?page=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with negative page size")
    @Test
    fun paginateFilesWithNegativePageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with invalid page")
    @Test
    fun paginateFilesWithInvalidPage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?page=wrong") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with invalid page size")
    @Test
    fun paginateFilesWithInvalidPageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=wrong") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    companion object {

        private lateinit var user: TestUser
        private lateinit var testFolder: File

        init {
            testApplication {
                user = Pair(
                    login("files.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
            }
        }

        @JvmStatic
        @BeforeAll
        fun prepareEnvironment() {
            val testFolderPath = Path.of("_tests")
            testFolder = if (Files.exists(testFolderPath)) {
                testFolderPath.toFile()
            } else {
                Files.createDirectory(testFolderPath).toFile()
            }

            val uploadFolder = File(
                AbstractFilesTests::class.java.getResource("/test_files/upload")?.toURI()
                    ?: throw Exception("Missing resource folder")
            )

            uploadFolder.copyRecursively(testFolder.resolve("upload"), true)
        }

        @JvmStatic
        @AfterAll
        fun cleanUpEnvironment() {
            testFolder.deleteRecursively()
        }
    }
}