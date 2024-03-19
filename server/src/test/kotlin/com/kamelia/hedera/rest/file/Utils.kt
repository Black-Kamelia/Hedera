package com.kamelia.hedera.rest.file

import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.mapOfVisibility
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.uuid
import io.ktor.http.*
import java.util.*


open class FilesTestsExpectedResults(
    val uploadFile: HttpStatusCode,
    val listFiles: HttpStatusCode,
    val listFilesFormats: HttpStatusCode,

    val viewOthersFileAPI: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val renameOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val updateVisibilityOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val bulkUpdateVisibilityOthersFile: HttpStatusCode,
    val bulkUpdateVisibilityOthersFileResult: Map<UserRole, Map<FileVisibility, Int>>,
    val updateCustomLinkOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val removeCustomLinkOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val deleteOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    val bulkDeleteOthersFile: HttpStatusCode,
    val bulkDeleteOthersFileResult: Map<UserRole, Map<FileVisibility, Int>>,
)

open class FilesTestsInput(
    val viewOthersFileCode: Map<UserRole, Map<FileVisibility, String>>,
    val renameOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    val updateVisibilityOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    val bulkUpdateVisibilityOthersFilesId: Map<UserRole, Map<FileVisibility, List<UUID>>>,
    val updateCustomLinkOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    val removeCustomLinkOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    val deleteOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    val bulkDeleteOthersFileId: Map<UserRole, Map<FileVisibility, List<UUID>>>,
)

class UserFilesTestsExpectedResults(
    uploadFile: HttpStatusCode,
    listFiles: HttpStatusCode,
    listFilesFormats: HttpStatusCode,

    val viewOwnFile: Map<FileVisibility, HttpStatusCode>,
    val viewPubliclyFileCode: Map<FileVisibility, HttpStatusCode>,
    val viewPubliclyFileCustomLink: Map<FileVisibility, HttpStatusCode>,
    val renameOwnFile: Map<FileVisibility, HttpStatusCode>,
    val updateVisibilityOwnFile: Map<FileVisibility, HttpStatusCode>,
    val bulkUpdateVisibilityOwnFile: HttpStatusCode,
    val bulkUpdateVisibilityOwnFileResult: Map<FileVisibility, Int>,
    val updateCustomLinkOwnFile: Map<FileVisibility, HttpStatusCode>,
    val removeCustomLinkOwnFile: Map<FileVisibility, HttpStatusCode>,
    val deleteOwnFile: Map<FileVisibility, HttpStatusCode>,
    val bulkDeleteOwnFile: HttpStatusCode,
    val bulkDeleteOwnFileResult: Map<FileVisibility, Int>,

    viewOthersFileAPI: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    renameOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    updateVisibilityOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    bulkUpdateVisibilityOthersFile: HttpStatusCode,
    bulkUpdateVisibilityOthersFileResult: Map<UserRole, Map<FileVisibility, Int>>,
    updateCustomLinkOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    removeCustomLinkOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    deleteOthersFile: Map<UserRole, Map<FileVisibility, HttpStatusCode>>,
    bulkDeleteOthersFile: HttpStatusCode,
    bulkDeleteOthersFileResult: Map<UserRole, Map<FileVisibility, Int>>,
) : FilesTestsExpectedResults(
    uploadFile,
    listFiles,
    listFilesFormats,
    viewOthersFileAPI,
    renameOthersFile,
    updateVisibilityOthersFile,
    bulkUpdateVisibilityOthersFile,
    bulkUpdateVisibilityOthersFileResult,
    updateCustomLinkOthersFile,
    removeCustomLinkOthersFile,
    deleteOthersFile,
    bulkDeleteOthersFile,
    bulkDeleteOthersFileResult,
)

