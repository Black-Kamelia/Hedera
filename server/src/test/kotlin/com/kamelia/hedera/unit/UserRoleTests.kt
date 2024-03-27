package com.kamelia.hedera.unit

import com.kamelia.hedera.rest.user.UserRole
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("User role tests")
class UserRoleTests {

    @DisplayName("Find user role by name")
    @Test
    fun findUserRoleByName() {
        val role = UserRole.valueOfOrNull("ADMIN")
        assertEquals(UserRole.ADMIN, role)
    }

    @DisplayName("Find unknown user role by name returns null")
    @Test
    fun findUnknownUserRoleByNameReturnsNull() {
        val role = UserRole.valueOfOrNull("UNKNOWN_ROLE")
        assertEquals(null, role)
    }

    @DisplayName("Compare user roles by power (operator: {0})")
    @ParameterizedTest(name = "{1} {0} {2} is {3}")
    @MethodSource
    fun compareUserRolesByPower(
        operator: (UserRole, UserRole) -> Boolean,
        role1: UserRole,
        role2: UserRole,
        expectedResult: Boolean,
    ) = assertEquals(expectedResult, operator(role1, role2))

    companion object {

        @JvmStatic
        fun compareUserRolesByPower(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of(">=") { a: UserRole, b: UserRole -> a ge b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of(">=") { a: UserRole, b: UserRole -> a ge b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of(">=") { a: UserRole, b: UserRole -> a ge b },
                UserRole.OWNER,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of(">") { a: UserRole, b: UserRole -> a gt b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of(">") { a: UserRole, b: UserRole -> a gt b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of(">") { a: UserRole, b: UserRole -> a gt b },
                UserRole.OWNER,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("<=") { a: UserRole, b: UserRole -> a le b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("<=") { a: UserRole, b: UserRole -> a le b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("<=") { a: UserRole, b: UserRole -> a le b },
                UserRole.OWNER,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of("<") { a: UserRole, b: UserRole -> a lt b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("<") { a: UserRole, b: UserRole -> a lt b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of("<") { a: UserRole, b: UserRole -> a lt b },
                UserRole.OWNER,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of("==") { a: UserRole, b: UserRole -> a eq b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                false
            ),
            Arguments.of(
                Named.of("==") { a: UserRole, b: UserRole -> a eq b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("!=") { a: UserRole, b: UserRole -> a ne b },
                UserRole.REGULAR,
                UserRole.ADMIN,
                true
            ),
            Arguments.of(
                Named.of("!=") { a: UserRole, b: UserRole -> a ne b },
                UserRole.ADMIN,
                UserRole.ADMIN,
                false
            ),
        )
    }

}