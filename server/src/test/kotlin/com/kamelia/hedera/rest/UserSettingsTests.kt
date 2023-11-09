package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.mapOfVisibility
import com.kamelia.hedera.rest.test.AbstractFilesTests
import com.kamelia.hedera.rest.test.AbstractUserFilesTests
import com.kamelia.hedera.rest.test.AbstractUserSettingsTests
import com.kamelia.hedera.rest.test.FilesTestsExpectedResults
import com.kamelia.hedera.rest.test.FilesTestsInput
import com.kamelia.hedera.rest.test.UserFilesTestsExpectedResults
import com.kamelia.hedera.rest.test.UserFilesTestsInput
import com.kamelia.hedera.rest.test.UserSettingsTestsExpectedResults
import com.kamelia.hedera.uuid
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.NotFound
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
        UserSettingsTestsExpectedResults(OK, OK)
    )

    @DisplayName("User settings tests as admin")
    @Nested
    inner class AdminFilesTests : AbstractUserSettingsTests(
        admin,
        UserSettingsTestsExpectedResults(OK, OK)
    )

    @DisplayName("User settings tests as regular user")
    @Nested
    inner class RegularUserFilesTests : AbstractUserSettingsTests(
        user,
        UserSettingsTestsExpectedResults(OK, OK)
    )

    @DisplayName("User settings tests as guest")
    @Nested
    inner class GuestFilesTests : AbstractUserSettingsTests(
        guest,
        UserSettingsTestsExpectedResults(Unauthorized, Unauthorized)
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