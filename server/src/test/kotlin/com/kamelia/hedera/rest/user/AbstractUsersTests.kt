package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import io.ktor.server.testing.*
import java.util.stream.Stream
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
    }

    @DisplayName("Create user")
    @ParameterizedTest(name = "Create user with role ''{0}''")
    @MethodSource("roles")
    fun createUserTest(
        role: UserRole
    ) = testApplication {
    }

    @DisplayName("Edit others username")
    @ParameterizedTest(name = "Edit {0}''s username")
    @MethodSource("roles")
    fun editOthersUsernameTest(
        target: UserRole,
    ) = testApplication {
    }

    @DisplayName("Edit others email address")
    @ParameterizedTest(name = "Edit {0}''s email address")
    @MethodSource("roles")
    fun editOthersEmailTest(
        target: UserRole,
    ) = testApplication {
    }

    @DisplayName("Edit others role")
    @ParameterizedTest(name = "Edit {0}''s role to ''{1}''")
    @MethodSource("rolesRolesCombo")
    fun editOthersRoleTest(
        target: UserRole,
        newRole: UserRole,
    ) = testApplication {
    }

    @DisplayName("Edit others disk space quota")
    @ParameterizedTest(name = "Edit {0}''s disk space quota")
    @MethodSource("roles")
    fun editOthersDiskQuotaTest(
        target: UserRole,
    ) = testApplication {
    }

    @DisplayName("Edit others password")
    @ParameterizedTest(name = "Edit {0}''s password")
    @MethodSource("roles")
    fun editOthersPasswordTest(
        target: UserRole
    ) = testApplication {
    }

    @DisplayName("Activate someone")
    @ParameterizedTest(name = "Activate {0}")
    @MethodSource("roles")
    fun activateSomeoneTest(
        target: UserRole,
    ) = testApplication {
    }

    @DisplayName("Deactivate someone")
    @ParameterizedTest(name = "Deactivate {0}")
    @MethodSource("roles")
    fun deactivateSomeoneTest(
        target: UserRole,
    ) = testApplication {
    }

    @DisplayName("Delete someone")
    @ParameterizedTest(name = "Delete {0}")
    @MethodSource("roles")
    fun deleteSomeoneTest(
        target: UserRole,
    ) = testApplication {
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

open class UsersTestsExpectedResults(

)

open class UsersTestsInput(

)