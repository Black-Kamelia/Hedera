package com.kamelia.hedera.rest.token

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

@DisplayName("Personal tokens tests")
class PersonalTokensTests {

    @DisplayName("Personal tokens as owner")
    @Nested
    inner class OwnerPersonalTokensTests : AbstractUserPersonalTokensTests(
        owner,
        ownerExpectedResults,
        ownerInput,
    )

    @DisplayName("Personal tokens as admin")
    @Nested
    inner class AdminPersonalTokensTests : AbstractUserPersonalTokensTests(
        admin,
        adminExpectedResults,
        adminInput,
    )

    @DisplayName("Personal tokens as regular user")
    @Nested
    inner class RegularUserPersonalTokensTests : AbstractUserPersonalTokensTests(
        user,
        regularUserFilesTests,
        regularUserInput,
    )

    @DisplayName("Personal tokens as guest")
    @Nested
    inner class GuestPersonalTokensTests : AbstractPersonalTokensTests(
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
                    login("personal.tokens.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0004-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("personal.tokens.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0004-0000-0001-000000000000".uuid()
                )
                user = Pair(
                    login("personal.tokens.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0004-0000-0001-000000000000".uuid()
                )
            }
        }
    }
}