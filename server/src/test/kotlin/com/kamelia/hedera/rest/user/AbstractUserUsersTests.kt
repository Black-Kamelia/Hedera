package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import io.ktor.server.testing.*
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

) : UsersTestsExpectedResults(

)

open class UserUsersTestsInput(

) : UsersTestsInput(

)