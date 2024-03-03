package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
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
abstract class AbstractUsersTests(
    private val user: TestUser,
    private val expectedResults: UsersTestsExpectedResults,
    private val input: UsersTestsInput,
) {

    @DisplayName("List users")
    @Test
    fun listUsersTest() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.listUsers, response.status, response.bodyAsText())
    }

    @DisplayName("Create user")
    @ParameterizedTest(name = "Create user with role ''{0}''")
    @MethodSource("roles")
    fun createUserTest(
        role: UserRole
    ) = testApplication {
        val (tokens, userId) = user
        val userDto = UserCreationDTO(
            username = "$userId-create-$role-user".lowercase(),
            password = "password",
            email = "$userId-create-$role@test.local".lowercase(),
            role = role,
        )
        val client = client()
        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.createUser[role], response.status, response.bodyAsText())

        if (response.status == HttpStatusCode.Created) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(userDto.username, responseDto.payload!!.username)
            assertEquals(userDto.email, responseDto.payload!!.email)
            assertEquals(role, responseDto.payload!!.role)
        }
    }

    @DisplayName("Edit others username")
    @ParameterizedTest(name = "Edit {0}''s username")
    @MethodSource("roles")
    fun editOthersUsernameTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, id) = user
        val newUsername = "$id-edit-$target-username".lowercase()
        val client = client()
        val userId = input.updateOthersUsernameUserId[target]!!
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = newUsername))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOthersUsername[target], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newUsername, responseDto.payload!!.username)
        }
    }

    @DisplayName("Edit others email address")
    @ParameterizedTest(name = "Edit {0}''s email address")
    @MethodSource("roles")
    fun editOthersEmailTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, id) = user
        val newEmail = "$id-edit-$target-email@test.local".lowercase()
        val client = client()
        val userId = input.updateOthersEmailUserId[target]!!
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = newEmail))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOthersEmail[target], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newEmail, responseDto.payload!!.email)
        }
    }

    @DisplayName("Edit others role")
    @ParameterizedTest(name = "Edit {0}''s role to ''{1}''")
    @MethodSource("rolesRolesCombo")
    fun editOthersRoleTest(
        target: UserRole,
        newRole: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.updateOthersRoleUserId[target]!![newRole]!!
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = newRole))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOthersRole[target]!![newRole], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newRole, responseDto.payload!!.role)
        }
    }

    @DisplayName("Edit others disk space quota")
    @ParameterizedTest(name = "Edit {0}''s disk space quota")
    @MethodSource("roles")
    fun editOthersDiskQuotaTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.updateOthersQuotaUserId[target]!!
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = 5000))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOthersQuota[target], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(5000, responseDto.payload!!.maximumDiskQuota)
        }
    }

    @DisplayName("Edit others password")
    @ParameterizedTest(name = "Edit {0}''s password")
    @MethodSource("roles")
    fun editOthersPasswordTest(
        target: UserRole
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.updateOthersPasswordUserId[target]!!
        val response = client.patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(UserPasswordUpdateDTO(oldPassword = "password", newPassword = "new-password"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOthersPassword[target], response.status, response.bodyAsText())
    }

    @DisplayName("Activate user")
    @ParameterizedTest(name = "Activate {0}")
    @MethodSource("roles")
    fun activateUserTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.activateUserId[target]!!
        val response = client.post("/api/users/$userId/activate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.activateUser[target], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(true, responseDto.payload!!.enabled)
        }
    }

    @DisplayName("Deactivate user")
    @ParameterizedTest(name = "Deactivate {0}")
    @MethodSource("roles")
    fun deactivateUserTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.deactivateUserId[target]!!
        val response = client.post("/api/users/$userId/deactivate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deactivateUser[target], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(false, responseDto.payload!!.enabled)
        }
    }

    @DisplayName("Delete user")
    @ParameterizedTest(name = "Delete {0}")
    @MethodSource("roles")
    fun deleteUserTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val userId = input.deleteUserId[target]!!
        val response = client.delete("/api/users/$userId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteUser[target], response.status, response.bodyAsText())
    }

    companion object {

        private val roles = listOf(
            Named.of("owner", UserRole.OWNER),
            Named.of("admin", UserRole.ADMIN),
            Named.of("regular user", UserRole.REGULAR),
        )

        @JvmStatic
        fun roles(): Stream<Arguments> {
            return roles.map{ Arguments.of(it) }.stream()
        }

        @JvmStatic
        fun rolesRolesCombo(): Stream<Arguments> {
            return roles.flatMap { role1 ->
                roles.map { role2 ->
                    Arguments.of(role1, role2)
                }
            }.stream()
        }
    }
}
