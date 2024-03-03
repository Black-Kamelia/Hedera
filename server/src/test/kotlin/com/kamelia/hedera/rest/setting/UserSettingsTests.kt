package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.uuid
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("User settings tests")
class UserSettingsTests {

    @DisplayName("User settings tests as owner")
    @Nested
    inner class OwnerFilesTests : AbstractUserSettingsTests(
        owner,
        ownerExpectedResults
    )

    @DisplayName("User settings tests as admin")
    @Nested
    inner class AdminFilesTests : AbstractUserSettingsTests(
        admin,
        adminExpectedResults
    )

    @DisplayName("User settings tests as regular user")
    @Nested
    inner class RegularUserFilesTests : AbstractUserSettingsTests(
        user,
        regularUserExpectedResults
    )

    @DisplayName("User settings tests as guest")
    @Nested
    inner class GuestFilesTests : AbstractUserSettingsTests(
        guest,
        guestExpectedResults
    )

    companion object {

        private lateinit var owner: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                owner = Pair(
                    login("user.settings.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0002-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("user.settings.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0002-0000-0001-000000000000".uuid()
                )
                user = Pair(
                    login("user.settings.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0002-0000-0001-000000000000".uuid()
                )
            }
        }
    }
}