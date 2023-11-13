package com.kamelia.hedera.rest.user

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

@DisplayName("Users tests")
class UsersTests {

    @DisplayName("Users tests as owner")
    @Nested
    inner class OwnerUsersTests : AbstractUserUsersTests(
        owner,
        UserUsersTestsExpectedResults(
            listUsers = OK,
            createUser = mapOfRole(Forbidden, Created, Created),
            updateOthersUsername = mapOfRole(OK, OK, OK),
            updateOthersEmail = mapOfRole(OK, OK, OK),
            updateOthersRole = mapOfRole(
                mapOfRole(Forbidden, Forbidden, Forbidden),
                mapOfRole(Forbidden, OK, OK),
                mapOfRole(Forbidden, OK, OK),
            ),
            updateOthersQuota = mapOfRole(OK, OK, OK),
            updateOthersPassword = mapOfRole(Forbidden, Forbidden, Forbidden),
            activateUser = mapOfRole(Forbidden, OK, OK),
            deactivateUser = mapOfRole(Forbidden, OK, OK),
            deleteUser = mapOfRole(Forbidden, OK, OK),
        ),
        UserUsersTestsInput(
            updateOthersUsernameUserId = mapOfRole(
                "00000000-0002-0001-0001-000000000001".uuid(),
                "00000000-0002-0001-0001-000000000002".uuid(),
                "00000000-0002-0001-0001-000000000003".uuid(),
            ),
            updateOthersEmailUserId = mapOfRole(
                "00000000-0002-0002-0001-000000000001".uuid(),
                "00000000-0002-0002-0001-000000000002".uuid(),
                "00000000-0002-0002-0001-000000000003".uuid(),
            ),
            updateOthersRoleUserId = mapOfRole(
                mapOfRole(
                    "00000000-0002-0003-0001-000000000001".uuid(),
                    "00000000-0002-0003-0001-000000000002".uuid(),
                    "00000000-0002-0003-0001-000000000003".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0001-000000000004".uuid(),
                    "00000000-0002-0003-0001-000000000005".uuid(),
                    "00000000-0002-0003-0001-000000000006".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0001-000000000007".uuid(),
                    "00000000-0002-0003-0001-000000000008".uuid(),
                    "00000000-0002-0003-0001-000000000009".uuid(),
                ),
            ),
            updateOthersQuotaUserId = mapOfRole(
                "00000000-0002-0004-0001-000000000001".uuid(),
                "00000000-0002-0004-0001-000000000002".uuid(),
                "00000000-0002-0004-0001-000000000003".uuid(),
            ),
            updateOthersPasswordUserId = mapOfRole(
                "00000000-0002-0005-0001-000000000001".uuid(),
                "00000000-0002-0005-0001-000000000002".uuid(),
                "00000000-0002-0005-0001-000000000003".uuid(),
            ),
            activateUserId = mapOfRole(
                "00000000-0002-0006-0001-000000000001".uuid(),
                "00000000-0002-0006-0001-000000000002".uuid(),
                "00000000-0002-0006-0001-000000000003".uuid(),
            ),
            deactivateUserId = mapOfRole(
                "00000000-0002-0007-0001-000000000001".uuid(),
                "00000000-0002-0007-0001-000000000002".uuid(),
                "00000000-0002-0007-0001-000000000003".uuid(),
            ),
            deleteUserId = mapOfRole(
                "00000000-0002-0008-0001-000000000001".uuid(),
                "00000000-0002-0008-0001-000000000002".uuid(),
                "00000000-0002-0008-0001-000000000003".uuid(),
            ),
        ),
    )

