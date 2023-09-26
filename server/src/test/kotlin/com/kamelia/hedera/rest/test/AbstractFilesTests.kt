package com.kamelia.hedera.rest.test

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Actions
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
        println("Preparing environment for $user")


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
    fun cleanUpEnvironment() {
        testFolder.deleteRecursively()
    }

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

    @DisplayName("View others file (via API)")
    @ParameterizedTest(name = "View {0}''s {1} file")
    @MethodSource("rolesVisibilitiesCombo")
    fun viewOthersFileAPITest(
        target: UserRole,
        visibility: FileVisibility
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val response = client.get("/api/files/${input.viewOthersFileCode[target]!![visibility]}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.viewOthersFileAPI[target]!![visibility], response.status)
    }

    @DisplayName("Rename others file")
    @ParameterizedTest(name = "Rename {0}''s file")
    @MethodSource("roles")
    fun renameOthersFileTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val response = client.put("/api/files/${input.renameOthersFileId[target]}/name") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(name = "bar.txt"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.renameOthersFile[target], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Name.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Update.Name.Success.MESSAGE, responseDto.message!!.key)
            assertEquals("bar.txt", responseDto.payload!!.name)
        }
    }

    @DisplayName("Update others file visibility")
    @ParameterizedTest(name = "Update {0}''s file visibility to {1}")
    @MethodSource("rolesVisibilitiesCombo")
    fun updateVisibilityOthersFileTest(
        target: UserRole,
        newVisibility: FileVisibility,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val response = client.put("/api/files/${input.updateVisibilityOthersFileId[target]}/visibility") {
            contentType(ContentType.Application.Json)
            setBody(FileUpdateDTO(visibility = newVisibility))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateVisibilityOthersFile[target], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Update.Visibility.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Update.Visibility.Success.MESSAGE, responseDto.message!!.key)
            assertEquals(newVisibility, responseDto.payload!!.visibility)
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
        val client = client()

        val response = client.delete("/api/files/${input.deleteOthersFileId[target]!![visibility]}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOthersFile[target]!![visibility], response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<FileRepresentationDTO>>(response.bodyAsText())
            assertEquals(Actions.Files.Delete.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Files.Delete.Success.MESSAGE, responseDto.message!!.key)
        }
    }

    companion object {

        @JvmStatic
        fun rolesVisibilitiesCombo(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("owner", UserRole.OWNER), Named.of("public", FileVisibility.PUBLIC)),
            Arguments.of(Named.of("owner", UserRole.OWNER), Named.of("unlisted", FileVisibility.UNLISTED)),
            Arguments.of(Named.of("owner", UserRole.OWNER), Named.of("private", FileVisibility.PRIVATE)),
            Arguments.of(Named.of("admin", UserRole.ADMIN), Named.of("public", FileVisibility.PUBLIC)),
            Arguments.of(Named.of("admin", UserRole.ADMIN), Named.of("unlisted", FileVisibility.UNLISTED)),
            Arguments.of(Named.of("admin", UserRole.ADMIN), Named.of("private", FileVisibility.PRIVATE)),
            Arguments.of(Named.of("regular user", UserRole.REGULAR), Named.of("public", FileVisibility.PUBLIC)),
            Arguments.of(Named.of("regular user", UserRole.REGULAR), Named.of("unlisted", FileVisibility.UNLISTED)),
            Arguments.of(Named.of("regular user", UserRole.REGULAR), Named.of("private", FileVisibility.PRIVATE)),
        )

        @JvmStatic
        fun roles(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("owner", UserRole.OWNER)),
            Arguments.of(Named.of("admin", UserRole.ADMIN)),
            Arguments.of(Named.of("regular user", UserRole.REGULAR)),
        )

    }
}

open class FilesTestsExpectedResults(
    val uploadFile: HttpStatusCode,

    val viewOthersFileAPI: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val viewOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val renameOthersFile: Map<UserRole, HttpStatusCode>,
    val updateVisibilityOthersFile: Map<UserRole, HttpStatusCode>,
    val deleteOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
)

open class FilesTestsInput(
    val viewOthersFileCode: Map<UserRole, Map<FileVisibility, String>>,
    val renameOthersFileId: Map<UserRole, UUID>,
    val updateVisibilityOthersFileId: Map<UserRole, UUID>,
    val deleteOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
)