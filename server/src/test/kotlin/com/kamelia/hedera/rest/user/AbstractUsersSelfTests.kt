package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.login
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUsersSelfTests(
    user: TestUser,
    private val expectedResults: SelfUsersTestsExpectedResults,
    private val input: SelfUsersTestsInput,
    selfRole: UserRole,
) : AbstractUsersTests(user, expectedResults, input) {

    private val role = selfRole.name.lowercase()
    private lateinit var usernameUser: TestUser
    private lateinit var emailUser: TestUser
    private lateinit var roleUser: Map<UserRole, TestUser>
    private lateinit var quotaUser: TestUser
    private lateinit var passwordUser: TestUser
    private lateinit var activationUser: TestUser
    private lateinit var deactivationUser: TestUser
    private lateinit var deletionUser: TestUser

    init {
        testApplication {
            usernameUser = Pair(
                login("$role.edit.username", "password").second ?: throw Exception("Login failed"),
                input.updateOwnUsernameUserId
            )
            emailUser = Pair(
                login("$role.edit.email", "password").second ?: throw Exception("Login failed"),
                input.updateOwnEmailUserId
            )
            roleUser = UserRole.entries.associateWith {
                Pair(
                    login("$role.edit.role.${it.name}".lowercase(), "password").second ?: throw Exception("Login failed"),
                    input.updateOwnRoleUserId[it] ?: throw Exception("Missing user ID for role $role")
                )
            }
            quotaUser = Pair(
                login("$role.edit.quota", "password").second ?: throw Exception("Login failed"),
                input.updateOwnQuotaUserId
            )
            passwordUser = Pair(
                login("$role.edit.password", "password").second ?: throw Exception("Login failed"),
                input.updateOwnPasswordUserId
            )
            activationUser = Pair(
                login("$role.activate", "password").second ?: throw Exception("Login failed"),
                input.activateSelfUserId
            )
            deactivationUser = Pair(
                login("$role.deactivate", "password").second ?: throw Exception("Login failed"),
                input.deactivateSelfUserId
            )
            deletionUser = Pair(
                login("$role.delete", "password").second ?: throw Exception("Login failed"),
                input.deleteSelfUserId
            )
        }
    }

    @DisplayName("Edit own username")
    @Test
    fun editOwnUsernameTest() = testApplication {
        val (tokens, userId) = usernameUser
        val newUsername = "$userId-edit-username".lowercase()
        val client = client()
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(username = newUsername))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOwnUsername, response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newUsername, responseDto.payload!!.username)
        }
    }

    @DisplayName("Edit own email address")
    @Test
    fun editOwnEmailTest() = testApplication {
        val (tokens, userId) = emailUser
        val newEmail = "$userId-edit-email@test.local".lowercase()
        val client = client()
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(email = newEmail))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOwnEmail, response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newEmail, responseDto.payload!!.email)
        }
    }

    @DisplayName("Edit own role")
    @ParameterizedTest(name = "Edit own role to ''{0}''")
    @MethodSource("roles")
    fun editOwnRoleTest(
        newRole: UserRole,
    ) = testApplication {
        val (tokens, userId) = roleUser[newRole]!!
        val client = client()
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(role = newRole))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOwnRole[newRole], response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(newRole, responseDto.payload!!.email)
        }
    }

    @DisplayName("Edit own disk space quota")
    @Test
    fun editOwnDiskQuotaTest() = testApplication {
        val (tokens, userId) = quotaUser
        val client = client()
        val response = client.patch("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(UserUpdateDTO(diskQuota = 10000))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOwnQuota, response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(10000, responseDto.payload!!.maximumDiskQuota)
        }
    }

    @DisplayName("Edit own password")
    @Test
    fun editOwnPasswordTest() = testApplication {
        val (tokens, userId) = passwordUser
        val client = client()
        val response = client.patch("/api/users/$userId/password") {
            contentType(ContentType.Application.Json)
            setBody(UserPasswordUpdateDTO(oldPassword = "password", newPassword = "new-password"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.updateOwnPassword, response.status, response.bodyAsText())
    }

    @DisplayName("Activate self")
    @Test
    fun activateSelfTest() = testApplication {
        val (tokens, userId) = activationUser
        val client = client()
        val response = client.post("/api/users/$userId/activate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.activateSelf, response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(true, responseDto.payload!!.enabled)
        }
    }

    @DisplayName("Deactivate self")
    @Test
    fun deactivateSelfTest() = testApplication {
        val (tokens, userId) = deactivationUser
        val client = client()
        val response = client.post("/api/users/$userId/deactivate") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deactivateSelf, response.status, response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<UserRepresentationDTO>>(response.bodyAsText())
            assertEquals(false, responseDto.payload!!.enabled)
        }
    }

    @DisplayName("Delete self")
    @Test
    fun deleteSelfTest() = testApplication {
        val (tokens, userId) = deletionUser
        val client = client()
        val response = client.delete("/api/users/$userId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteSelf, response.status, response.bodyAsText())
    }

}

open class SelfUsersTestsExpectedResults(
    listUsers: HttpStatusCode,
    createUser: Map<UserRole, HttpStatusCode>,
    val updateOwnUsername: HttpStatusCode,
    val updateOwnEmail: HttpStatusCode,
    val updateOwnRole: Map<UserRole, HttpStatusCode>,
    val updateOwnQuota: HttpStatusCode,
    val updateOwnPassword: HttpStatusCode,
    val activateSelf: HttpStatusCode,
    val deactivateSelf: HttpStatusCode,
    val deleteSelf: HttpStatusCode,

    updateOthersUsername: Map<UserRole, HttpStatusCode>,
    updateOthersEmail: Map<UserRole, HttpStatusCode>,
    updateOthersRole: Map<UserRole, Map<UserRole, HttpStatusCode>>,
    updateOthersQuota: Map<UserRole, HttpStatusCode>,
    updateOthersPassword: Map<UserRole, HttpStatusCode>,
    activateUser: Map<UserRole, HttpStatusCode>,
    deactivateUser: Map<UserRole, HttpStatusCode>,
    deleteUser: Map<UserRole, HttpStatusCode>,
) : UsersTestsExpectedResults(
    listUsers,
    createUser,
    updateOthersUsername,
    updateOthersEmail,
    updateOthersRole,
    updateOthersQuota,
    updateOthersPassword,
    activateUser,
    deactivateUser,
    deleteUser,
)

open class SelfUsersTestsInput(
    val updateOwnUsernameUserId: UUID,
    val updateOwnEmailUserId: UUID,
    val updateOwnRoleUserId: Map<UserRole, UUID>,
    val updateOwnQuotaUserId: UUID,
    val updateOwnPasswordUserId: UUID,
    val activateSelfUserId: UUID,
    val deactivateSelfUserId: UUID,
    val deleteSelfUserId: UUID,

    updateOthersUsernameUserId: Map<UserRole, UUID>,
    updateOthersEmailUserId: Map<UserRole, UUID>,
    updateOthersRoleUserId: Map<UserRole, Map<UserRole, UUID>>,
    updateOthersQuotaUserId: Map<UserRole, UUID>,
    updateOthersPasswordUserId: Map<UserRole, UUID>,
    activateUserId: Map<UserRole, UUID>,
    deactivateUserId: Map<UserRole, UUID>,
    deleteUserId: Map<UserRole, UUID>,
) : UsersTestsInput(
    updateOthersUsernameUserId,
    updateOthersEmailUserId,
    updateOthersRoleUserId,
    updateOthersQuotaUserId,
    updateOthersPasswordUserId,
    activateUserId,
    deactivateUserId,
    deleteUserId,
)