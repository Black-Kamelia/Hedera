package com.kamelia.hedera.rest.test

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.rest.file.FileRepresentationDTO
import com.kamelia.hedera.rest.file.FileUpdateDTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.file.toSizeDTO
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest2.FileTest
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class AbstractFilesTests(
    private val user: TestUser,
    private val expectedResults: FilesTestsExpectedResults,
    private val input: FilesTestsInput,
) {

    @DisplayName("Upload a file")
    @Test
    fun uploadFileTest() = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.submitFormWithBinaryData("/api/files/upload", formData {
            appendFile("/test_files/test.txt", "test.txt", "text/plain")
        }) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.uploadFile, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(userId, responseDto.owner.id)
            assertEquals("test.txt", responseDto.name)
            assertEquals("text/plain", responseDto.mimeType)
            assertEquals(20L.toSizeDTO(), responseDto.size)
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

        val response = client.get("/api/files/${input.viewOwnFileCode[visibility]}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.viewOwnFile[visibility], response.status)
    }

    @DisplayName("Rename own file")
    @Test
    fun renameOwnFileTest() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.put("/api/files/${input.renameOwnFileId}/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.renameOwnFile, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Update own file visibility")
    @ParameterizedTest(name = "Update own file visibility to {0}")
    @MethodSource("visibilities")
    fun updateVisibilityOwnFileTest(
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.put("/api/files/${input.updateVisibilityOwnFileId}/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateVisibilityOwnFile, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(newVisibility, responseDto.payload!!.visibility)
        }
    }

    @DisplayName("Delete own file")
    @Test
    fun deleteOwnFileTest() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/files/${input.deleteOwnFileId}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOwnFile, response.status)
    }

    companion object {

        private lateinit var testFolder: File

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
                FileTest::class.java.getResource("/upload")?.toURI()
                    ?: throw Exception("Missing resource folder")
            )

            uploadFolder.copyRecursively(testFolder.resolve("upload"), true)
        }

        @JvmStatic
        @AfterAll
        fun cleanUpEnvironment() {
            testFolder.deleteRecursively()
        }

        @JvmStatic
        fun visibilities(): Stream<Arguments> = Stream.of(
            Named.of("public", FileVisibility.PUBLIC),
            Named.of("unlisted", FileVisibility.UNLISTED),
            Named.of("private", FileVisibility.PRIVATE),
        ).map { Arguments.of(it) }
    }
}

class FilesTestsExpectedResults(
    val uploadFile: HttpStatusCode,

    val viewOwnFile: Map<FileVisibility, HttpStatusCode>,
    val renameOwnFile: HttpStatusCode,
    val updateVisibilityOwnFile: HttpStatusCode,
    val deleteOwnFile: HttpStatusCode,

    val viewOtherFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val renameOtherFile: Map<UserRole, HttpStatusCode>,
    val updateVisibilityOtherFile: Map<UserRole, HttpStatusCode>,
    val deleteOtherFile: Map<UserRole, HttpStatusCode>,
)

class FilesTestsInput(
    val viewOwnFileCode: Map<FileVisibility, String>,
    val renameOwnFileId: UUID,
    val updateVisibilityOwnFileId: UUID,
    val deleteOwnFileId: UUID,
)
