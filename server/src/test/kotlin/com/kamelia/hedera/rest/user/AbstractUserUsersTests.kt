package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUserUsersTests(
    private val user: TestUser,
    private val expectedResults: UsersTestsExpectedResults,
    private val input: UsersTestsInput,
) : AbstractUsersTests(user, expectedResults, input) {

    @DisplayName("Edit own username")
    @Test
    fun editOwnUsernameTest() = testApplication {
    }

    @DisplayName("Edit own email address")
    @Test
    fun editOwnEmailTest() = testApplication {
    }

    @DisplayName("Edit own role")
    @ParameterizedTest(name = "Edit own role to ''{0}''")
    @MethodSource("roles")
    fun editOwnRoleTest(
        newRole: UserRole,
    ) = testApplication {
    }

    @DisplayName("Edit own disk space quota")
    @Test
    fun editOwnDiskQuotaTest() = testApplication {
    }

    @DisplayName("Edit own password")
    @Test
    fun editOwnPasswordTest() = testApplication {
    }

    @DisplayName("Activate self")
    @Test
    fun activateSelfTest() = testApplication {
    }

    @DisplayName("Deactivate self")
    @Test
    fun deactivateSelfTest() = testApplication {
    }

    @DisplayName("Delete self")
    @Test
    fun deleteSelfTest() = testApplication {
    }

}

open class UserUsersTestsExpectedResults(
    listUsers: HttpStatusCode,
    createUser: Map<UserRole, HttpStatusCode>,
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

open class UserUsersTestsInput(
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