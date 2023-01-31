package com.kamelia.jellyfish.rest

import com.kamelia.jellyfish.TestUser
import com.kamelia.jellyfish.client
import com.kamelia.jellyfish.login
import com.kamelia.jellyfish.rest.user.UserDTO
import com.kamelia.jellyfish.rest.user.UserPasswordUpdateDTO
import com.kamelia.jellyfish.rest.user.UserRepresentationDTO
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.UserUpdateDTO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.util.UUID
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
import kotlin.test.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    @DisplayName("Signing up")
    @Test
    fun signUp() = testApplication {
        val newUserDto = UserDTO(
            username = "test",
            password = "Test0@aaa",
            email = "test@test.com"
        )
        val client = client()
        val response = client.post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
        assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertEquals(newUserDto.username, responseDto.username)
        assertEquals(newUserDto.email, responseDto.email)
        assertEquals(UserRole.REGULAR, responseDto.role)

        client.delete("/api/users/${responseDto.id}")
    }

    @DisplayName("Signing up with already existing email")
    @Test
    fun signUpExistingMail() = testApplication {
        val dto = UserDTO(
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
        val dto = UserDTO(
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
        val dto = UserDTO(
            username = "admin",
            password = "Test0@aaa",
            email = "test@test.com"
        )
        val response = client().post("/api/users/signup") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @DisplayName("Signing up with invalid role")
    @Test
    fun signUpInvalidRole() = testApplication {
        val dto = UserDTO(
            username = "test",
            password = "Test0@aaa",
            email = "test@test.com",
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
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newUsername, responseDto.username)
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
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newUsername, responseDto.username)
        }
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
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newEmail, responseDto.email)
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
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newEmail, responseDto.email)
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

    @DisplayName("Updating password with wrong old password")
    @Test
    fun updatePasswordWrongOldPassword() = testApplication {
        val (tokens, userId) = superadmin
        val client = client()
        val response = client.patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(
                UserPasswordUpdateDTO(
                    "wrong",
                    "P@ssw0rd"
                )
            )
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
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
            val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
            assertEquals(newRole, responseDto.role)
        }
    }

    @DisplayName("Updating password with wrong old password")
    @Test
    fun updatePasswordWrong() = testApplication {
        val (status, tokens) = login("edit_wrong_password", "password")
        assertEquals(HttpStatusCode.OK, status)
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
        val (status, tokens) = login("admin", "password")
        assertEquals(HttpStatusCode.OK, status)
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
        val (status, tokens) = login("admin", "password")
        assertEquals(HttpStatusCode.OK, status)
        val client = client()
        val response = client.delete("/api/users/ffffffff-ffff-ffff-ffff-ffffffffffff") {
            bearerAuth(tokens!!.accessToken)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Regenerating token gives a new token")
    @Test
    fun regenerateToken() = testApplication {
        val tokens = login("regenerate_token", "password").second ?: throw Exception("Login failed")
        val client = client()
        val response = client.post("/api/users/uploadToken") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = "newEmail"))
            bearerAuth(tokens.accessToken)
        }

        assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString(UserRepresentationDTO.serializer(), response.bodyAsText())
        assertNotEquals("0123456789abdcef0123456789abdcef", responseDto.uploadToken)
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                superadmin = Pair(
                    login("owner", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin", "password").second ?: throw Exception("Login failed"),
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
                Arguments.of(Named.of("owner", superadmin), "newSuperadmin", HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), "newAdmin", HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), "newUser", HttpStatusCode.OK),
                Arguments.of(Named.of("guest", guest), "newGuest", HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateOtherUsername(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("owner", superadmin),
                    UUID.fromString("00000000-0000-0005-0000-000000000001"),
                    "test5-newUsername1",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("admin", admin),
                    UUID.fromString("00000000-0000-0005-0000-000000000002"),
                    "test5-newUsername2",
                    HttpStatusCode.OK
                ),
                Arguments.of(
                    Named.of("regular user", user),
                    UUID.fromString("00000000-0000-0005-0000-000000000003"),
                    "test5-newUsername3",
                    HttpStatusCode.Forbidden
                ),
                Arguments.of(
                    Named.of("guest", guest),
                    UUID.fromString("00000000-0000-0005-0000-000000000004"),
                    "test5-newUsername4",
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
