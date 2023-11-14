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
        UserPersonalTokensTestsExpectedResults(
            deleteOwnPersonalToken = OK,

            listPersonalTokens = OK,
            createPersonalToken = Created,
            deleteOthersPersonalToken = mapOfRole(Forbidden, Forbidden, Forbidden),
        ),
        UserPersonalTokensTestsInput(
            deleteOwnPersonalTokenId = "00000001-0002-0000-0001-000000000000".uuid(),

            deleteOthersPersonalTokenId = mapOfRole(
                "00000001-0002-0000-0001-000000000001".uuid(),
                "00000002-0002-0000-0001-000000000001".uuid(),
                "00000003-0002-0000-0001-000000000001".uuid(),
            )
        ),
    )

    @DisplayName("Personal tokens as admin")
    @Nested
    inner class AdminPersonalTokensTests : AbstractUserPersonalTokensTests(
        admin,
        UserPersonalTokensTestsExpectedResults(
            deleteOwnPersonalToken = OK,

            listPersonalTokens = OK,
            createPersonalToken = Created,
            deleteOthersPersonalToken = mapOfRole(Forbidden, Forbidden, Forbidden),
        ),
        UserPersonalTokensTestsInput(
            deleteOwnPersonalTokenId = "00000002-0002-0000-0001-000000000000".uuid(),

            deleteOthersPersonalTokenId = mapOfRole(
                "00000001-0002-0000-0001-000000000002".uuid(),
                "00000002-0002-0000-0001-000000000002".uuid(),
                "00000003-0002-0000-0001-000000000002".uuid(),
            ),
        ),
    )

    @DisplayName("Personal tokens as regular user")
    @Nested
    inner class RegularUserPersonalTokensTests : AbstractUserPersonalTokensTests(
        user,
        UserPersonalTokensTestsExpectedResults(
            deleteOwnPersonalToken = OK,

            listPersonalTokens = OK,
            createPersonalToken = Created,
            deleteOthersPersonalToken = mapOfRole(Forbidden, Forbidden, Forbidden),
        ),
        UserPersonalTokensTestsInput(
            deleteOwnPersonalTokenId = "00000003-0002-0000-0001-000000000000".uuid(),

            deleteOthersPersonalTokenId = mapOfRole(
                "00000001-0002-0000-0001-000000000003".uuid(),
                "00000002-0002-0000-0001-000000000003".uuid(),
                "00000003-0002-0000-0001-000000000003".uuid(),
            ),
        ),
    )

    @DisplayName("Personal tokens as guest")
    @Nested
    inner class GuestPersonalTokensTests : AbstractPersonalTokensTests(
        guest,
        PersonalTokensTestsExpectedResults(
            listPersonalTokens = Unauthorized,
            createPersonalToken = Unauthorized,
            deleteOthersPersonalToken = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
        ),
        PersonalTokensTestsInput(
            deleteOthersPersonalTokenId = mapOfRole(
                "00000001-0002-0000-0001-000000000004".uuid(),
                "00000002-0002-0000-0001-000000000004".uuid(),
                "00000003-0002-0000-0001-000000000004".uuid(),
            ),
        ),
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