package com.kamelia.hedera.rest.user

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.uuid
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Users tests")
class UsersTests {

    @DisplayName("Users tests as owner")
    @Nested
    inner class OwnerUsersTests : AbstractUsersSelfTests(
        owner,
        ownerExpectedResults,
        ownerInput,
        selfRole = UserRole.OWNER,
    )

    @DisplayName("Users tests as admin")
    @Nested
    inner class AdminUsersTests : AbstractUsersSelfTests(
        admin,
        adminExpectedResults,
        adminInput,
        selfRole = UserRole.ADMIN,
    )

    @DisplayName("Users tests as regular user")
    @Nested
    inner class RegularUserUsersTests : AbstractUsersSelfTests(
        user,
        regularUserExpectedResults,
        regularUserInput,
        selfRole = UserRole.REGULAR,
    )

    @DisplayName("Users tests as guest")
    @Nested
    inner class GuestUsersTests : AbstractUsersTests(
        guest,
        guestExpectedResults,
        guestInput,
    )

    companion object {

        private lateinit var owner: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                owner = Pair(
                    login("users.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("users.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0002-000000000000".uuid()
                )
                user = Pair(
                    login("users.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0003-000000000000".uuid()
                )
            }
        }
    }
}