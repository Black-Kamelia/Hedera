package com.kamelia.hedera.rest.test

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.rest.file.FileRepresentationDTO
import com.kamelia.hedera.rest.file.FileUpdateDTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

abstract class AbstractUserFilesTests(
    private val user: TestUser,
    private val expectedResults: UserFilesTestsExpectedResults,
    private val input: UserFilesTestsInput,
) : AbstractFilesTests(user, expectedResults, input) {

    @DisplayName("Upload a file using token")
    @Test
    fun uploadFileTokenTest() = testApplication {
        val (_, userId) = user
        val client = client()

        val response = client.submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", input.uploadToken)
        }
        assertEquals(expectedResults.uploadFile, response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<FileRepresentationDTO>(response.bodyAsText())
            assertEquals(userId, responseDto.owner.id)
            assertEquals("test.txt", responseDto.name)
            assertEquals("text/plain", responseDto.mimeType)
            assertEquals(20L, responseDto.size)
        }
    }

    @DisplayName("View own file")
    @ParameterizedTest(name = "View own {0} file")
    @MethodSource("visibilities")
    fun viewOwnFileTest(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val fileCode = input.viewOwnFileCode[visibility]!!
        val response = client.get("/api/files/$fileCode") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.viewOwnFile[visibility], response.status)
    }

    @DisplayName("Rename own file")
    @ParameterizedTest(name = "Rename own {0} file")
    @MethodSource("visibilities")
    fun renameOwnFileTest(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val fileId = input.renameOwnFileId[visibility]!!
        val response = client.put("/api/files/$fileId/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.renameOwnFile[visibility], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Name.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Update.Name.Success.MESSAGE, responseDto.message!!.key)
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Update own file visibility")
    @ParameterizedTest(name = "Update own {0} file visibility to {1}")
    @MethodSource
    fun updateVisibilityOwnFileTest(
        visibility: FileVisibility,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val fileId = input.updateVisibilityOwnFileId[visibility]!!
        val response = client.put("/api/files/$fileId/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateVisibilityOwnFile[visibility], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Visibility.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Update.Visibility.Success.MESSAGE, responseDto.message!!.key)
            assertEquals(newVisibility, responseDto.payload!!.visibility)
        }
    }

    @DisplayName("Delete own file")
    @ParameterizedTest(name = "Delete own {0} file")
    @MethodSource("visibilities")
    fun deleteOwnFileTest(
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val fileId = input.deleteOwnFileId[visibility]!!
        val response = client.delete("/api/files/$fileId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOwnFile[visibility], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Delete.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Delete.Success.MESSAGE, responseDto.message!!.key)
        }
    }

    companion object {

        @JvmStatic
        fun visibilities(): Stream<Arguments> = visibilities.map { Arguments.of(it) }.stream()

        @JvmStatic
        fun updateVisibilityOwnFileTest(): Stream<Arguments> {
            return visibilities.flatMap { visibility ->
                visibilities.map { newVisibility ->
                    Arguments.of(visibility, newVisibility)
                }
            }.stream()
        }
    }
}

class UserFilesTestsExpectedResults(
    uploadFile: HttpStatusCode,
    listFiles: HttpStatusCode,

    val viewOwnFile: Map<FileVisibility, HttpStatusCode>,
    val renameOwnFile: Map<FileVisibility, HttpStatusCode>,
    val updateVisibilityOwnFile: Map<FileVisibility, HttpStatusCode>,
    val deleteOwnFile: Map<FileVisibility, HttpStatusCode>,

    viewOthersFileAPI: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    viewOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    renameOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    updateVisibilityOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    deleteOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
) : FilesTestsExpectedResults(
    uploadFile,
    listFiles,
    viewOthersFile,
    viewOthersFileAPI,
    renameOthersFile,
    updateVisibilityOthersFile,
    deleteOthersFile,
)

class UserFilesTestsInput(
    val uploadToken: String,
    val viewOwnFileCode: Map<FileVisibility, String>,
    val renameOwnFileId: Map<FileVisibility, UUID>,
    val updateVisibilityOwnFileId: Map<FileVisibility, UUID>,
    val deleteOwnFileId: Map<FileVisibility, UUID>,

    viewOthersFileCode: Map<UserRole, Map<FileVisibility, String>>,
    renameOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    updateVisibilityOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    deleteOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
) : FilesTestsInput(
    viewOthersFileCode,
    renameOthersFileId,
    updateVisibilityOthersFileId,
    deleteOthersFileId,
)
