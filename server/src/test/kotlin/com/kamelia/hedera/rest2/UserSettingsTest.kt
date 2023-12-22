package com.kamelia.hedera.rest2

import com.kamelia.hedera.GlobalConfigurationSetup
import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.setting.DateStyle
import com.kamelia.hedera.rest.setting.FileDoubleClickAction
import com.kamelia.hedera.rest.setting.FilesSizeScale
import com.kamelia.hedera.rest.setting.Locale
import com.kamelia.hedera.rest.setting.TimeStyle
import com.kamelia.hedera.rest.setting.UploadBehavior
import com.kamelia.hedera.rest.setting.UserSettingsRepresentationDTO
import com.kamelia.hedera.rest.setting.UserSettingsUpdateDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(GlobalConfigurationSetup::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserSettingsTest {

    @DisplayName("Get own settings")
    @ParameterizedTest(name = "Get own settings as {0} is {1}")
    @MethodSource
    fun getOwnSettings(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/users/settings") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(UserSettingsRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(FileVisibility.UNLISTED, responseDto.defaultFileVisibility)
            assertEquals(false, responseDto.autoRemoveFiles)
            assertEquals(FilesSizeScale.BINARY, responseDto.filesSizeScale)
            assertEquals(DateStyle.SHORT, responseDto.preferredDateStyle)
            assertEquals(TimeStyle.MEDIUM, responseDto.preferredTimeStyle)
            assertEquals(Locale.en, responseDto.preferredLocale)
            assertEquals(UploadBehavior.INSTANT, responseDto.uploadBehavior)
            assertEquals(FileDoubleClickAction.OPEN_NEW_TAB, responseDto.fileDoubleClickAction)
        }
    }

    @DisplayName("Update own settings")
    @ParameterizedTest(name = "Update own settings as {0} is {1}")
    @MethodSource
    fun updateOwnSettings(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/settings") {
            contentType(ContentType.Application.Json)
            setBody(UserSettingsUpdateDTO(defaultFileVisibility = FileVisibility.PUBLIC))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(UserSettingsRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(FileVisibility.PUBLIC, responseDto.defaultFileVisibility)
        }
    }

    @DisplayName("Update individual setting")
    @ParameterizedTest(name = "Update ''{1}'' setting")
    @MethodSource
    fun updateSetting(
        user: TestUser,
        settingName: String,
        newSettingsDTO: UserSettingsUpdateDTO,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/users/settings") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())
        val responseDto = Json.decodeFromString(UserSettingsRepresentationDTO.serializer(), response.bodyAsText())

        val updateResponse = client.patch("/api/users/settings") {
            contentType(ContentType.Application.Json)
            setBody(newSettingsDTO)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status, updateResponse.bodyAsText())
        val updateResponseDto =
            Json.decodeFromString(UserSettingsRepresentationDTO.serializer(), updateResponse.bodyAsText())
        newSettingsDTO.defaultFileVisibility?.let {
            assertEquals(it, updateResponseDto.defaultFileVisibility)
            assertNotEquals(responseDto.defaultFileVisibility, updateResponseDto.defaultFileVisibility)
        }
        newSettingsDTO.autoRemoveFiles?.let {
            assertEquals(it, updateResponseDto.autoRemoveFiles)
            assertNotEquals(responseDto.autoRemoveFiles, updateResponseDto.autoRemoveFiles)
        }
        newSettingsDTO.filesSizeScale?.let {
            assertEquals(it, updateResponseDto.filesSizeScale)
            assertNotEquals(responseDto.filesSizeScale, updateResponseDto.filesSizeScale)
        }
        newSettingsDTO.preferredDateStyle?.let {
            assertEquals(it, updateResponseDto.preferredDateStyle)
            assertNotEquals(responseDto.preferredDateStyle, updateResponseDto.preferredDateStyle)
        }
        newSettingsDTO.preferredTimeStyle?.let {
            assertEquals(it, updateResponseDto.preferredTimeStyle)
            assertNotEquals(responseDto.preferredTimeStyle, updateResponseDto.preferredTimeStyle)
        }
        newSettingsDTO.preferredLocale?.let {
            assertEquals(it, updateResponseDto.preferredLocale)
            assertNotEquals(responseDto.preferredLocale, updateResponseDto.preferredLocale)
        }
        newSettingsDTO.uploadBehavior?.let {
            assertEquals(it, updateResponseDto.uploadBehavior)
            assertNotEquals(responseDto.uploadBehavior, updateResponseDto.uploadBehavior)
        }
        newSettingsDTO.fileDoubleClickAction?.let {
            assertEquals(it, updateResponseDto.fileDoubleClickAction)
            assertNotEquals(responseDto.fileDoubleClickAction, updateResponseDto.fileDoubleClickAction)
        }
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                superadmin = Pair(
                    login("owner1-settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin1-settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0000-0000-000000000002")
                )
                user = Pair(
                    login("user1-settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0000-0000-000000000003")
                )
            }
        }

        @JvmStatic
        fun getOwnSettings(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), HttpStatusCode.OK),
                Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateOwnSettings(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0001-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0001-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_settings", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0001-0000-000000000003")
                )
            }

            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), HttpStatusCode.OK),
                Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateSetting(): Stream<Arguments> {
            lateinit var user: TestUser

            testApplication {
                user = Pair(
                    login("user_edit_individual_setting", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000001-0000-0002-0000-000000000001")
                )
            }

            return Stream.of(
                Arguments.of(
                    user,
                    "defaultFileVisibility",
                    UserSettingsUpdateDTO(defaultFileVisibility = FileVisibility.PUBLIC)
                ),
                Arguments.of(user, "autoRemoveFiles", UserSettingsUpdateDTO(autoRemoveFiles = true)),
                Arguments.of(user, "filesSizeScale", UserSettingsUpdateDTO(filesSizeScale = FilesSizeScale.DECIMAL)),
                Arguments.of(user, "preferredDateStyle", UserSettingsUpdateDTO(preferredDateStyle = DateStyle.FULL)),
                Arguments.of(user, "preferredTimeStyle", UserSettingsUpdateDTO(preferredTimeStyle = TimeStyle.FULL)),
                Arguments.of(user, "preferredLocale", UserSettingsUpdateDTO(preferredLocale = Locale.fr)),
                Arguments.of(user, "uploadBehavior", UserSettingsUpdateDTO(uploadBehavior = UploadBehavior.MANUAL)),
                Arguments.of(
                    user,
                    "fileDoubleClickAction",
                    UserSettingsUpdateDTO(fileDoubleClickAction = FileDoubleClickAction.OPEN_PREVIEW)
                ),
            )
        }
    }
}
