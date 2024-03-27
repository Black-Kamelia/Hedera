package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.configuration.GlobalConfigurationService
import com.kamelia.hedera.rest.configuration.GlobalConfigurationUpdateDTO
import com.kamelia.hedera.rest.configuration.isolateGlobalConfiguration
import com.kamelia.hedera.uuid
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Users edge cases tests")
class UsersEdgeCasesTests {

    @DisplayName("Create user with username too long")
    @Test
    fun createUserWithUsernameTooLong() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "long".repeat(256),
            password = "password",
            email = "user.create.username.too.long@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Username.TOO_LONG, responseDto.fields!!["username"]!!.key)
    }

    @DisplayName("Create user with username too short")
    @Test
    fun createUserWithUsernameTooShort() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "a",
            password = "password",
            email = "user.create.username.too.short@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Username.TOO_SHORT, responseDto.fields!!["username"]!!.key)
    }

    @DisplayName("Create user with invalid username")
    @Test
    fun createUserWithInvalidUsername() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "invalid@user-NAME",
            password = "password",
            email = "user.create.username.invalid@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Username.INVALID_USERNAME, responseDto.fields!!["username"]!!.key)
    }

    @DisplayName("Create user with already existing username")
    @Test
    fun createUserWithAlreadyExistingUsername() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "user.create.username.already.exists",
            password = "password",
            email = "user.create.username.already.exists@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Username.ALREADY_EXISTS, responseDto.fields!!["username"]!!.key)
    }

    @DisplayName("Create user with password too long")
    @Test
    fun createUserWithPasswordTooLong() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "username.long.password",
            password = "password".repeat(100),
            email = "username.long.password@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Password.TOO_LONG, responseDto.fields!!["password"]!!.key)
    }

    @DisplayName("Create user with password too short")
    @Test
    fun createUserWithPasswordTooShort() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "username.short.password",
            password = "a",
            email = "username.short.password@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Password.TOO_SHORT, responseDto.fields!!["password"]!!.key)
    }

    @DisplayName("Create user with e-mail too long")
    @Test
    fun createUserWithEmailTooLong() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "user.long.email",
            password = "password",
            email = "user".repeat(100) + "@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Email.TOO_LONG, responseDto.fields!!["email"]!!.key)
    }

    @DisplayName("Create user with invalid e-mail")
    @Test
    fun createUserWithInvalidEmail() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "user.invalid.email",
            password = "password",
            email = "invalid.email",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Email.INVALID_EMAIL, responseDto.fields!!["email"]!!.key)
    }

    @DisplayName("Create user with already existing e-mail")
    @Test
    fun createUserWithAlreadyExistingEmail() = testApplication {
        val (tokens, _) = owner

        val userDto = UserCreationDTO(
            username = "user.create.email.exists",
            password = "password",
            email = "user.create.email.already.exists@test.com",
        )
        val response = client().post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.Email.ALREADY_EXISTS, responseDto.fields!!["email"]!!.key)
    }

    @DisplayName("Sign up while registrations are disabled")
    @Test
    fun signUpWhileRegistrationsDisabled() = testApplication {
        isolateGlobalConfiguration {
            GlobalConfigurationService.updateConfiguration(
                GlobalConfigurationUpdateDTO(enableRegistrations = false)
            )

            val response = client().post("/api/users/signup") {
                contentType(ContentType.Application.Json)
                setBody(
                    UserCreationDTO(
                        username = "user.registrations.disabled",
                        password = "password",
                        email = "user.registrations.disabled@test.local"
                    )
                )
            }
            assertEquals(HttpStatusCode.Forbidden, response.status)

            val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
            assertEquals(Errors.Registrations.DISABLED, responseDto.title.key)
        }
    }

    @DisplayName("Sign up with role higher than regular")
    @Test
    fun signUpWithRoleHigherThanRegular() = testApplication {
        isolateGlobalConfiguration {
            GlobalConfigurationService.updateConfiguration(
                GlobalConfigurationUpdateDTO(enableRegistrations = true)
            )

            val response = client().post("/api/users/signup") {
                contentType(ContentType.Application.Json)
                setBody(
                    UserCreationDTO(
                        username = "user.role.higher",
                        password = "password",
                        email = "user.role.higher@test.local",
                        role = UserRole.ADMIN
                    )
                )
            }
            assertEquals(HttpStatusCode.Forbidden, response.status)

            val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
            assertEquals(Errors.ILLEGAL_ACTION, responseDto.title.key)
        }
    }

    @DisplayName("Edit unknown user")
    @Test
    fun editUnknownUser() = testApplication {
        val (tokens, _) = owner

        val response = client().patch("/api/users/00000000-0000-0000-0000-000000000000") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = "new.username"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Edit own disk quota as non-owner")
    @Test
    fun editOwnDiskQuotaNonOwner() = testApplication {
        val (tokens, _) = regular

        val response = client().patch("/api/users/ffffffff-0003-0000-0003-000000000000") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = 42000))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.ILLEGAL_ACTION, responseDto.title.key)
    }

    @DisplayName("Activate unknown user")
    @Test
    fun activateUnknownUser() = testApplication {
        val (tokens, _) = owner

        val response = client().post("/api/users/00000000-0000-0000-0000-000000000000/activate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Deactivate unknown user")
    @Test
    fun deactivateUnknownUser() = testApplication {
        val (tokens, _) = owner

        val response = client().post("/api/users/00000000-0000-0000-0000-000000000000/deactivate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Change password without old password")
    @Test
    fun changePasswordWithoutOldPassword() = testApplication {
        val (tokens, userId) = owner

        val response = client().patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(UserPasswordUpdateDTO(newPassword = "new.password"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Actions.Users.UpdatePassword.fail.title.key, responseDto.title.key)
        assertEquals(Errors.Users.Password.MISSING_OLD_PASSWORD, responseDto.fields!!["oldPassword"]!!.key)
    }

    @DisplayName("Change password with incorrect old password")
    @Test
    fun changePasswordWithIncorrectOldPassword() = testApplication {
        val (tokens, userId) = owner

        val response = client().patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(UserPasswordUpdateDTO(oldPassword = "incorrect.password", newPassword = "new.password"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Actions.Users.UpdatePassword.fail.title.key, responseDto.title.key)
        assertEquals(Errors.Users.Password.INCORRECT_PASSWORD, responseDto.fields!!["oldPassword"]!!.key)
    }

    @DisplayName("Delete unknown user")
    @Test
    fun deleteUnknownUser() = testApplication {
        val (tokens, _) = owner

        val response = client().delete("/api/users/00000000-0000-0000-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Delete owner")
    @Test
    fun deleteOwner() = testApplication {
        val (tokens, _) = owner

        val response = client().delete("/api/users/00000000-000a-0003-0000-000000000000") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.ILLEGAL_ACTION, responseDto.title.key)
    }

    @DisplayName("Update disk quota with incorrect quota")
    @Test
    fun updateDiskQuotaWithIncorrectQuota() = testApplication {
        val (tokens, _) = owner

        val response = client().patch("/api/users/00000000-000a-0004-0000-000000000000") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = -10))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.DiskQuota.INVALID_DISK_QUOTA, responseDto.fields!!["diskQuota"]!!.key)
    }

    @DisplayName("Get unknown user")
    @Test
    fun getUnknownUser() = testApplication {
        val (tokens, _) = owner

        val response = client().get("/api/users/${UUID(0, 0)}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status, response.bodyAsText())

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.Users.NOT_FOUND, responseDto.title.key)
    }

    companion object {

        private lateinit var owner: TestUser
        private lateinit var regular: TestUser

        init {
            testApplication {
                owner = Pair(
                    login("users.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0001-000000000000".uuid()
                )
                regular = Pair(
                    login("users.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0003-000000000000".uuid()
                )
            }
        }
    }

}