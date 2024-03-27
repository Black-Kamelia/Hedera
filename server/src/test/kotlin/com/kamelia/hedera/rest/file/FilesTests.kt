package com.kamelia.hedera.rest.file

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.mapOfVisibility
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

@DisplayName("Files tests")
class FilesTests {

    @DisplayName("Files tests as owner")
    @Nested
    inner class OwnerFilesTests : AbstractUserFilesTests(
        owner,
        ownerExpectedResults,
        ownerInput,
    )

    @DisplayName("Files tests as admin")
    @Nested
    inner class AdminFilesTests : AbstractUserFilesTests(
        admin,
        adminExpectedResults,
        adminInput,
    )

    @DisplayName("Files tests as regular user")
    @Nested
    inner class RegularUserFilesTests : AbstractUserFilesTests(
        user,
        regularUserExpectedResults,
        regularUserInput,
    )

    @DisplayName("Files tests as guest")
    @Nested
    inner class GuestFilesTests : AbstractFilesTests(
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
                    login("files.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("files.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0002-000000000000".uuid()
                )
                user = Pair(
                    login("files.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0003-000000000000".uuid()
                )
            }
        }
    }
}