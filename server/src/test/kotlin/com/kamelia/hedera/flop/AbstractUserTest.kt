package com.kamelia.hedera.flop

import com.kamelia.hedera.rest.user.UserRole
import java.util.stream.Stream
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UserTestExpectedResults(
    val userCreation: Map<UserRole, Boolean>,
    val userEdition: Map<UserRole, Boolean>,
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUserTest(
    private val user: String,
    private val expectedResults: UserTestExpectedResults
) {

    @DisplayName("Editing a user")
    @ParameterizedTest(name = "Editing {0}")
    @MethodSource
    fun test3(role: UserRole) {
        val expected = expectedResults.userEdition.getValue(role)
        println("$user can edit $role: $expected")
    }

    @DisplayName("Creating a user")
    @ParameterizedTest(name = "Creating {0}")
    @MethodSource
    fun test2(role: UserRole) {
        val expected = expectedResults.userCreation.getValue(role)
        println("$user can edit $role: $expected")
    }

    companion object {

        const val user0 = "admin"
        const val user1 = "owner"
        const val user2 = "regular user"

        @JvmStatic
        fun test3(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("regular user", UserRole.REGULAR)),
            Arguments.of(Named.of("admin", UserRole.ADMIN)),
            Arguments.of(Named.of("owner", UserRole.OWNER)),
        )

        @JvmStatic
        fun test2(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("regular user", UserRole.REGULAR)),
            Arguments.of(Named.of("admin", UserRole.ADMIN)),
            Arguments.of(Named.of("owner", UserRole.OWNER)),
        )

    }
}
