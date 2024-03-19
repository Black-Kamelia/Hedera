package com.kamelia.hedera.rest.file

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.response.BulkActionSummaryDTO
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.test.assertNull
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractFilesTests(
    private val user: TestUser,
    private val expectedResults: FilesTestsExpectedResults,
    private val input: FilesTestsInput,
) {

    private lateinit var testFolder: File

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

    @AfterAll
    fun cleanUpEnvironment() {
        testFolder.deleteRecursively()
    }

    @DisplayName("Upload a file")
    @Test
    fun uploadFileTest() = testApplication {
        val (tokens, userId) = user

        val response = client().submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            tokens?.let { bearerAuth(it.accessToken) }
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

    @DisplayName("List files")
    @Test
    fun listFilesTest() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.listFiles, response.status, response.bodyAsText())
    }

    @DisplayName("List files formats")
    @Test
    fun listFilesFormatsTest() = testApplication {
        val (tokens, _) = user

        val response = client().get("/api/files/filters/formats") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.listFilesFormats, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<List<String>>(response.bodyAsText())
            assertEquals(setOf("text/plain", "image/png"), responseDto.toSet())
        }
    }

    @DisplayName("View others file (via API)")
    @ParameterizedTest(name = "View {0}''s {1} file")
    @MethodSource("rolesVisibilitiesCombo")
    fun viewOthersFileAPITest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileCode = input.viewOthersFileCode[target]!![visibility]!!
        val response = client().get("/api/files/$fileCode") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.viewOthersFileAPI[target]!![visibility], response.status, response.bodyAsText())
    }

    @DisplayName("Rename others file")
    @ParameterizedTest(name = "Rename {0}''s {1} file")
    @MethodSource("rolesVisibilitiesCombo")
    fun renameOthersFileTest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.renameOthersFileId[target]!![visibility]!!
        val response = client().put("/api/files/$fileId/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.renameOthersFile[target]!![visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Name.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.Name.success.message.key, responseDto.message!!.key)
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Update others file visibility")
    @ParameterizedTest(name = "Update {0}''s {1} file visibility to {2}")
    @MethodSource("rolesVisibilitiesCartesianProductCombo")
    fun updateVisibilityOthersFileTest(
        target: UserRole,
        visibility: FileVisibility,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.updateVisibilityOthersFileId[target]!![visibility]!!
        val response = client().put("/api/files/$fileId/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(
            expectedResults.updateVisibilityOthersFile[target]!![visibility],
            response.status,
            response.bodyAsText()
        )

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Visibility.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.Visibility.success.message.key, responseDto.message!!.key)
            assertEquals(newVisibility, responseDto.payload!!.visibility)
        }
    }

    @DisplayName("Bulk update others files visibility")
    @ParameterizedTest(name = "Bulk update {0}''s {1} files visibility to {2}")
    @MethodSource("rolesVisibilitiesCartesianProductCombo")
    fun bulkUpdateVisibilityOthersFileTest(
        target: UserRole,
        visibility: FileVisibility,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val filesId = input.bulkUpdateVisibilityOthersFilesId[target]!![visibility]!!
        val response = client().post("/api/files/bulk/visibility") {
            contentType(ContentType.Application.Json)
            setBody(BulkUpdateVisibilityDTO(filesId, newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.bulkUpdateVisibilityOthersFile, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<BulkActionSummaryDTO<String>>>(response.bodyAsText())
            assertEquals(
                expectedResults.bulkUpdateVisibilityOthersFileResult[target]!![visibility],
                responseDto.payload!!.success
            )
            assertEquals(0, responseDto.payload!!.fail)
            assertEquals(2, responseDto.payload!!.total)
        }
    }

    @DisplayName("Update others file's custom link")
    @ParameterizedTest(name = "Update {0}''s {1} file's custom link")
    @MethodSource("rolesVisibilitiesCombo")
    fun updateOtherFileCustomLinkTest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, uuid) = user

        val fileId = input.updateCustomLinkOthersFileId[target]!![visibility]!!
        val customLink = "$uuid-$fileId-$visibility".lowercase()
        val response = client().put("/api/files/$fileId/custom-link") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(customLink = customLink))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(
            expectedResults.updateCustomLinkOthersFile[target]!![visibility],
            response.status,
            response.bodyAsText()
        )

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.CustomLink.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Update.CustomLink.success.message.key, responseDto.message!!.key)
            assertEquals(customLink, responseDto.payload!!.customLink)
        }
    }

    @DisplayName("Remove others file's custom link")
    @ParameterizedTest(name = "Remove {0}''s {1} file's custom link")
    @MethodSource("rolesVisibilitiesCombo")
    fun removeOtherFileCustomLinkTest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val fileId = input.removeCustomLinkOthersFileId[target]!![visibility]!!
        val response = client().delete("/api/files/$fileId/custom-link") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(
            expectedResults.removeCustomLinkOthersFile[target]!![visibility],
            response.status,
            response.bodyAsText()
        )

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.RemoveCustomLink.success.title.key, responseDto.title.key)
            assertNull(responseDto.payload!!.customLink)
        }
    }

    @DisplayName("Delete others file")
    @ParameterizedTest(name = "Delete {0}''s {1} file")
    @MethodSource("rolesVisibilitiesCombo")
    fun deleteOthersFileTest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val response = client().delete("/api/files/${input.deleteOthersFileId[target]!![visibility]}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOthersFile[target]!![visibility], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Delete.success.title.key, responseDto.title.key)
            assertEquals(Actions.Files.Delete.success.message.key, responseDto.message!!.key)
        }
    }

    @DisplayName("Bulk delete others files")
    @ParameterizedTest(name = "Bulk delete {0}''s {1} files")
    @MethodSource("rolesVisibilitiesCombo")
    fun bulkDeleteOthersFileTest(
        target: UserRole,
        visibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user

        val filesId = input.bulkDeleteOthersFileId[target]!![visibility]!!
        val response = client().post("/api/files/bulk/delete") {
            contentType(ContentType.Application.Json)
            setBody(BulkDeleteDTO(filesId))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.bulkDeleteOthersFile, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<BulkActionSummaryDTO<String>>>(response.bodyAsText())
            assertEquals(
                expectedResults.bulkDeleteOthersFileResult[target]!![visibility],
                responseDto.payload!!.success
            )
            assertEquals(0, responseDto.payload!!.fail)
            assertEquals(2, responseDto.payload!!.total)
        }
    }

    companion object {

        private val roles = listOf(
            Named.of("owner", UserRole.OWNER),
            Named.of("admin", UserRole.ADMIN),
            Named.of("regular user", UserRole.REGULAR),
        )
        val visibilities = listOf(
            Named.of("public", FileVisibility.PUBLIC),
            Named.of("unlisted", FileVisibility.UNLISTED),
            Named.of("private", FileVisibility.PRIVATE),
        )

        @JvmStatic
        fun rolesVisibilitiesCombo(): Stream<Arguments> {
            return roles.flatMap { role ->
                visibilities.map { visibility ->
                    Arguments.of(role, visibility)
                }
            }.stream()
        }

        @JvmStatic
        fun rolesVisibilitiesCartesianProductCombo(): Stream<Arguments> {
            return roles.flatMap { role ->
                visibilities.flatMap { visibility ->
                    visibilities.map { newVisibility ->
                        Arguments.of(role, visibility, newVisibility)
                    }
                }
            }.stream()
        }
    }
}