    @DisplayName("Users tests as admin")
    @Nested
    inner class AdminUsersTests : AbstractUserUsersTests(
        admin,
        UserUsersTestsExpectedResults(
            listUsers = OK,
            createUser = mapOfRole(Forbidden, Forbidden, Created),
            updateOthersUsername = mapOfRole(Forbidden, Forbidden, OK),
            updateOthersEmail = mapOfRole(Forbidden, Forbidden, OK),
            updateOthersRole = mapOfRole(
                mapOfRole(Forbidden, Forbidden, Forbidden),
                mapOfRole(Forbidden, Forbidden, Forbidden),
                mapOfRole(Forbidden, Forbidden, Forbidden),
            ),
            updateOthersQuota = mapOfRole(Forbidden, Forbidden, OK),
            updateOthersPassword = mapOfRole(Forbidden, Forbidden, Forbidden),
            activateUser = mapOfRole(Forbidden, Forbidden, OK),
            deactivateUser = mapOfRole(Forbidden, Forbidden, OK),
            deleteUser = mapOfRole(Forbidden, Forbidden, OK),
        ),
        UserUsersTestsInput(
            updateOthersUsernameUserId = mapOfRole(
                "00000000-0002-0001-0002-000000000001".uuid(),
                "00000000-0002-0001-0002-000000000002".uuid(),
                "00000000-0002-0001-0002-000000000003".uuid(),
            ),
            updateOthersEmailUserId = mapOfRole(
                "00000000-0002-0002-0002-000000000001".uuid(),
                "00000000-0002-0002-0002-000000000002".uuid(),
                "00000000-0002-0002-0002-000000000003".uuid(),
            ),
            updateOthersRoleUserId = mapOfRole(
                mapOfRole(
                    "00000000-0002-0003-0002-000000000001".uuid(),
                    "00000000-0002-0003-0002-000000000002".uuid(),
                    "00000000-0002-0003-0002-000000000003".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0002-000000000004".uuid(),
                    "00000000-0002-0003-0002-000000000005".uuid(),
                    "00000000-0002-0003-0002-000000000006".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0002-000000000007".uuid(),
                    "00000000-0002-0003-0002-000000000008".uuid(),
                    "00000000-0002-0003-0002-000000000009".uuid(),
                ),
            ),
            updateOthersQuotaUserId = mapOfRole(
                "00000000-0002-0004-0002-000000000001".uuid(),
                "00000000-0002-0004-0002-000000000002".uuid(),
                "00000000-0002-0004-0002-000000000003".uuid(),
            ),
            updateOthersPasswordUserId = mapOfRole(
                "00000000-0002-0005-0002-000000000001".uuid(),
                "00000000-0002-0005-0002-000000000002".uuid(),
                "00000000-0002-0005-0002-000000000003".uuid(),
            ),
            activateUserId = mapOfRole(
                "00000000-0002-0006-0002-000000000001".uuid(),
                "00000000-0002-0006-0002-000000000002".uuid(),
                "00000000-0002-0006-0002-000000000003".uuid(),
            ),
            deactivateUserId = mapOfRole(
                "00000000-0002-0007-0002-000000000001".uuid(),
                "00000000-0002-0007-0002-000000000002".uuid(),
                "00000000-0002-0007-0002-000000000003".uuid(),
            ),
            deleteUserId = mapOfRole(
                "00000000-0002-0008-0002-000000000001".uuid(),
                "00000000-0002-0008-0002-000000000002".uuid(),
                "00000000-0002-0008-0002-000000000003".uuid(),
            ),
        ),
    )

    @DisplayName("Users tests as regular user")
    @Nested
    inner class RegularUserUsersTests : AbstractUserUsersTests(
        user,
        UserUsersTestsExpectedResults(
            listUsers = Forbidden,
            createUser = mapOfRole(Forbidden, Forbidden, Forbidden),
            updateOthersUsername = mapOfRole(Forbidden, Forbidden, Forbidden),
            updateOthersEmail = mapOfRole(Forbidden, Forbidden, Forbidden),
            updateOthersRole = mapOfRole(
                mapOfRole(Forbidden, Forbidden, Forbidden),
                mapOfRole(Forbidden, Forbidden, Forbidden),
                mapOfRole(Forbidden, Forbidden, Forbidden),
            ),
            updateOthersQuota = mapOfRole(Forbidden, Forbidden, Forbidden),
            updateOthersPassword = mapOfRole(Forbidden, Forbidden, Forbidden),
            activateUser = mapOfRole(Forbidden, Forbidden, Forbidden),
            deactivateUser = mapOfRole(Forbidden, Forbidden, Forbidden),
            deleteUser = mapOfRole(Forbidden, Forbidden, Forbidden),
        ),
        UserUsersTestsInput(
            updateOthersUsernameUserId = mapOfRole(
                "00000000-0002-0001-0003-000000000001".uuid(),
                "00000000-0002-0001-0003-000000000002".uuid(),
                "00000000-0002-0001-0003-000000000003".uuid(),
            ),
            updateOthersEmailUserId = mapOfRole(
                "00000000-0002-0002-0003-000000000001".uuid(),
                "00000000-0002-0002-0003-000000000002".uuid(),
                "00000000-0002-0002-0003-000000000003".uuid(),
            ),
            updateOthersRoleUserId = mapOfRole(
                mapOfRole(
                    "00000000-0002-0003-0003-000000000001".uuid(),
                    "00000000-0002-0003-0003-000000000002".uuid(),
                    "00000000-0002-0003-0003-000000000003".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0003-000000000004".uuid(),
                    "00000000-0002-0003-0003-000000000005".uuid(),
                    "00000000-0002-0003-0003-000000000006".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0003-000000000007".uuid(),
                    "00000000-0002-0003-0003-000000000008".uuid(),
                    "00000000-0002-0003-0003-000000000009".uuid(),
                ),
            ),
            updateOthersQuotaUserId = mapOfRole(
                "00000000-0002-0004-0003-000000000001".uuid(),
                "00000000-0002-0004-0003-000000000002".uuid(),
                "00000000-0002-0004-0003-000000000003".uuid(),
            ),
            updateOthersPasswordUserId = mapOfRole(
                "00000000-0002-0005-0003-000000000001".uuid(),
                "00000000-0002-0005-0003-000000000002".uuid(),
                "00000000-0002-0005-0003-000000000003".uuid(),
            ),
            activateUserId = mapOfRole(
                "00000000-0002-0006-0003-000000000001".uuid(),
                "00000000-0002-0006-0003-000000000002".uuid(),
                "00000000-0002-0006-0003-000000000003".uuid(),
            ),
            deactivateUserId = mapOfRole(
                "00000000-0002-0007-0003-000000000001".uuid(),
                "00000000-0002-0007-0003-000000000002".uuid(),
                "00000000-0002-0007-0003-000000000003".uuid(),
            ),
            deleteUserId = mapOfRole(
                "00000000-0002-0008-0003-000000000001".uuid(),
                "00000000-0002-0008-0003-000000000002".uuid(),
                "00000000-0002-0008-0003-000000000003".uuid(),
            ),
        ),
    )