class UserFilesTestsInput(
    val uploadToken: String,
    val viewOwnFileCode: Map<FileVisibility, String>,
    val viewPubliclyFileCode: Map<FileVisibility, String>,
    val viewPubliclyFileCustomLink: Map<FileVisibility, String>,
    val renameOwnFileId: Map<FileVisibility, UUID>,
    val updateVisibilityOwnFileId: Map<FileVisibility, UUID>,
    val bulkUpdateVisibilityOwnFilesId: Map<FileVisibility, List<UUID>>,
    val updateCustomLinkOwnFileId: Map<FileVisibility, UUID>,
    val removeCustomLinkOwnFileId: Map<FileVisibility, UUID>,
    val deleteOwnFileId: Map<FileVisibility, UUID>,
    val bulkDeleteOwnFilesId: Map<FileVisibility, List<UUID>>,

    viewOthersFileCode: Map<UserRole, Map<FileVisibility, String>>,
    renameOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    updateVisibilityOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    bulkUpdateVisibilityOthersFilesId: Map<UserRole, Map<FileVisibility, List<UUID>>>,
    updateCustomLinkOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    removeCustomLinkOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    deleteOthersFileId: Map<UserRole, Map<FileVisibility, UUID>>,
    bulkDeleteOthersFileId: Map<UserRole, Map<FileVisibility, List<UUID>>>,
) : FilesTestsInput(
    viewOthersFileCode,
    renameOthersFileId,
    updateVisibilityOthersFileId,
    bulkUpdateVisibilityOthersFilesId,
    updateCustomLinkOthersFileId,
    removeCustomLinkOthersFileId,
    deleteOthersFileId,
    bulkDeleteOthersFileId,
)

