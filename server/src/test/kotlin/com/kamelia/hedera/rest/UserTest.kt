package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.user.UserCreationDTO
import com.kamelia.hedera.rest.user.UserPasswordUpdateDTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest.user.UserUpdateDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    @DisplayName("Signing up")
    @Test
    fun signUp() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "test.test-test_123",
            password = "Test0@aaa",
            email = "test.signup@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(newUserDto.username, responseDto.username)
        assertEquals(newUserDto.email, responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)

        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with lowercase username")
    @Test
    fun signUpLowercaseUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "thisisatest",
            password = "password",
            email = "test.signup.lower@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with username with dashes")
    @Test
    fun signUpDashesUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "this-is-a-test",
            password = "password",
            email = "test.signup.dashes@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with username with underscores")
    @Test
    fun signUpUnderscoresUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "this_is_a_test",
            password = "password",
            email = "test.signup.under@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with username with dots")
    @Test
    fun signUpDotsUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "this.is.a.test",
            password = "password",
            email = "test.signup.dots@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with username with digits")
    @Test
    fun signUpDigitsUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "this123",
            password = "password",
            email = "test.signup.digits@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.Created, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        val deleteResponse = client.delete("/api/users/${responseDto.id}") {
            superadmin.first?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    @DisplayName("Signing up with uppercase username")
    @Test
    fun signUpUppercaseUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "THISISATEST",
            password = "password",
            email = "test.signup.upper@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
    }

    @DisplayName("Signing up with username with spaces")
    @Test
    fun signUpSpacesUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "this is a test",
            password = "password",
            email = "test.signup.spaces@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
    }

    @DisplayName("Signing up with username with special characters")
    @Test
    fun signUpSpecialCharactersUsername() = testApplication {
        val newUserDto = UserCreationDTO(
            username = "test\"'()[]{}/+-:;.,?!@#$%^&*|\\`~",
            password = "password",
            email = "test.signup.special@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
    }

    @DisplayName("Signing up with already existing email")
    @Test
    fun signUpExistingMail() = testApplication {
        val dto = UserCreationDTO(
            username = "test",
            password = "Test0@aaa",
            email = "admin@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @DisplayName("Signing up with invalid email")
    @Test
    fun signUpInvalidMail() = testApplication {
        val dto = UserCreationDTO(
            username = "test",
            password = "Test0@aaa",
            email = "myemail"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Signing up with already existing username")
    @Test
    fun signUpExistingUsername() = testApplication {
        val dto = UserCreationDTO(
            username = "admin1",
            password = "Test0@aaa",
            email = "test.username.exists@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @DisplayName("Signing up with invalid username")
    @Test
    fun signUpInvalidUsername() = testApplication {
        val dto = UserCreationDTO(
            username = "inv@lidTEST",
            password = "Test0@aaa",
            email = "test.username.invalid@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Signing up with invalid role")
    @Test
    fun signUpInvalidRole() = testApplication {
        val dto = UserCreationDTO(
            username = "test",
            password = "Test0@aaa",
            email = "test.role.invalid@test.com",
            role = UserRole.ADMIN
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @DisplayName("Updating own username")
    @ParameterizedTest(name = "Updating own username as {0} is {2}")
    @MethodSource
    fun updateOwnUsername(
        user: TestUser,
        newUsername: String,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = newUsername))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newUsername, responseDto.payload!!.username)
        }
    }

    @DisplayName("Updating other's username")
    @ParameterizedTest(name = "Updating other''s username as {0} is {3}")
    @MethodSource
    fun updateOtherUsername(
        user: TestUser,
        userId: UUID,
        newUsername: String,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = newUsername))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newUsername, responseDto.payload!!.username)
        }
    }

    @DisplayName("Updating username with invalid username")
    @Test
    fun updateUsernameInvalid() = testApplication {
        val tokens = login("edit_username_invalid", "password").second ?: throw Exception("Login failed")
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0005-0001-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = "newUsern@me"))
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
    }

    @DisplayName("Updating own email")
    @ParameterizedTest(name = "Updating own email as {0} is {2}")
    @MethodSource
    fun updateOwnEmail(
        user: TestUser,
        newEmail: String,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = newEmail))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newEmail, responseDto.payload!!.email)
        }
    }

    @DisplayName("Updating other's email")
    @ParameterizedTest(name = "Updating other''s email as {0} is {3}")
    @MethodSource
    fun updateOtherEmail(
        user: TestUser,
        userId: UUID,
        newEmail: String,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = newEmail))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newEmail, responseDto.payload!!.email)
        }
    }

    @DisplayName("Updating email with invalid email")
    @Test
    fun updateEmailInvalid() = testApplication {
        val tokens = login("edit_email_invalid", "password").second ?: throw Exception("Login failed")
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0007-0001-000000000001") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = "newEmail"))
            bearerAuth(tokens.accessToken)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status, response.bodyAsText())
    }

    @DisplayName("Updating own password")
    @ParameterizedTest(name = "Updating own password as {0} is {1}")
    @MethodSource
    fun updateOwnPassword(
        user: TestUser,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "password",
                    "P@ssw0rd"
                )
            )
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status)
    }

    @DisplayName("Updating other's password")
    @ParameterizedTest(name = "Updating other''s password as {0} is {2}")
    @MethodSource
    fun updateOtherPassword(
        user: TestUser,
        userId: UUID,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "password",
                    "p@ssw0rd"
                )
            )
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status)
    }

    @DisplayName("Updating own role")
    @ParameterizedTest(name = "Updating own role as {0} to {1} is {2}")
    @MethodSource
    fun updateOwnRole(
        user: TestUser,
        newRole: UserRole,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = newRole))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newRole, responseDto.role)
        }
    }

    @DisplayName("Updating other's role")
    @ParameterizedTest(name = "Updating {1}''s role to {2} as {0} is {3}")
    @MethodSource
    fun updateOtherRole(
        user: TestUser,
        userId: UUID,
        newRole: UserRole,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = newRole))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newRole, responseDto.payload!!.role)
        }
    }

    @DisplayName("Updating own disk quota")
    @ParameterizedTest(name = "Updating own disk quota as {0} is {1}")
    @MethodSource
    fun updateOwnDiskQuota(
        user: TestUser,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = -1))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(-1, responseDto.payload!!.maximumDiskQuota)
        }
    }

    @DisplayName("Updating other's disk quota")
    @ParameterizedTest(name = "Updating {1}''s disk quota as {0} is {2}")
    @MethodSource
    fun updateOtherDiskQuota(
        user: TestUser,
        userId: UUID,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/users/${userId}") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = -1))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
        if (expectedStatus == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(-1, responseDto.payload!!.maximumDiskQuota)
        }
    }

    @DisplayName("Updating own state (`enabled`)")
    @ParameterizedTest(name = "Updating own state as {0} to {1} is 403 Forbidden")
    @MethodSource
    fun updateOwnState(
        user: TestUser,
        newState: Boolean,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, userId) = user
        val client = client()
        val route = if (newState) "/api/users/${userId}/activate" else "/api/users/${userId}/deactivate"
        val response = client.post(route) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status, response.bodyAsText())
    }

    @DisplayName("Updating other's state (`enabled`)")
    @ParameterizedTest(name = "Updating {1}''s state as {0} to {2} is {3}")
    @MethodSource
    fun updateOtherState(
        user: TestUser,
        userId: UUID,
        newState: Boolean,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val route = if (newState) "/api/users/${userId}/activate" else "/api/users/${userId}/deactivate"
        val response = client.post(route) {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status, response.bodyAsText())
    }

    @DisplayName("Updating password with wrong old password")
    @Test
    fun updatePasswordWrong() = testApplication {
        val (loginResponse, tokens) = login("edit_wrong_password", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0012-0000-000000000001/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "wrongPassword",
                    "newPassword"
                )
            )
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @DisplayName("Updating unknown user")
    @Test
    fun updateUnknownUser() = testApplication {
        val (loginResponse, tokens) = login("admin1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)
        val client = client()
        val response = client.patch("/api/users/00000000-0000-0000-0000-00000000000f") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDTO(
                    email = "newEmail@test.com"
                )
            )
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.NotFound, response.status, response.bodyAsText())
    }

    @DisplayName("Deleting user")
    @ParameterizedTest(name = "Deleting user as {0} is {2}")
    @MethodSource
    fun deleteUser(
        user: TestUser,
        userId: UUID,
        expectedStatus: HttpStatusCode
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/users/$userId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedStatus, response.status)
    }

    @DisplayName("Deleting unknown user")
    @Test
    fun deleteUnknownUser() = testApplication {
        val (loginResponse, tokens) = login("admin1", "password")
        assertEquals(HttpStatusCode.Created, loginResponse.status)
        val client = client()
        val response = client.delete("/api/users/ffffffff-ffff-ffff-ffff-ffffffffffff") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
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
                user = Pair(
                    login("user1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000003")
                )
            }
        }

        @JvmStatic
        fun updateOwnUsername(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_username", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0004-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_username", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0004-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_username", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0004-0000-000000000003")
                )
            }

            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), "new_superadmin", HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), "new_admin", HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), "new_user", HttpStatusCode.OK),
                Arguments.of(Named.of("guest", guest), "new_guest", HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateOtherUsername(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    UUID.fromString("00000000-0000-0005-0000-000000000001"),
                    "test5-new_username1",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    UUID.fromString("00000000-0000-0005-0000-000000000002"),
                    "test5-new_username2",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    UUID.fromString("00000000-0000-0005-0000-000000000003"),
                    "test5-new_username3",
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    UUID.fromString("00000000-0000-0005-0000-000000000004"),
                    "test5-new_username4",
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOwnEmail(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_email", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0006-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_email", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0006-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_email", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0006-0000-000000000003")
                )
            }

            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), "Superadmin@newEmail.me", HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), "Admin@newEmail.me", HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), "User@newEmail.me", HttpStatusCode.OK),
                Arguments.of(Named.of("guest", guest), "Guest@newEmail.me", HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateOtherEmail(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    UUID.fromString("00000000-0000-0007-0000-000000000001"),
                    "newEmail1@test7.com",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    UUID.fromString("00000000-0000-0007-0000-000000000002"),
                    "newEmail2@test7.com",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    UUID.fromString("00000000-0000-0007-0000-000000000003"),
                    "newEmail3@test7.com",
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    UUID.fromString("00000000-0000-0007-0000-000000000004"),
                    "newEmail4@test7.com",
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOwnPassword(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_password", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0008-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_password", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0008-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_password", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0008-0000-000000000003")
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
        fun updateOtherPassword(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    UUID.fromString("00000000-0000-0009-0000-000000000001"),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    UUID.fromString("00000000-0000-0009-0000-000000000002"),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    UUID.fromString("00000000-0000-0009-0000-000000000003"),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    UUID.fromString("00000000-0000-0009-0000-000000000004"),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOwnRole(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_role", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0010-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_role", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0010-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_role", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0010-0000-000000000003")
                )
            }

            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOtherRole(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0001-000000000001")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0001-000000000002")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0001-000000000003")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0001-000000000004")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0001-000000000005")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0001-000000000006")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0002-000000000001")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0002-000000000002")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0002-000000000003")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0002-000000000004")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0002-000000000005")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0002-000000000006")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0003-000000000001")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0003-000000000002")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0003-000000000003")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0003-000000000004")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0003-000000000005")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0003-000000000006")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0004-000000000001")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UUID.fromString("00000000-0000-0011-0004-000000000002")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0004-000000000003")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UUID.fromString("00000000-0000-0011-0004-000000000004")),
                    Named.of("regular", UserRole.REGULAR),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0004-000000000005")),
                    Named.of("owner", UserRole.OWNER),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UUID.fromString("00000000-0000-0011-0004-000000000006")),
                    Named.of("admin", UserRole.ADMIN),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOwnDiskQuota(): Stream<Arguments> {
            lateinit var superadmin: TestUser
            lateinit var admin: TestUser
            lateinit var user: TestUser

            testApplication {
                superadmin = Pair(
                    login("owner_edit_quota", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0019-0000-000000000001")
                )
                admin = Pair(
                    login("admin_edit_quota", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0019-0000-000000000002")
                )
                user = Pair(
                    login("user_edit_quota", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0019-0000-000000000003")
                )
            }

            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOtherDiskQuota(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("owner", UUID.fromString("00000000-0000-0020-0001-000000000001")),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UUID.fromString("00000000-0000-0020-0001-000000000003")),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UUID.fromString("00000000-0000-0020-0001-000000000005")),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UUID.fromString("00000000-0000-0020-0002-000000000001")),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("admin", UUID.fromString("00000000-0000-0020-0002-000000000003")),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UUID.fromString("00000000-0000-0020-0002-000000000005")),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UUID.fromString("00000000-0000-0020-0003-000000000001")),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UUID.fromString("00000000-0000-0020-0003-000000000003")),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("regular", UUID.fromString("00000000-0000-0020-0003-000000000005")),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UUID.fromString("00000000-0000-0020-0004-000000000001")),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UUID.fromString("00000000-0000-0020-0004-000000000003")),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UUID.fromString("00000000-0000-0020-0004-000000000005")),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun updateOwnState(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
            )
        }

        @JvmStatic
        fun updateOtherState(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0001-000000000001")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0001-000000000001")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0001-000000000002")),
                    Named.of("enabled", true),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0001-000000000002")),
                    Named.of("disabled", false),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0001-000000000003")),
                    Named.of("enabled", true),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("owner", superadmin),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0001-000000000003")),
                    Named.of("disabled", false),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0002-000000000001")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0002-000000000001")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0002-000000000002")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0002-000000000002")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0002-000000000003")),
                    Named.of("enabled", true),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0002-000000000003")),
                    Named.of("disabled", false),
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0003-000000000001")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0003-000000000001")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0003-000000000002")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0003-000000000002")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0003-000000000003")),
                    Named.of("enabled", true),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0003-000000000003")),
                    Named.of("disabled", false),
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0004-000000000001")),
                    Named.of("enabled", true),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("owner", UUID.fromString("00000000-0000-0013-0004-000000000001")),
                    Named.of("disabled", false),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0004-000000000002")),
                    Named.of("enabled", true),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("admin", UUID.fromString("00000000-0000-0013-0004-000000000002")),
                    Named.of("disabled", false),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0004-000000000003")),
                    Named.of("enabled", true),
                    HttpStatusCode.Unauthorized
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    Named.of("regular", UUID.fromString("00000000-0000-0013-0004-000000000003")),
                    Named.of("disabled", false),
                    HttpStatusCode.Unauthorized
                ),
            )
        }

        @JvmStatic
        fun deleteUser(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    "00000000-0000-0014-0000-000000000001",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    "00000000-0000-0014-0000-000000000002",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    "00000000-0000-0014-0000-000000000003",
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    "00000000-0000-0014-0000-000000000004",
                    HttpStatusCode.Unauthorized
                ),
            )
        }
    }
}
