package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.rest.file.FileVisibility
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.stream.Stream
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUserSettingsTests(
    private val user: TestUser,
    private val expectedResults: UserSettingsTestsExpectedResults,
) {

    @DisplayName("Get user settings")
    @Test
    fun getUserSettingsTest() = testApplication {
        val (tokens, userId) = user
        val client = client()

        val response = client.get("/api/users/settings") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.getSettings, response.status)
    }

    @DisplayName("Update user setting")
    @ParameterizedTest(name = "Update ''{0}'' setting ")
    @MethodSource
    fun updateUserSettingTest(
        newSettings: UserSettingsUpdateDTO,
        dtoCheck: (UserSettingsRepresentationDTO) -> Boolean,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val getResponse = client.get("/api/users/settings") {
            tokens?.let { bearerAuth(it.accessToken) }
        }

        if (getResponse.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<UserSettingsRepresentationDTO>(getResponse.bodyAsText())
            assertFalse(dtoCheck(responseDto))
        }

        val response = client.patch("/api/users/settings") {
            contentType(ContentType.Application.Json)
            setBody(newSettings)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateSetting, response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<UserSettingsRepresentationDTO>(response.bodyAsText())
            assertTrue(dtoCheck(responseDto))
        }
    }

    companion object {

        @JvmStatic
        fun updateUserSettingTest(): Stream<Arguments> = listOf(
            Arguments.of(
                Named.of(
                    "Default file visibility",
                    UserSettingsUpdateDTO(defaultFileVisibility = FileVisibility.PRIVATE)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.defaultFileVisibility == FileVisibility.PRIVATE }
            ),
            Arguments.of(
                Named.of(
                    "Auto remove files",
                    UserSettingsUpdateDTO(autoRemoveFiles = true)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.autoRemoveFiles }
            ),
            Arguments.of(
                Named.of(
                    "Files size scale",
                    UserSettingsUpdateDTO(filesSizeScale = FilesSizeScale.DECIMAL)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.filesSizeScale == FilesSizeScale.DECIMAL }
            ),
            Arguments.of(
                Named.of(
                    "Date style",
                    UserSettingsUpdateDTO(preferredDateStyle = DateStyle.LONG)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.preferredDateStyle == DateStyle.LONG }
            ),
            Arguments.of(
                Named.of(
                    "Time style",
                    UserSettingsUpdateDTO(preferredTimeStyle = TimeStyle.LONG)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.preferredTimeStyle == TimeStyle.LONG }
            ),
            Arguments.of(
                Named.of(
                    "Locale",
                    UserSettingsUpdateDTO(preferredLocale = Locale.fr)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.preferredLocale == Locale.fr }
            ),
            Arguments.of(
                Named.of(
                    "Upload behavior",
                    UserSettingsUpdateDTO(uploadBehavior = UploadBehavior.MANUAL)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.uploadBehavior == UploadBehavior.MANUAL }
            ),
            Arguments.of(
                Named.of(
                    "File double click action",
                    UserSettingsUpdateDTO(fileDoubleClickAction = FileDoubleClickAction.OPEN_PREVIEW)
                ),
                { dto: UserSettingsRepresentationDTO -> dto.fileDoubleClickAction == FileDoubleClickAction.OPEN_PREVIEW }
            ),
        ).stream()
    }
}

open class UserSettingsTestsExpectedResults(
    val getSettings: HttpStatusCode,
    val updateSetting: HttpStatusCode,
)