val ownerExpectedResults = UserFilesTestsExpectedResults(
    uploadFile = HttpStatusCode.Created,
    listFiles = HttpStatusCode.OK,
    listFilesFormats = HttpStatusCode.OK,

    // OWN FILES
    viewOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    viewPubliclyFileCode = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    viewPubliclyFileCustomLink = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    renameOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateVisibilityOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkUpdateVisibilityOwnFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOwnFileResult = mapOfVisibility(2, 2, 2),
    updateCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    removeCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    deleteOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkDeleteOwnFile = HttpStatusCode.OK,
    bulkDeleteOwnFileResult = mapOfVisibility(2, 2, 2),

    // OTHERS FILES
    viewOthersFileAPI = mapOfRole(
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    ),
    renameOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    updateVisibilityOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    bulkUpdateVisibilityOthersFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
    updateCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    removeCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    deleteOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    ),
    bulkDeleteOthersFile = HttpStatusCode.OK,
    bulkDeleteOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
)
val adminExpectedResults = UserFilesTestsExpectedResults(
    uploadFile = HttpStatusCode.Created,
    listFiles = HttpStatusCode.OK,
    listFilesFormats = HttpStatusCode.OK,

    // OWN FILES
    viewOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    viewPubliclyFileCode = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    viewPubliclyFileCustomLink = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    renameOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateVisibilityOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkUpdateVisibilityOwnFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOwnFileResult = mapOfVisibility(2, 2, 2),
    updateCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    removeCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    deleteOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkDeleteOwnFile = HttpStatusCode.OK,
    bulkDeleteOwnFileResult = mapOfVisibility(2, 2, 2),

    // OTHERS FILES
    viewOthersFileAPI = mapOfRole(
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    ),
    renameOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    updateVisibilityOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    bulkUpdateVisibilityOthersFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
    updateCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    removeCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    deleteOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    ),
    bulkDeleteOthersFile = HttpStatusCode.OK,
    bulkDeleteOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
)
val regularUserExpectedResults = UserFilesTestsExpectedResults(
    uploadFile = HttpStatusCode.Created,
    listFiles = HttpStatusCode.OK,
    listFilesFormats = HttpStatusCode.OK,

    // OWN FILES
    viewOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    viewPubliclyFileCode = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    viewPubliclyFileCustomLink = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    renameOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateVisibilityOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkUpdateVisibilityOwnFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOwnFileResult = mapOfVisibility(2, 2, 2),
    updateCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    removeCustomLinkOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    deleteOwnFile = mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    bulkDeleteOwnFile = HttpStatusCode.OK,
    bulkDeleteOwnFileResult = mapOfVisibility(2, 2, 2),

    // OTHERS FILES
    viewOthersFileAPI = mapOfRole(
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.NotFound),
    ),
    renameOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    updateVisibilityOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    bulkUpdateVisibilityOthersFile = HttpStatusCode.OK,
    bulkUpdateVisibilityOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
    updateCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    removeCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    deleteOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
        mapOfVisibility(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.NotFound),
    ),
    bulkDeleteOthersFile = HttpStatusCode.OK,
    bulkDeleteOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
)
val guestExpectedResults = FilesTestsExpectedResults(
    uploadFile = HttpStatusCode.Unauthorized,
    listFiles = HttpStatusCode.Unauthorized,
    listFilesFormats = HttpStatusCode.Unauthorized,

    // OTHERS FILES
    viewOthersFileAPI = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    renameOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    updateVisibilityOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    bulkUpdateVisibilityOthersFile = HttpStatusCode.Unauthorized,
    bulkUpdateVisibilityOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
    updateCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    removeCustomLinkOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    deleteOthersFile = mapOfRole(
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfVisibility(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    bulkDeleteOthersFile = HttpStatusCode.Unauthorized,
    bulkDeleteOthersFileResult = mapOfRole(
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
        mapOfVisibility(0, 0, 0),
    ),
)

val ownerInput = UserFilesTestsInput(
    uploadToken = "0000000100010000000100000000000000000000000000000000000000000000",
    viewOwnFileCode = mapOfVisibility("0100010001", "0100010002", "0100010003"),
    viewPubliclyFileCode = mapOfVisibility("0100080001", "0100080002", "0100080003"),
    viewPubliclyFileCustomLink = mapOfVisibility(
        "ffffffff-0001-0000-0001-000000000000-00000001-0000-0000-0008-000000000001-public",
        "ffffffff-0001-0000-0001-000000000000-00000001-0000-0000-0008-000000000002-unlisted",
        "ffffffff-0001-0000-0001-000000000000-00000001-0000-0000-0008-000000000003-private"
    ),
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
    bulkUpdateVisibilityOwnFilesId = mapOfVisibility(
        listOf("00000001-0000-0000-0010-000000000011".uuid(), "00000001-0000-0000-0010-000000000012".uuid()),
        listOf("00000001-0000-0000-0010-000000000021".uuid(), "00000001-0000-0000-0010-000000000022".uuid()),
        listOf("00000001-0000-0000-0010-000000000031".uuid(), "00000001-0000-0000-0010-000000000032".uuid()),
    ),
    updateCustomLinkOwnFileId = mapOfVisibility(
        "00000001-0000-0000-0006-000000000001".uuid(),
        "00000001-0000-0000-0006-000000000002".uuid(),
        "00000001-0000-0000-0006-000000000003".uuid(),
    ),
    removeCustomLinkOwnFileId = mapOfVisibility(
        "00000001-0000-0000-0007-000000000001".uuid(),
        "00000001-0000-0000-0007-000000000002".uuid(),
        "00000001-0000-0000-0007-000000000003".uuid(),
    ),
    deleteOwnFileId = mapOfVisibility(
        "00000001-0000-0000-0005-000000000001".uuid(),
        "00000001-0000-0000-0005-000000000002".uuid(),
        "00000001-0000-0000-0005-000000000003".uuid(),
    ),
    bulkDeleteOwnFilesId = mapOfVisibility(
        listOf("00000001-0000-0000-0009-000000000011".uuid(), "00000001-0000-0000-0009-000000000012".uuid()),
        listOf("00000001-0000-0000-0009-000000000021".uuid(), "00000001-0000-0000-0009-000000000022".uuid()),
        listOf("00000001-0000-0000-0009-000000000031".uuid(), "00000001-0000-0000-0009-000000000032".uuid()),
    ),

    viewOthersFileCode = mapOfRole(
        mapOfVisibility("1110010001", "1110010002", "1110010003"),
        mapOfVisibility("1210010001", "1210010002", "1210010003"),
        mapOfVisibility("1310010001", "1310010002", "1310010003"),
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
    bulkUpdateVisibilityOthersFilesId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0001-0000-0010-000000000011".uuid(), "10000001-0001-0000-0010-000000000012".uuid()),
            listOf("10000001-0001-0000-0010-000000000021".uuid(), "10000001-0001-0000-0010-000000000022".uuid()),
            listOf("10000001-0001-0000-0010-000000000031".uuid(), "10000001-0001-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0001-0000-0010-000000000011".uuid(), "10000002-0001-0000-0010-000000000012".uuid()),
            listOf("10000002-0001-0000-0010-000000000021".uuid(), "10000002-0001-0000-0010-000000000022".uuid()),
            listOf("10000002-0001-0000-0010-000000000031".uuid(), "10000002-0001-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0001-0000-0010-000000000011".uuid(), "10000003-0001-0000-0010-000000000012".uuid()),
            listOf("10000003-0001-0000-0010-000000000021".uuid(), "10000003-0001-0000-0010-000000000022".uuid()),
            listOf("10000003-0001-0000-0010-000000000031".uuid(), "10000003-0001-0000-0010-000000000032".uuid()),
        ),
    ),
    updateCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0001-0000-0006-000000000001".uuid(),
            "10000001-0001-0000-0006-000000000002".uuid(),
            "10000001-0001-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0001-0000-0006-000000000001".uuid(),
            "10000002-0001-0000-0006-000000000002".uuid(),
            "10000002-0001-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0001-0000-0006-000000000001".uuid(),
            "10000003-0001-0000-0006-000000000002".uuid(),
            "10000003-0001-0000-0006-000000000003".uuid(),
        ),
    ),
    removeCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0001-0000-0007-000000000001".uuid(),
            "10000001-0001-0000-0007-000000000002".uuid(),
            "10000001-0001-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0001-0000-0007-000000000001".uuid(),
            "10000002-0001-0000-0007-000000000002".uuid(),
            "10000002-0001-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0001-0000-0007-000000000001".uuid(),
            "10000003-0001-0000-0007-000000000002".uuid(),
            "10000003-0001-0000-0007-000000000003".uuid(),
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
    bulkDeleteOthersFileId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0001-0000-0009-000000000011".uuid(), "10000001-0001-0000-0009-000000000012".uuid()),
            listOf("10000001-0001-0000-0009-000000000021".uuid(), "10000001-0001-0000-0009-000000000022".uuid()),
            listOf("10000001-0001-0000-0009-000000000031".uuid(), "10000001-0001-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0001-0000-0009-000000000011".uuid(), "10000002-0001-0000-0009-000000000012".uuid()),
            listOf("10000002-0001-0000-0009-000000000021".uuid(), "10000002-0001-0000-0009-000000000022".uuid()),
            listOf("10000002-0001-0000-0009-000000000031".uuid(), "10000002-0001-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0001-0000-0009-000000000011".uuid(), "10000003-0001-0000-0009-000000000012".uuid()),
            listOf("10000003-0001-0000-0009-000000000021".uuid(), "10000003-0001-0000-0009-000000000022".uuid()),
            listOf("10000003-0001-0000-0009-000000000031".uuid(), "10000003-0001-0000-0009-000000000032".uuid()),
        ),
    ),
)
val adminInput = UserFilesTestsInput(
    uploadToken = "0000000200010000000100000000000000000000000000000000000000000000",
    viewOwnFileCode = mapOfVisibility("0200010001", "0200010002", "0200010003"),
    viewPubliclyFileCode = mapOfVisibility("0200080001", "0200080002", "0200080003"),
    viewPubliclyFileCustomLink = mapOfVisibility(
        "ffffffff-0001-0000-0002-000000000000-00000002-0000-0000-0008-000000000001-public",
        "ffffffff-0001-0000-0002-000000000000-00000002-0000-0000-0008-000000000002-unlisted",
        "ffffffff-0001-0000-0002-000000000000-00000002-0000-0000-0008-000000000003-private"
    ),
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
    bulkUpdateVisibilityOwnFilesId = mapOfVisibility(
        listOf("00000002-0000-0000-0010-000000000011".uuid(), "00000002-0000-0000-0010-000000000012".uuid()),
        listOf("00000002-0000-0000-0010-000000000021".uuid(), "00000002-0000-0000-0010-000000000022".uuid()),
        listOf("00000002-0000-0000-0010-000000000031".uuid(), "00000002-0000-0000-0010-000000000032".uuid()),
    ),
    updateCustomLinkOwnFileId = mapOfVisibility(
        "00000002-0000-0000-0006-000000000001".uuid(),
        "00000002-0000-0000-0006-000000000002".uuid(),
        "00000002-0000-0000-0006-000000000003".uuid(),
    ),
    removeCustomLinkOwnFileId = mapOfVisibility(
        "00000002-0000-0000-0007-000000000001".uuid(),
        "00000002-0000-0000-0007-000000000002".uuid(),
        "00000002-0000-0000-0007-000000000003".uuid(),
    ),
    deleteOwnFileId = mapOfVisibility(
        "00000002-0000-0000-0005-000000000001".uuid(),
        "00000002-0000-0000-0005-000000000002".uuid(),
        "00000002-0000-0000-0005-000000000003".uuid(),
    ),
    bulkDeleteOwnFilesId = mapOfVisibility(
        listOf("00000002-0000-0000-0009-000000000011".uuid(), "00000002-0000-0000-0009-000000000012".uuid()),
        listOf("00000002-0000-0000-0009-000000000021".uuid(), "00000002-0000-0000-0009-000000000022".uuid()),
        listOf("00000002-0000-0000-0009-000000000031".uuid(), "00000002-0000-0000-0009-000000000032".uuid()),
    ),

    viewOthersFileCode = mapOfRole(
        mapOfVisibility("1120010001", "1120010002", "1120010003"),
        mapOfVisibility("1220010001", "1220010002", "1220010003"),
        mapOfVisibility("1320010001", "1320010002", "1320010003"),
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
    bulkUpdateVisibilityOthersFilesId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0002-0000-0010-000000000011".uuid(), "10000001-0002-0000-0010-000000000012".uuid()),
            listOf("10000001-0002-0000-0010-000000000021".uuid(), "10000001-0002-0000-0010-000000000022".uuid()),
            listOf("10000001-0002-0000-0010-000000000031".uuid(), "10000001-0002-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0002-0000-0010-000000000011".uuid(), "10000002-0002-0000-0010-000000000012".uuid()),
            listOf("10000002-0002-0000-0010-000000000021".uuid(), "10000002-0002-0000-0010-000000000022".uuid()),
            listOf("10000002-0002-0000-0010-000000000031".uuid(), "10000002-0002-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0002-0000-0010-000000000011".uuid(), "10000003-0002-0000-0010-000000000012".uuid()),
            listOf("10000003-0002-0000-0010-000000000021".uuid(), "10000003-0002-0000-0010-000000000022".uuid()),
            listOf("10000003-0002-0000-0010-000000000031".uuid(), "10000003-0002-0000-0010-000000000032".uuid()),
        ),
    ),
    updateCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0002-0000-0006-000000000001".uuid(),
            "10000001-0002-0000-0006-000000000002".uuid(),
            "10000001-0002-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0002-0000-0006-000000000001".uuid(),
            "10000002-0002-0000-0006-000000000002".uuid(),
            "10000002-0002-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0002-0000-0006-000000000001".uuid(),
            "10000003-0002-0000-0006-000000000002".uuid(),
            "10000003-0002-0000-0006-000000000003".uuid(),
        ),
    ),
    removeCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0002-0000-0007-000000000001".uuid(),
            "10000001-0002-0000-0007-000000000002".uuid(),
            "10000001-0002-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0002-0000-0007-000000000001".uuid(),
            "10000002-0002-0000-0007-000000000002".uuid(),
            "10000002-0002-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0002-0000-0007-000000000001".uuid(),
            "10000003-0002-0000-0007-000000000002".uuid(),
            "10000003-0002-0000-0007-000000000003".uuid(),
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
    bulkDeleteOthersFileId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0002-0000-0009-000000000011".uuid(), "10000001-0002-0000-0009-000000000012".uuid()),
            listOf("10000001-0002-0000-0009-000000000021".uuid(), "10000001-0002-0000-0009-000000000022".uuid()),
            listOf("10000001-0002-0000-0009-000000000031".uuid(), "10000001-0002-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0002-0000-0009-000000000011".uuid(), "10000002-0002-0000-0009-000000000012".uuid()),
            listOf("10000002-0002-0000-0009-000000000021".uuid(), "10000002-0002-0000-0009-000000000022".uuid()),
            listOf("10000002-0002-0000-0009-000000000031".uuid(), "10000002-0002-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0002-0000-0009-000000000011".uuid(), "10000003-0002-0000-0009-000000000012".uuid()),
            listOf("10000003-0002-0000-0009-000000000021".uuid(), "10000003-0002-0000-0009-000000000022".uuid()),
            listOf("10000003-0002-0000-0009-000000000031".uuid(), "10000003-0002-0000-0009-000000000032".uuid()),
        ),
    ),
)
val regularUserInput = UserFilesTestsInput(
    uploadToken = "0000000300010000000100000000000000000000000000000000000000000000",
    viewOwnFileCode = mapOfVisibility("0300010001", "0300010002", "0300010003"),
    viewPubliclyFileCode = mapOfVisibility("0300080001", "0300080002", "0300080003"),
    viewPubliclyFileCustomLink = mapOfVisibility(
        "ffffffff-0001-0000-0003-000000000000-00000003-0000-0000-0008-000000000001-public",
        "ffffffff-0001-0000-0003-000000000000-00000003-0000-0000-0008-000000000002-unlisted",
        "ffffffff-0001-0000-0003-000000000000-00000003-0000-0000-0008-000000000003-private"
    ),
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
    bulkUpdateVisibilityOwnFilesId = mapOfVisibility(
        listOf("00000003-0000-0000-0010-000000000011".uuid(), "00000003-0000-0000-0010-000000000012".uuid()),
        listOf("00000003-0000-0000-0010-000000000021".uuid(), "00000003-0000-0000-0010-000000000022".uuid()),
        listOf("00000003-0000-0000-0010-000000000031".uuid(), "00000003-0000-0000-0010-000000000032".uuid()),
    ),
    updateCustomLinkOwnFileId = mapOfVisibility(
        "00000003-0000-0000-0006-000000000001".uuid(),
        "00000003-0000-0000-0006-000000000002".uuid(),
        "00000003-0000-0000-0006-000000000003".uuid(),
    ),
    removeCustomLinkOwnFileId = mapOfVisibility(
        "00000003-0000-0000-0007-000000000001".uuid(),
        "00000003-0000-0000-0007-000000000002".uuid(),
        "00000003-0000-0000-0007-000000000003".uuid(),
    ),
    deleteOwnFileId = mapOfVisibility(
        "00000003-0000-0000-0005-000000000001".uuid(),
        "00000003-0000-0000-0005-000000000002".uuid(),
        "00000003-0000-0000-0005-000000000003".uuid(),
    ),
    bulkDeleteOwnFilesId = mapOfVisibility(
        listOf("00000003-0000-0000-0009-000000000011".uuid(), "00000003-0000-0000-0009-000000000012".uuid()),
        listOf("00000003-0000-0000-0009-000000000021".uuid(), "00000003-0000-0000-0009-000000000022".uuid()),
        listOf("00000003-0000-0000-0009-000000000031".uuid(), "00000003-0000-0000-0009-000000000032".uuid()),
    ),

    viewOthersFileCode = mapOfRole(
        mapOfVisibility("1130010001", "1130010002", "1130010003"),
        mapOfVisibility("1230010001", "1230010002", "1230010003"),
        mapOfVisibility("1330010001", "1330010002", "1330010003"),
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
    bulkUpdateVisibilityOthersFilesId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0003-0000-0010-000000000011".uuid(), "10000001-0003-0000-0010-000000000012".uuid()),
            listOf("10000001-0003-0000-0010-000000000021".uuid(), "10000001-0003-0000-0010-000000000022".uuid()),
            listOf("10000001-0003-0000-0010-000000000031".uuid(), "10000001-0003-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0003-0000-0010-000000000011".uuid(), "10000002-0003-0000-0010-000000000012".uuid()),
            listOf("10000002-0003-0000-0010-000000000021".uuid(), "10000002-0003-0000-0010-000000000022".uuid()),
            listOf("10000002-0003-0000-0010-000000000031".uuid(), "10000002-0003-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0003-0000-0010-000000000011".uuid(), "10000003-0003-0000-0010-000000000012".uuid()),
            listOf("10000003-0003-0000-0010-000000000021".uuid(), "10000003-0003-0000-0010-000000000022".uuid()),
            listOf("10000003-0003-0000-0010-000000000031".uuid(), "10000003-0003-0000-0010-000000000032".uuid()),
        ),
    ),
    updateCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0003-0000-0006-000000000001".uuid(),
            "10000001-0003-0000-0006-000000000002".uuid(),
            "10000001-0003-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0003-0000-0006-000000000001".uuid(),
            "10000002-0003-0000-0006-000000000002".uuid(),
            "10000002-0003-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0003-0000-0006-000000000001".uuid(),
            "10000003-0003-0000-0006-000000000002".uuid(),
            "10000003-0003-0000-0006-000000000003".uuid(),
        ),
    ),
    removeCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-0003-0000-0007-000000000001".uuid(),
            "10000001-0003-0000-0007-000000000002".uuid(),
            "10000001-0003-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-0003-0000-0007-000000000001".uuid(),
            "10000002-0003-0000-0007-000000000002".uuid(),
            "10000002-0003-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-0003-0000-0007-000000000001".uuid(),
            "10000003-0003-0000-0007-000000000002".uuid(),
            "10000003-0003-0000-0007-000000000003".uuid(),
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
    bulkDeleteOthersFileId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-0003-0000-0009-000000000011".uuid(), "10000001-0003-0000-0009-000000000012".uuid()),
            listOf("10000001-0003-0000-0009-000000000021".uuid(), "10000001-0003-0000-0009-000000000022".uuid()),
            listOf("10000001-0003-0000-0009-000000000031".uuid(), "10000001-0003-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-0003-0000-0009-000000000011".uuid(), "10000002-0003-0000-0009-000000000012".uuid()),
            listOf("10000002-0003-0000-0009-000000000021".uuid(), "10000002-0003-0000-0009-000000000022".uuid()),
            listOf("10000002-0003-0000-0009-000000000031".uuid(), "10000002-0003-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-0003-0000-0009-000000000011".uuid(), "10000003-0003-0000-0009-000000000012".uuid()),
            listOf("10000003-0003-0000-0009-000000000021".uuid(), "10000003-0003-0000-0009-000000000022".uuid()),
            listOf("10000003-0003-0000-0009-000000000031".uuid(), "10000003-0003-0000-0009-000000000032".uuid()),
        ),
    ),
)
val guestInput = FilesTestsInput(
    viewOthersFileCode = mapOfRole(
        mapOfVisibility("11f0010001", "11f0010002", "11f0010003"),
        mapOfVisibility("12f0010001", "12f0010002", "12f0010003"),
        mapOfVisibility("13f0010001", "13f0010002", "13f0010003"),
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
    bulkUpdateVisibilityOthersFilesId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-000f-0000-0010-000000000011".uuid(), "10000001-000f-0000-0010-000000000012".uuid()),
            listOf("10000001-000f-0000-0010-000000000021".uuid(), "10000001-000f-0000-0010-000000000022".uuid()),
            listOf("10000001-000f-0000-0010-000000000031".uuid(), "10000001-000f-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-000f-0000-0010-000000000011".uuid(), "10000002-000f-0000-0010-000000000012".uuid()),
            listOf("10000002-000f-0000-0010-000000000021".uuid(), "10000002-000f-0000-0010-000000000022".uuid()),
            listOf("10000002-000f-0000-0010-000000000031".uuid(), "10000002-000f-0000-0010-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-000f-0000-0010-000000000011".uuid(), "10000003-000f-0000-0010-000000000012".uuid()),
            listOf("10000003-000f-0000-0010-000000000021".uuid(), "10000003-000f-0000-0010-000000000022".uuid()),
            listOf("10000003-000f-0000-0010-000000000031".uuid(), "10000003-000f-0000-0010-000000000032".uuid()),
        ),
    ),
    updateCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-000f-0000-0006-000000000001".uuid(),
            "10000001-000f-0000-0006-000000000002".uuid(),
            "10000001-000f-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-000f-0000-0006-000000000001".uuid(),
            "10000002-000f-0000-0006-000000000002".uuid(),
            "10000002-000f-0000-0006-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-000f-0000-0006-000000000001".uuid(),
            "10000003-000f-0000-0006-000000000002".uuid(),
            "10000003-000f-0000-0006-000000000003".uuid(),
        ),
    ),
    removeCustomLinkOthersFileId = mapOfRole(
        mapOfVisibility(
            "10000001-000f-0000-0007-000000000001".uuid(),
            "10000001-000f-0000-0007-000000000002".uuid(),
            "10000001-000f-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000002-000f-0000-0007-000000000001".uuid(),
            "10000002-000f-0000-0007-000000000002".uuid(),
            "10000002-000f-0000-0007-000000000003".uuid(),
        ),
        mapOfVisibility(
            "10000003-000f-0000-0007-000000000001".uuid(),
            "10000003-000f-0000-0007-000000000002".uuid(),
            "10000003-000f-0000-0007-000000000003".uuid(),
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
    bulkDeleteOthersFileId = mapOfRole(
        mapOfVisibility(
            listOf("10000001-000f-0000-0009-000000000011".uuid(), "10000001-000f-0000-0009-000000000012".uuid()),
            listOf("10000001-000f-0000-0009-000000000021".uuid(), "10000001-000f-0000-0009-000000000022".uuid()),
            listOf("10000001-000f-0000-0009-000000000031".uuid(), "10000001-000f-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000002-000f-0000-0009-000000000011".uuid(), "10000002-000f-0000-0009-000000000012".uuid()),
            listOf("10000002-000f-0000-0009-000000000021".uuid(), "10000002-000f-0000-0009-000000000022".uuid()),
            listOf("10000002-000f-0000-0009-000000000031".uuid(), "10000002-000f-0000-0009-000000000032".uuid()),
        ),
        mapOfVisibility(
            listOf("10000003-000f-0000-0009-000000000011".uuid(), "10000003-000f-0000-0009-000000000012".uuid()),
            listOf("10000003-000f-0000-0009-000000000021".uuid(), "10000003-000f-0000-0009-000000000022".uuid()),
            listOf("10000003-000f-0000-0009-000000000031".uuid(), "10000003-000f-0000-0009-000000000032".uuid()),
        ),
    ),
)