    @DisplayName("Users tests as guest")
    @Nested
    inner class GuestUsersTests : AbstractUsersTests(
        guest,
        UsersTestsExpectedResults(
            listUsers = Unauthorized,
            createUser = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            updateOthersUsername = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            updateOthersEmail = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            updateOthersRole = mapOfRole(
                mapOfRole(Unauthorized, Unauthorized, Unauthorized),
                mapOfRole(Unauthorized, Unauthorized, Unauthorized),
                mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            ),
            updateOthersQuota = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            updateOthersPassword = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            activateUser = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            deactivateUser = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
            deleteUser = mapOfRole(Unauthorized, Unauthorized, Unauthorized),
        ),
        UsersTestsInput(
            updateOthersUsernameUserId = mapOfRole(
                "00000000-0002-0001-0004-000000000001".uuid(),
                "00000000-0002-0001-0004-000000000002".uuid(),
                "00000000-0002-0001-0004-000000000003".uuid(),
            ),
            updateOthersEmailUserId = mapOfRole(
                "00000000-0002-0002-0004-000000000001".uuid(),
                "00000000-0002-0002-0004-000000000002".uuid(),
                "00000000-0002-0002-0004-000000000003".uuid(),
            ),
            updateOthersRoleUserId = mapOfRole(
                mapOfRole(
                    "00000000-0002-0003-0004-000000000001".uuid(),
                    "00000000-0002-0003-0004-000000000002".uuid(),
                    "00000000-0002-0003-0004-000000000003".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0004-000000000004".uuid(),
                    "00000000-0002-0003-0004-000000000005".uuid(),
                    "00000000-0002-0003-0004-000000000006".uuid(),
                ),
                mapOfRole(
                    "00000000-0002-0003-0004-000000000007".uuid(),
                    "00000000-0002-0003-0004-000000000008".uuid(),
                    "00000000-0002-0003-0004-000000000009".uuid(),
                ),
            ),
            updateOthersQuotaUserId = mapOfRole(
                "00000000-0002-0004-0004-000000000001".uuid(),
                "00000000-0002-0004-0004-000000000002".uuid(),
                "00000000-0002-0004-0004-000000000003".uuid(),
            ),
            updateOthersPasswordUserId = mapOfRole(
                "00000000-0002-0005-0004-000000000001".uuid(),
                "00000000-0002-0005-0004-000000000002".uuid(),
                "00000000-0002-0005-0004-000000000003".uuid(),
            ),
            activateUserId = mapOfRole(
                "00000000-0002-0006-0004-000000000001".uuid(),
                "00000000-0002-0006-0004-000000000002".uuid(),
                "00000000-0002-0006-0004-000000000003".uuid(),
            ),
            deactivateUserId = mapOfRole(
                "00000000-0002-0007-0004-000000000001".uuid(),
                "00000000-0002-0007-0004-000000000002".uuid(),
                "00000000-0002-0007-0004-000000000003".uuid(),
            ),
            deleteUserId = mapOfRole(
                "00000000-0002-0008-0004-000000000001".uuid(),
                "00000000-0002-0008-0004-000000000002".uuid(),
                "00000000-0002-0008-0004-000000000003".uuid(),
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