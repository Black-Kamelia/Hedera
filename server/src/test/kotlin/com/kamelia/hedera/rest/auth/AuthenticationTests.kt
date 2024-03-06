package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.uuid
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Authentication tests")
class AuthenticationTests {

    @DisplayName("Authentication tests as owner")
    @Nested
    inner class OwnerAuthenticationTests : AbstractUserAuthenticationTests(
        owner,
        ownerExpectedResults,
        ownerInput,
    )

    @DisplayName("Authentication tests as admin")
    @Nested
    inner class AdminAuthenticationTests : AbstractUserAuthenticationTests(
        admin,
        adminExpectedResults,
        adminInput,
    )

    @DisplayName("Authentication tests as regular user")
    @Nested
    inner class RegularUserAuthenticationTests : AbstractUserAuthenticationTests(
        user,
        regularUserExpectedResults,
        regularUserInput,
    )

    @DisplayName("Authentication tests as guest")
    @Nested
    inner class GuestAuthenticationTests : AbstractGuestAuthenticationTests(
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
                    login("authentication.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0006-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("authentication.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0006-0000-0002-000000000000".uuid()
                )
                user = Pair(
                    login("authentication.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0006-0000-0003-000000000000".uuid()
                )
            }
        }
    }
}
