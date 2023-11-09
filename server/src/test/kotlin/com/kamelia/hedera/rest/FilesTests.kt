package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.mapOfVisibility
import com.kamelia.hedera.rest.test.AbstractFilesTests
import com.kamelia.hedera.rest.test.AbstractUserFilesTests
import com.kamelia.hedera.rest.test.FilesTestsExpectedResults
import com.kamelia.hedera.rest.test.FilesTestsInput
import com.kamelia.hedera.rest.test.UserFilesTestsExpectedResults
import com.kamelia.hedera.rest.test.UserFilesTestsInput
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
        UserFilesTestsExpectedResults(
            uploadFile = Created,
            listFiles = OK,

            // OWN FILES
            viewOwnFile = mapOfVisibility(OK, OK, OK),
            renameOwnFile = mapOfVisibility(OK, OK, OK),
            updateVisibilityOwnFile = mapOfVisibility(OK, OK, OK),
            deleteOwnFile = mapOfVisibility(OK, OK, OK),

            // OTHERS FILES
            viewOthersFileAPI = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            viewOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            renameOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            updateVisibilityOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            deleteOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, OK),
                mapOfVisibility(OK, OK, OK),
                mapOfVisibility(OK, OK, OK),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000001000100000001000000000000",
            viewOwnFileCode = mapOfVisibility("\$0100010001", "\$0100010002", "\$0100010003"),
            renameOwnFileId = mapOfVisibility(
                "00000001-0000-0000-0003-000000000001".uuid(),
                "00000001-0000-0000-0003-000000000002".uuid(),
                "00000001-0000-0000-0003-000000000003".uuid(),
            ),
            updateVisibilityOwnFileId = mapOfVisibility(
                "00000001-0000-0000-0004-000000000001".uuid(),
                "00000001-0000-0000-0004-000000000002".uuid(),
                "00000001-0000-0000-0004-000000000003".uuid(),
            ),
            deleteOwnFileId = mapOfVisibility(
                "00000001-0000-0000-0005-000000000001".uuid(),
                "00000001-0000-0000-0005-000000000002".uuid(),
                "00000001-0000-0000-0005-000000000003".uuid(),
            ),

            viewOthersFileCode = mapOfRole(
                mapOfVisibility("\$1110010001", "\$1110010002", "\$1110010003"),
                mapOfVisibility("\$1210010001", "\$1210010002", "\$1210010003"),
                mapOfVisibility("\$1310010001", "\$1310010002", "\$1310010003"),
            ),
            renameOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0003-0000-0003-000000000001".uuid(),
                    "10000001-0003-0000-0003-000000000002".uuid(),
                    "10000001-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0003-0000-0003-000000000001".uuid(),
                    "10000002-0003-0000-0003-000000000002".uuid(),
                    "10000002-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0003-0000-0003-000000000001".uuid(),
                    "10000003-0003-0000-0003-000000000002".uuid(),
                    "10000003-0003-0000-0003-000000000003".uuid(),
                ),
            ),
            updateVisibilityOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0001-0000-0004-000000000001".uuid(),
                    "10000001-0001-0000-0004-000000000002".uuid(),
                    "10000001-0001-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0001-0000-0004-000000000001".uuid(),
                    "10000002-0001-0000-0004-000000000002".uuid(),
                    "10000002-0001-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0001-0000-0004-000000000001".uuid(),
                    "10000003-0001-0000-0004-000000000002".uuid(),
                    "10000003-0001-0000-0004-000000000003".uuid(),
                ),
            ),
            deleteOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0001-0000-0005-000000000001".uuid(),
                    "10000001-0001-0000-0005-000000000002".uuid(),
                    "10000001-0001-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0001-0000-0005-000000000001".uuid(),
                    "10000002-0001-0000-0005-000000000002".uuid(),
                    "10000002-0001-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0001-0000-0005-000000000001".uuid(),
                    "10000003-0001-0000-0005-000000000002".uuid(),
                    "10000003-0001-0000-0005-000000000003".uuid(),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as admin")
    @Nested
    inner class AdminFilesTests : AbstractUserFilesTests(
        admin,
        UserFilesTestsExpectedResults(
            uploadFile = Created,
            listFiles = OK,

            // OWN FILES
            viewOwnFile = mapOfVisibility(OK, OK, OK),
            renameOwnFile = mapOfVisibility(OK, OK, OK),
            updateVisibilityOwnFile = mapOfVisibility(OK, OK, OK),
            deleteOwnFile = mapOfVisibility(OK, OK, OK),

            // OTHERS FILES
            viewOthersFileAPI = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            viewOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            renameOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            updateVisibilityOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            deleteOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, OK),
                mapOfVisibility(OK, OK, OK),
                mapOfVisibility(OK, OK, OK),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000002000100000001000000000000",
            viewOwnFileCode = mapOfVisibility("\$0200010001", "\$0200010002", "\$0200010003"),
            renameOwnFileId = mapOfVisibility(
                "00000002-0000-0000-0003-000000000001".uuid(),
                "00000002-0000-0000-0003-000000000002".uuid(),
                "00000002-0000-0000-0003-000000000003".uuid(),
            ),
            updateVisibilityOwnFileId = mapOfVisibility(
                "00000002-0000-0000-0004-000000000001".uuid(),
                "00000002-0000-0000-0004-000000000002".uuid(),
                "00000002-0000-0000-0004-000000000003".uuid(),
            ),
            deleteOwnFileId = mapOfVisibility(
                "00000002-0000-0000-0005-000000000001".uuid(),
                "00000002-0000-0000-0005-000000000002".uuid(),
                "00000002-0000-0000-0005-000000000003".uuid(),
            ),

            viewOthersFileCode = mapOfRole(
                mapOfVisibility("\$1120010001", "\$1120010002", "\$1120010003"),
                mapOfVisibility("\$1220010001", "\$1220010002", "\$1220010003"),
                mapOfVisibility("\$1320010001", "\$1320010002", "\$1320010003"),
            ),
            renameOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0003-0000-0003-000000000001".uuid(),
                    "10000001-0003-0000-0003-000000000002".uuid(),
                    "10000001-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0003-0000-0003-000000000001".uuid(),
                    "10000002-0003-0000-0003-000000000002".uuid(),
                    "10000002-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0003-0000-0003-000000000001".uuid(),
                    "10000003-0003-0000-0003-000000000002".uuid(),
                    "10000003-0003-0000-0003-000000000003".uuid(),
                ),
            ),
            updateVisibilityOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0002-0000-0004-000000000001".uuid(),
                    "10000001-0002-0000-0004-000000000002".uuid(),
                    "10000001-0002-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0002-0000-0004-000000000001".uuid(),
                    "10000002-0002-0000-0004-000000000002".uuid(),
                    "10000002-0002-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0002-0000-0004-000000000001".uuid(),
                    "10000003-0002-0000-0004-000000000002".uuid(),
                    "10000003-0002-0000-0004-000000000003".uuid(),
                ),
            ),
            deleteOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0002-0000-0005-000000000001".uuid(),
                    "10000001-0002-0000-0005-000000000002".uuid(),
                    "10000001-0002-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0002-0000-0005-000000000001".uuid(),
                    "10000002-0002-0000-0005-000000000002".uuid(),
                    "10000002-0002-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0002-0000-0005-000000000001".uuid(),
                    "10000003-0002-0000-0005-000000000002".uuid(),
                    "10000003-0002-0000-0005-000000000003".uuid(),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as regular user")
    @Nested
    inner class RegularUserFilesTests : AbstractUserFilesTests(
        user,
        UserFilesTestsExpectedResults(
            uploadFile = Created,
            listFiles = OK,

            // OWN FILES
            viewOwnFile = mapOfVisibility(OK, OK, OK),
            renameOwnFile = mapOfVisibility(OK, OK, OK),
            updateVisibilityOwnFile = mapOfVisibility(OK, OK, OK),
            deleteOwnFile = mapOfVisibility(OK, OK, OK),

            // OTHERS FILES
            viewOthersFileAPI = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            viewOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            renameOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            updateVisibilityOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
            deleteOthersFile = mapOfRole(
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
                mapOfVisibility(Forbidden, Forbidden, NotFound),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000003000100000001000000000000",
            viewOwnFileCode = mapOfVisibility("\$0300010001", "\$0300010002", "\$0300010003"),
            renameOwnFileId = mapOfVisibility(
                "00000003-0000-0000-0003-000000000001".uuid(),
                "00000003-0000-0000-0003-000000000002".uuid(),
                "00000003-0000-0000-0003-000000000003".uuid(),
            ),
            updateVisibilityOwnFileId = mapOfVisibility(
                "00000003-0000-0000-0004-000000000001".uuid(),
                "00000003-0000-0000-0004-000000000002".uuid(),
                "00000003-0000-0000-0004-000000000003".uuid(),
            ),
            deleteOwnFileId = mapOfVisibility(
                "00000003-0000-0000-0005-000000000001".uuid(),
                "00000003-0000-0000-0005-000000000002".uuid(),
                "00000003-0000-0000-0005-000000000003".uuid(),
            ),

            viewOthersFileCode = mapOfRole(
                mapOfVisibility("\$1130010001", "\$1130010002", "\$1130010003"),
                mapOfVisibility("\$1230010001", "\$1230010002", "\$1230010003"),
                mapOfVisibility("\$1330010001", "\$1330010002", "\$1330010003"),
            ),
            renameOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0003-0000-0003-000000000001".uuid(),
                    "10000001-0003-0000-0003-000000000002".uuid(),
                    "10000001-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0003-0000-0003-000000000001".uuid(),
                    "10000002-0003-0000-0003-000000000002".uuid(),
                    "10000002-0003-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0003-0000-0003-000000000001".uuid(),
                    "10000003-0003-0000-0003-000000000002".uuid(),
                    "10000003-0003-0000-0003-000000000003".uuid(),
                ),
            ),
            updateVisibilityOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0003-0000-0004-000000000001".uuid(),
                    "10000001-0003-0000-0004-000000000002".uuid(),
                    "10000001-0003-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0003-0000-0004-000000000001".uuid(),
                    "10000002-0003-0000-0004-000000000002".uuid(),
                    "10000002-0003-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0003-0000-0004-000000000001".uuid(),
                    "10000003-0003-0000-0004-000000000002".uuid(),
                    "10000003-0003-0000-0004-000000000003".uuid(),
                ),
            ),
            deleteOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-0003-0000-0005-000000000001".uuid(),
                    "10000001-0003-0000-0005-000000000002".uuid(),
                    "10000001-0003-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-0003-0000-0005-000000000001".uuid(),
                    "10000002-0003-0000-0005-000000000002".uuid(),
                    "10000002-0003-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-0003-0000-0005-000000000001".uuid(),
                    "10000003-0003-0000-0005-000000000002".uuid(),
                    "10000003-0003-0000-0005-000000000003".uuid(),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as guest")
    @Nested
    inner class GuestFilesTests : AbstractFilesTests(
        guest,
        FilesTestsExpectedResults(
            uploadFile = Unauthorized,
            listFiles = Unauthorized,

            // OTHERS FILES
            viewOthersFileAPI = mapOfRole(
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
            ),
            viewOthersFile = mapOfRole(
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
                mapOfVisibility(OK, OK, NotFound),
            ),
            renameOthersFile = mapOfRole(
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
            ),
            updateVisibilityOthersFile = mapOfRole(
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
            ),
            deleteOthersFile = mapOfRole(
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
                mapOfVisibility(Unauthorized, Unauthorized, Unauthorized),
            ),
        ),
        FilesTestsInput(
            viewOthersFileCode = mapOfRole(
                mapOfVisibility("\$11f0010001", "\$11f0010002", "\$11f0010003"),
                mapOfVisibility("\$12f0010001", "\$12f0010002", "\$12f0010003"),
                mapOfVisibility("\$13f0010001", "\$13f0010002", "\$13f0010003"),
            ),
            renameOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-000f-0000-0003-000000000001".uuid(),
                    "10000001-000f-0000-0003-000000000002".uuid(),
                    "10000001-000f-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-000f-0000-0003-000000000001".uuid(),
                    "10000002-000f-0000-0003-000000000002".uuid(),
                    "10000002-000f-0000-0003-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-000f-0000-0003-000000000001".uuid(),
                    "10000003-000f-0000-0003-000000000002".uuid(),
                    "10000003-000f-0000-0003-000000000003".uuid(),
                ),
            ),
            updateVisibilityOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-000f-0000-0004-000000000001".uuid(),
                    "10000001-000f-0000-0004-000000000002".uuid(),
                    "10000001-000f-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-000f-0000-0004-000000000001".uuid(),
                    "10000002-000f-0000-0004-000000000002".uuid(),
                    "10000002-000f-0000-0004-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-000f-0000-0004-000000000001".uuid(),
                    "10000003-000f-0000-0004-000000000002".uuid(),
                    "10000003-000f-0000-0004-000000000003".uuid(),
                ),
            ),
            deleteOthersFileId = mapOfRole(
                mapOfVisibility(
                    "10000001-000f-0000-0005-000000000001".uuid(),
                    "10000001-000f-0000-0005-000000000002".uuid(),
                    "10000001-000f-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000002-000f-0000-0005-000000000001".uuid(),
                    "10000002-000f-0000-0005-000000000002".uuid(),
                    "10000002-000f-0000-0005-000000000003".uuid(),
                ),
                mapOfVisibility(
                    "10000003-000f-0000-0005-000000000001".uuid(),
                    "10000003-000f-0000-0005-000000000002".uuid(),
                    "10000003-000f-0000-0005-000000000003".uuid(),
                ),
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
                    login("files.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("files.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
                user = Pair(
                    login("files.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
            }
        }
    }
}