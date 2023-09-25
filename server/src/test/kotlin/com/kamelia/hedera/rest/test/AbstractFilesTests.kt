package com.kamelia.hedera.rest.test

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.appendFile
import com.kamelia.hedera.client
import com.kamelia.hedera.rest.file.FileRepresentationDTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.file.toSizeDTO
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

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
        Assertions.assertEquals(expectedResults.uploadFile, response.status)
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(FileRepresentationDTO.serializer(), response.bodyAsText())
            Assertions.assertEquals(userId, responseDto.owner.id)
            Assertions.assertEquals("test.txt", responseDto.name)
            Assertions.assertEquals("text/plain", responseDto.mimeType)
            Assertions.assertEquals(20L.toSizeDTO(), responseDto.size)
        }
    }
}

open class FilesTestsExpectedResults(
    val uploadFile: HttpStatusCode,

    val viewOtherFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val renameOtherFile: Map<UserRole, HttpStatusCode>,
    val updateVisibilityOtherFile: Map<UserRole, HttpStatusCode>,
    val deleteOtherFile: Map<UserRole, HttpStatusCode>,
)

open class FilesTestsInput(
)