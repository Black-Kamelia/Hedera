package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.test.AbstractFilesTests
import com.kamelia.hedera.rest.test.FilesTestsExpectedResults
import com.kamelia.hedera.rest.test.FilesTestsInput
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Files tests")
class FilesTests {

    @DisplayName("Files tests as owner")
    @Nested
    inner class OwnerTests : AbstractFilesTests(
        owner,
        FilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = HttpStatusCode.OK,
            updateVisibilityOwnFile = HttpStatusCode.OK,
            deleteOwnFile = HttpStatusCode.OK,

            // OTHERS FILES
            viewOtherFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            renameOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            updateVisibilityOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            deleteOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
        ),
        FilesTestsInput(
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$1000010001",
                FileVisibility.UNLISTED to "\$1000010002",
                FileVisibility.PRIVATE to "\$1000010003",
            ),
            renameOwnFileId = UUID.fromString("00000001-0000-0000-0003-000000000000"),
            updateVisibilityOwnFileId = UUID.fromString("00000001-0000-0000-0004-000000000000"),
            deleteOwnFileId = UUID.fromString("00000001-0000-0000-0005-000000000000"),
        ),
    )

    @DisplayName("Files tests as admin")
    @Nested
    inner class AdminTests : AbstractFilesTests(
        admin,
        FilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = HttpStatusCode.OK,
            updateVisibilityOwnFile = HttpStatusCode.OK,
            deleteOwnFile = HttpStatusCode.OK,

            // OTHERS FILES
            viewOtherFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            renameOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            updateVisibilityOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            deleteOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
        ),
        FilesTestsInput(
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$2000010001",
                FileVisibility.UNLISTED to "\$2000010002",
                FileVisibility.PRIVATE to "\$2000010003",
            ),
            renameOwnFileId = UUID.fromString("00000002-0000-0000-0003-000000000000"),
            updateVisibilityOwnFileId = UUID.fromString("00000002-0000-0000-0004-000000000000"),
            deleteOwnFileId = UUID.fromString("00000002-0000-0000-0005-000000000000"),
        ),
    )

    @DisplayName("Files tests as regular user")
    @Nested
    inner class RegularUserTests : AbstractFilesTests(
        user,
        FilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = HttpStatusCode.OK,
            updateVisibilityOwnFile = HttpStatusCode.OK,
            deleteOwnFile = HttpStatusCode.OK,

            // OTHERS FILES
            viewOtherFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            renameOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            updateVisibilityOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            deleteOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
        ),
        FilesTestsInput(
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$3000010001",
                FileVisibility.UNLISTED to "\$3000010002",
                FileVisibility.PRIVATE to "\$3000010003",
            ),
            renameOwnFileId = UUID.fromString("00000003-0000-0000-0003-000000000000"),
            updateVisibilityOwnFileId = UUID.fromString("00000003-0000-0000-0004-000000000000"),
            deleteOwnFileId = UUID.fromString("00000003-0000-0000-0005-000000000000"),
        ),
    )

    /*
    @DisplayName("Files tests as guest")
    @Nested
    inner class GuestTests : AbstractFilesTests(
        guest,
        FilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Unauthorized,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = HttpStatusCode.OK,
            updateVisibilityOwnFile = HttpStatusCode.OK,
            deleteOwnFile = HttpStatusCode.OK,

            // OTHERS FILES
            viewOtherFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            renameOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            updateVisibilityOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
            deleteOtherFile = mapOf(
                UserRole.OWNER to HttpStatusCode.Forbidden,
                UserRole.ADMIN to HttpStatusCode.Forbidden,
                UserRole.REGULAR to HttpStatusCode.Forbidden,
            ),
        ),
        FilesTestsInput(
            viewOwnFileId = UUID.fromString("00000000-0000-0000-0000-000000000000"),
            viewOwnFileCode = "$0000000000",
            deleteOwnFileId = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        ),
    )

     */

    companion object {

        private lateinit var owner: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                owner = Pair(
                    login("owner", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("ffffffff-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("ffffffff-0000-0000-0000-000000000002")
                )
                user = Pair(
                    login("user", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("ffffffff-0000-0000-0000-000000000003")
                )
            }
        }

    }

}