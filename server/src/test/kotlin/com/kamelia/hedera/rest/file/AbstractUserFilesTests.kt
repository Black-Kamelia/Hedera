package com.kamelia.hedera.rest.file

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.response.BulkActionSummaryDTO
import com.kamelia.hedera.core.response.MessageDTO
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.stream.Stream
import kotlin.test.assertNull
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
    fun uploadFileToken() = testApplication {
        val (_, userId) = user

        val response = client().submitFormWithBinaryData("/api/files/upload/token", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            header("Upload-Token", input.uploadToken)
        }
        assertEquals(expectedResults.uploadFile, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<FileRepresentationDTO>(response.bodyAsText())
            assertEquals(userId, responseDto.owner.id)
            assertEquals("test.txt", responseDto.name)
            assertEquals("text/plain", responseDto.mimeType)
            assertEquals(20L, responseDto.size)
        }
    }

    @DisplayName("View own file (via API)")
    @ParameterizedTest(name = "View own {0} file")
    @MethodSource("visibilities")
    fun viewOwnFileAPI(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user

        val fileCode = input.viewOwnFileCode[visibility]!!
        val response = client().get("/api/files/$fileCode") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.viewOwnFile[visibility], response.status, response.bodyAsText())
    }

    @DisplayName("View file publicly")
    @ParameterizedTest(name = "View {0} file publicly")
    @MethodSource("visibilities")
    fun viewFilePublicly(
        visibility: FileVisibility
    ) = testApplication {
        val fileCode = input.viewPubliclyFileCode[visibility]!!
        val response = client().get("/m/$fileCode")
        assertEquals(expectedResults.viewPubliclyFileCode[visibility], response.status, response.bodyAsText())
    }

    @DisplayName("View file publicly via custom link")
    @ParameterizedTest(name = "View {0} file publicly via custom link")
    @MethodSource("visibilities")
    fun viewFilePubliclyCustomLink(
        visibility: FileVisibility
    ) = testApplication {
        val customLink = input.viewPubliclyFileCustomLink[visibility]!!
        val response = client().get("/c/$customLink")
        assertEquals(expectedResults.viewPubliclyFileCustomLink[visibility], response.status, response.bodyAsText())
    }

    @DisplayName("Rename own file")
    @ParameterizedTest(name = "Rename own {0} file")
    @MethodSource("visibilities")
    fun renameOwnFile(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.renameOwnFileId[visibility]!!
        val response = client().put("/api/files/$fileId/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.renameOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Name.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.Name.success.message.key, responseDto.message!!.key)
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Update own file visibility")
    @ParameterizedTest(name = "Update own {0} file visibility to {1}")
    @MethodSource("visibilityCartesianProduct")
    fun updateVisibilityOwnFile(
        visibility: FileVisibility,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val filesId = input.bulkUpdateVisibilityOwnFilesId[visibility]!!
        val response = client().post("/api/files/bulk/visibility") {
            contentType(ContentType.Application.Json)
            setBody(BulkUpdateVisibilityDTO(filesId, newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateVisibilityOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<BulkActionSummaryDTO<String>>>(response.bodyAsText())
            assertEquals(expectedResults.bulkUpdateVisibilityOwnFileResult[visibility], responseDto.payload!!.success)
            assertEquals(0, responseDto.payload!!.fail)
            assertEquals(2, responseDto.payload!!.total)
        }
    }

    @DisplayName("Bulk update own files visibility")
    @ParameterizedTest(name = "Bulk update own {0} files visibility to {1}")
    @MethodSource("visibilityCartesianProduct")
    fun bulkUpdateVisibilityOwnFile(
        visibility: FileVisibility,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.updateVisibilityOwnFileId[visibility]!!
        val response = client().put("/api/files/$fileId/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateVisibilityOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Visibility.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.Visibility.success.message.key, responseDto.message!!.key)
            assertEquals(newVisibility, responseDto.payload!!.visibility)
        }
    }

    @DisplayName("Update own file's custom link")
    @ParameterizedTest(name = "Update own {0} file's custom link")
    @MethodSource("visibilities")
    fun updateOwnFileCustomLink(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, uuid) = user

        val fileId = input.updateCustomLinkOwnFileId[visibility]!!
        val customLink = "$uuid-$fileId-$visibility".lowercase()
        val response = client().put("/api/files/$fileId/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = customLink))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateCustomLinkOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.CustomLink.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.CustomLink.success.message.key, responseDto.message!!.key)
            assertEquals(customLink, responseDto.payload!!.customLink)
        }
    }

    @DisplayName("Remove own file's custom link")
    @ParameterizedTest(name = "Remove own {0} file's custom link")
    @MethodSource("visibilities")
    fun removeOwnFileCustomLink(
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.removeCustomLinkOwnFileId[visibility]!!
        val response = client().delete("/api/files/$fileId/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.removeCustomLinkOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.RemoveCustomLink.success.title.key, responseDto.title.key)
            assertNull(responseDto.payload!!.customLink)
        }
    }

    @DisplayName("Delete own file")
    @ParameterizedTest(name = "Delete own {0} file")
    @MethodSource("visibilities")
    fun deleteOwnFile(
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.deleteOwnFileId[visibility]!!
        val response = client().delete("/api/files/$fileId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOwnFile[visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Delete.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Delete.success.message.key, responseDto.message!!.key)
        }
    }

    @DisplayName("Bulk delete own files")
    @ParameterizedTest(name = "Bulk delete own {0} files")
    @MethodSource("visibilities")
    fun bulkDeleteOwnFile(
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val filesId = input.bulkDeleteOwnFilesId[visibility]!!
        val response = client().post("/api/files/bulk/delete") {
            contentType(ContentType.Application.Json)
            setBody(BulkDeleteDTO(filesId))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.bulkDeleteOwnFile, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<BulkActionSummaryDTO<String>>>(response.bodyAsText())
            assertEquals(expectedResults.bulkDeleteOwnFileResult[visibility], responseDto.payload!!.success)
            assertEquals(0, responseDto.payload!!.fail)
            assertEquals(2, responseDto.payload!!.total)
        }
    }

    companion object {

        @JvmStatic
        fun visibilities(): Stream<Arguments> = visibilities.map { Arguments.of(it) }.stream()

        @JvmStatic
        fun visibilityCartesianProduct(): Stream<Arguments> {
            return visibilities.flatMap { visibility ->
                visibilities.map { newVisibility ->
                    Arguments.of(visibility, newVisibility)
                }
            }.stream()
        }
    }
}