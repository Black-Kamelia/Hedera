package com.kamelia.hedera.flop

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

import com.kamelia.hedera.rest.user.UserRole.*

class WholeUserTest {

    @DisplayName("User tests as admin")
    @Nested
    inner class UserTest : AbstractUserTest(
        user0,
        UserTestExpectedResults(
            userCreation = mapOf(REGULAR to true, ADMIN to false, OWNER to false),
            userEdition = mapOf(REGULAR to true, ADMIN to false, OWNER to false),
        ),
    )

    @DisplayName("User tests as owner")
    @Nested
    inner class UserTest2 : AbstractUserTest(
        user1,
        UserTestExpectedResults(
            userCreation = mapOf(REGULAR to true, ADMIN to true, OWNER to false),
            userEdition = mapOf(REGULAR to true, ADMIN to true, OWNER to true),
        ),
    )

    @DisplayName("User tests as regular user")
    @Nested
    inner class UserTest3 : AbstractUserTest(
        user2,
        UserTestExpectedResults(
            userCreation = mapOf(REGULAR to false, ADMIN to false, OWNER to false),
            userEdition = mapOf(REGULAR to false, ADMIN to false, OWNER to false),
        ),
    )

}