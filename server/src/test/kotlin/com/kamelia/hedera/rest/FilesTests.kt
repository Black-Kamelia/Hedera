package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.test.AbstractFilesTests
import com.kamelia.hedera.rest.test.AbstractUserFilesTests
import com.kamelia.hedera.rest.test.FilesTestsExpectedResults
import com.kamelia.hedera.rest.test.FilesTestsInput
import com.kamelia.hedera.rest.test.UserFilesTestsExpectedResults
import com.kamelia.hedera.rest.test.UserFilesTestsInput
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
    inner class OwnerFilesTests : AbstractUserFilesTests(
        owner,
        UserFilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,
            listFiles = HttpStatusCode.OK,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            updateVisibilityOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            deleteOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),

            // OTHERS FILES
            viewOthersFileAPI = mapOf(
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
            viewOthersFile = mapOf(
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
            renameOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            updateVisibilityOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            deleteOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000001000100000001000000000000",
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$0100010001",
                FileVisibility.UNLISTED to "\$0100010002",
                FileVisibility.PRIVATE to "\$0100010003",
            ),
            renameOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000001-0000-0000-0003-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000001-0000-0000-0003-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000001-0000-0000-0003-000000000003"),
            ),
            updateVisibilityOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000001-0000-0000-0004-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000001-0000-0000-0004-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000001-0000-0000-0004-000000000003"),
            ),
            deleteOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000001-0000-0000-0005-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000001-0000-0000-0005-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000001-0000-0000-0005-000000000003"),
            ),

            viewOthersFileCode = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to "\$1110010001",
                    FileVisibility.UNLISTED to "\$1110010002",
                    FileVisibility.PRIVATE to "\$1110010003",
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to "\$1210010001",
                    FileVisibility.UNLISTED to "\$1210010002",
                    FileVisibility.PRIVATE to "\$1210010003",
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to "\$1310010001",
                    FileVisibility.UNLISTED to "\$1310010002",
                    FileVisibility.PRIVATE to "\$1310010003",
                ),
            ),
            renameOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0003-0000-0003-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0003-0000-0003-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0003-0000-0003-000000000003"),
                ),
            ),
            updateVisibilityOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0001-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0001-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0001-0000-0004-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0001-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0001-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0001-0000-0004-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0001-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0001-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0001-0000-0004-000000000003"),
                ),
            ),
            deleteOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0001-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0001-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0001-0000-0005-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0001-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0001-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0001-0000-0005-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0001-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0001-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0001-0000-0005-000000000003"),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as admin")
    @Nested
    inner class AdminFilesTests : AbstractUserFilesTests(
        admin,
        UserFilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,
            listFiles = HttpStatusCode.OK,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            updateVisibilityOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            deleteOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),

            // OTHERS FILES
            viewOthersFileAPI = mapOf(
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
            viewOthersFile = mapOf(
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
            renameOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            updateVisibilityOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            deleteOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.OK,
                    FileVisibility.UNLISTED to HttpStatusCode.OK,
                    FileVisibility.PRIVATE to HttpStatusCode.OK,
                ),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000002000100000001000000000000",
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$0200010001",
                FileVisibility.UNLISTED to "\$0200010002",
                FileVisibility.PRIVATE to "\$0200010003",
            ),
            renameOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000002-0000-0000-0003-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000002-0000-0000-0003-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000002-0000-0000-0003-000000000003"),
            ),
            updateVisibilityOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000002-0000-0000-0004-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000002-0000-0000-0004-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000002-0000-0000-0004-000000000003"),
            ),
            deleteOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000002-0000-0000-0005-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000002-0000-0000-0005-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000002-0000-0000-0005-000000000003"),
            ),

            viewOthersFileCode = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to "\$1120010001",
                    FileVisibility.UNLISTED to "\$1120010002",
                    FileVisibility.PRIVATE to "\$1120010003",
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to "\$1220010001",
                    FileVisibility.UNLISTED to "\$1220010002",
                    FileVisibility.PRIVATE to "\$1220010003",
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to "\$1320010001",
                    FileVisibility.UNLISTED to "\$1320010002",
                    FileVisibility.PRIVATE to "\$1320010003",
                ),
            ),
            renameOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0003-0000-0003-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0003-0000-0003-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0003-0000-0003-000000000003"),
                ),
            ),
            updateVisibilityOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0002-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0002-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0002-0000-0004-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0002-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0002-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0002-0000-0004-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0002-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0002-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0002-0000-0004-000000000003"),
                ),
            ),
            deleteOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0002-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0002-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0002-0000-0005-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0002-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0002-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0002-0000-0005-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0002-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0002-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0002-0000-0005-000000000003"),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as regular user")
    @Nested
    inner class RegularUserFilesTests : AbstractUserFilesTests(
        user,
        UserFilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Created,
            listFiles = HttpStatusCode.OK,

            // OWN FILES
            viewOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            renameOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            updateVisibilityOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),
            deleteOwnFile = mapOf(
                FileVisibility.PUBLIC to HttpStatusCode.OK,
                FileVisibility.UNLISTED to HttpStatusCode.OK,
                FileVisibility.PRIVATE to HttpStatusCode.OK,
            ),

            // OTHERS FILES
            viewOthersFileAPI = mapOf(
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
            viewOthersFile = mapOf(
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
            renameOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            updateVisibilityOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
            deleteOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Forbidden,
                    FileVisibility.UNLISTED to HttpStatusCode.Forbidden,
                    FileVisibility.PRIVATE to HttpStatusCode.NotFound,
                ),
            ),
        ),
        UserFilesTestsInput(
            uploadToken = "00000003000100000001000000000000",
            viewOwnFileCode = mapOf(
                FileVisibility.PUBLIC to "\$0300010001",
                FileVisibility.UNLISTED to "\$0300010002",
                FileVisibility.PRIVATE to "\$0300010003",
            ),
            renameOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000003-0000-0000-0003-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000003-0000-0000-0003-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000003-0000-0000-0003-000000000003"),
            ),
            updateVisibilityOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000003-0000-0000-0004-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000003-0000-0000-0004-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000003-0000-0000-0004-000000000003"),
            ),
            deleteOwnFileId = mapOf(
                FileVisibility.PUBLIC to UUID.fromString("00000003-0000-0000-0005-000000000001"),
                FileVisibility.UNLISTED to UUID.fromString("00000003-0000-0000-0005-000000000002"),
                FileVisibility.PRIVATE to UUID.fromString("00000003-0000-0000-0005-000000000003"),
            ),

            viewOthersFileCode = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to "\$1130010001",
                    FileVisibility.UNLISTED to "\$1130010002",
                    FileVisibility.PRIVATE to "\$1130010003",
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to "\$1230010001",
                    FileVisibility.UNLISTED to "\$1230010002",
                    FileVisibility.PRIVATE to "\$1230010003",
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to "\$1330010001",
                    FileVisibility.UNLISTED to "\$1330010002",
                    FileVisibility.PRIVATE to "\$1330010003",
                ),
            ),
            renameOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0003-0000-0003-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0003-0000-0003-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0003-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0003-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0003-0000-0003-000000000003"),
                ),
            ),
            updateVisibilityOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0003-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0003-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0003-0000-0004-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0003-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0003-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0003-0000-0004-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0003-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0003-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0003-0000-0004-000000000003"),
                ),
            ),
            deleteOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-0003-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-0003-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-0003-0000-0005-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-0003-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-0003-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-0003-0000-0005-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-0003-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-0003-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-0003-0000-0005-000000000003"),
                ),
            ),
        ),
    )

    @DisplayName("Files tests as guest")
    @Nested
    inner class GuestFilesTests : AbstractFilesTests(
        guest,
        FilesTestsExpectedResults(
            uploadFile = HttpStatusCode.Unauthorized,
            listFiles = HttpStatusCode.Unauthorized,

            // OTHERS FILES
            viewOthersFileAPI = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
            ),
            viewOthersFile = mapOf(
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
            renameOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
            ),
            updateVisibilityOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
            ),
            deleteOthersFile = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to HttpStatusCode.Unauthorized,
                    FileVisibility.UNLISTED to HttpStatusCode.Unauthorized,
                    FileVisibility.PRIVATE to HttpStatusCode.Unauthorized,
                ),
            ),
        ),
        FilesTestsInput(
            viewOthersFileCode = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to "\$11f0010001",
                    FileVisibility.UNLISTED to "\$11f0010002",
                    FileVisibility.PRIVATE to "\$11f0010003",
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to "\$12f0010001",
                    FileVisibility.UNLISTED to "\$12f0010002",
                    FileVisibility.PRIVATE to "\$12f0010003",
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to "\$13f0010001",
                    FileVisibility.UNLISTED to "\$13f0010002",
                    FileVisibility.PRIVATE to "\$13f0010003",
                ),
            ),
            renameOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-000f-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-000f-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-000f-0000-0003-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-000f-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-000f-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-000f-0000-0003-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-000f-0000-0003-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-000f-0000-0003-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-000f-0000-0003-000000000003"),
                ),
            ),
            updateVisibilityOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-000f-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-000f-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-000f-0000-0004-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-000f-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-000f-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-000f-0000-0004-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-000f-0000-0004-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-000f-0000-0004-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-000f-0000-0004-000000000003"),
                ),
            ),
            deleteOthersFileId = mapOf(
                UserRole.OWNER to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000001-000f-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000001-000f-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000001-000f-0000-0005-000000000003"),
                ),
                UserRole.ADMIN to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000002-000f-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000002-000f-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000002-000f-0000-0005-000000000003"),
                ),
                UserRole.REGULAR to mapOf(
                    FileVisibility.PUBLIC to UUID.fromString("10000003-000f-0000-0005-000000000001"),
                    FileVisibility.UNLISTED to UUID.fromString("10000003-000f-0000-0005-000000000002"),
                    FileVisibility.PRIVATE to UUID.fromString("10000003-000f-0000-0005-000000000003"),
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
                    UUID.fromString("ffffffff-0001-0000-0001-000000000000")
                )
                admin = Pair(
                    login("files.admin", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("ffffffff-0001-0000-0001-000000000000")
                )
                user = Pair(
                    login("files.user", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("ffffffff-0001-0000-0001-000000000000")
                )
            }
        }
    }
}