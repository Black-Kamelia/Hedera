package com.kamelia.hedera.rest.user

import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.uuid
import io.ktor.http.*
import java.util.*

open class SelfUsersTestsExpectedResults(
    listUsers: HttpStatusCode,
    createUser: Map<UserRole, HttpStatusCode>,
    val updateOwnUsername: HttpStatusCode,
    val updateOwnEmail: HttpStatusCode,
    val updateOwnRole: Map<UserRole, HttpStatusCode>,
    val updateOwnQuota: HttpStatusCode,
    val updateOwnPassword: HttpStatusCode,
    val activateSelf: HttpStatusCode,
    val deactivateSelf: HttpStatusCode,
    val deleteSelf: HttpStatusCode,

    updateOthersUsername: Map<UserRole, HttpStatusCode>,
    updateOthersEmail: Map<UserRole, HttpStatusCode>,
    updateOthersRole: Map<UserRole, Map<UserRole, HttpStatusCode>>,
    updateOthersQuota: Map<UserRole, HttpStatusCode>,
    updateOthersPassword: Map<UserRole, HttpStatusCode>,
    activateUser: Map<UserRole, HttpStatusCode>,
    deactivateUser: Map<UserRole, HttpStatusCode>,
    deleteUser: Map<UserRole, HttpStatusCode>,
) : UsersTestsExpectedResults(
    listUsers,
    createUser,
    updateOthersUsername,
    updateOthersEmail,
    updateOthersRole,
    updateOthersQuota,
    updateOthersPassword,
    activateUser,
    deactivateUser,
    deleteUser,
)

open class SelfUsersTestsInput(
    val updateOwnUsernameUserId: UUID,
    val updateOwnEmailUserId: UUID,
    val updateOwnRoleUserId: Map<UserRole, UUID>,
    val updateOwnQuotaUserId: UUID,
    val updateOwnPasswordUserId: UUID,
    val activateSelfUserId: UUID,
    val deactivateSelfUserId: UUID,
    val deleteSelfUserId: UUID,

    updateOthersUsernameUserId: Map<UserRole, UUID>,
    updateOthersEmailUserId: Map<UserRole, UUID>,
    updateOthersRoleUserId: Map<UserRole, Map<UserRole, UUID>>,
    updateOthersQuotaUserId: Map<UserRole, UUID>,
    updateOthersPasswordUserId: Map<UserRole, UUID>,
    activateUserId: Map<UserRole, UUID>,
    deactivateUserId: Map<UserRole, UUID>,
    deleteUserId: Map<UserRole, UUID>,
) : UsersTestsInput(
    updateOthersUsernameUserId,
    updateOthersEmailUserId,
    updateOthersRoleUserId,
    updateOthersQuotaUserId,
    updateOthersPasswordUserId,
    activateUserId,
    deactivateUserId,
    deleteUserId,
)

open class UsersTestsExpectedResults(
    val listUsers: HttpStatusCode,
    val createUser: Map<UserRole, HttpStatusCode>,

    val updateOthersUsername: Map<UserRole, HttpStatusCode>,
    val updateOthersEmail: Map<UserRole, HttpStatusCode>,
    val updateOthersRole: Map<UserRole, Map<UserRole, HttpStatusCode>>,
    val updateOthersQuota: Map<UserRole, HttpStatusCode>,
    val updateOthersPassword: Map<UserRole, HttpStatusCode>,
    val activateUser: Map<UserRole, HttpStatusCode>,
    val deactivateUser: Map<UserRole, HttpStatusCode>,
    val deleteUser: Map<UserRole, HttpStatusCode>,
)

open class UsersTestsInput(
    val updateOthersUsernameUserId: Map<UserRole, UUID>,
    val updateOthersEmailUserId: Map<UserRole, UUID>,
    val updateOthersRoleUserId: Map<UserRole, Map<UserRole, UUID>>,
    val updateOthersQuotaUserId: Map<UserRole, UUID>,
    val updateOthersPasswordUserId: Map<UserRole, UUID>,
    val activateUserId: Map<UserRole, UUID>,
    val deactivateUserId: Map<UserRole, UUID>,
    val deleteUserId: Map<UserRole, UUID>,
)

val ownerExpectedResults = SelfUsersTestsExpectedResults(
    listUsers = HttpStatusCode.OK,
    createUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Created, HttpStatusCode.Created),

    updateOwnUsername = HttpStatusCode.OK,
    updateOwnEmail = HttpStatusCode.OK,
    updateOwnRole = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOwnQuota = HttpStatusCode.OK,
    updateOwnPassword = HttpStatusCode.OK,
    activateSelf = HttpStatusCode.Forbidden,
    deactivateSelf = HttpStatusCode.Forbidden,
    deleteSelf = HttpStatusCode.Forbidden,

    updateOthersUsername = mapOfRole(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateOthersEmail = mapOfRole(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateOthersRole = mapOfRole(
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.OK, HttpStatusCode.OK),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.OK, HttpStatusCode.OK),
    ),
    updateOthersQuota = mapOfRole(HttpStatusCode.OK, HttpStatusCode.OK, HttpStatusCode.OK),
    updateOthersPassword = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    activateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.OK, HttpStatusCode.OK),
    deactivateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.OK, HttpStatusCode.OK),
    deleteUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.OK, HttpStatusCode.OK),
)
val adminExpectedResults = SelfUsersTestsExpectedResults(
    listUsers = HttpStatusCode.OK,
    createUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Created),

    updateOwnUsername = HttpStatusCode.OK,
    updateOwnEmail = HttpStatusCode.OK,
    updateOwnRole = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOwnQuota = HttpStatusCode.Forbidden,
    updateOwnPassword = HttpStatusCode.OK,
    activateSelf = HttpStatusCode.Forbidden,
    deactivateSelf = HttpStatusCode.Forbidden,
    deleteSelf = HttpStatusCode.Forbidden,

    updateOthersUsername = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    updateOthersEmail = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    updateOthersRole = mapOfRole(
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    ),
    updateOthersQuota = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    updateOthersPassword = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    activateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    deactivateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
    deleteUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.OK),
)
val regularUserExpectedResults = SelfUsersTestsExpectedResults(
    listUsers = HttpStatusCode.Forbidden,
    createUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),

    updateOwnUsername = HttpStatusCode.OK,
    updateOwnEmail = HttpStatusCode.OK,
    updateOwnRole = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOwnQuota = HttpStatusCode.Forbidden,
    updateOwnPassword = HttpStatusCode.OK,
    activateSelf = HttpStatusCode.Forbidden,
    deactivateSelf = HttpStatusCode.Forbidden,
    deleteSelf = HttpStatusCode.Forbidden,

    updateOthersUsername = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOthersEmail = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOthersRole = mapOfRole(
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
        mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    ),
    updateOthersQuota = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    updateOthersPassword = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    activateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    deactivateUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
    deleteUser = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
)
val guestExpectedResults = UsersTestsExpectedResults(
    listUsers = HttpStatusCode.Unauthorized,
    createUser = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),

    updateOthersUsername = mapOfRole(
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized
    ),
    updateOthersEmail = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    updateOthersRole = mapOfRole(
        mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
        mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    ),
    updateOthersQuota = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    updateOthersPassword = mapOfRole(
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized
    ),
    activateUser = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    deactivateUser = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
    deleteUser = mapOfRole(HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized, HttpStatusCode.Unauthorized),
)

val ownerInput = SelfUsersTestsInput(
    updateOwnUsernameUserId = "ffffffff-0002-0000-0001-000000000001".uuid(),
    updateOwnEmailUserId = "ffffffff-0002-0000-0001-000000000002".uuid(),
    updateOwnRoleUserId = mapOfRole(
        "ffffffff-0002-0000-0001-000000000003".uuid(),
        "ffffffff-0002-0000-0001-000000000004".uuid(),
        "ffffffff-0002-0000-0001-000000000005".uuid(),
    ),
    updateOwnQuotaUserId = "ffffffff-0002-0000-0001-000000000006".uuid(),
    updateOwnPasswordUserId = "ffffffff-0002-0000-0001-000000000007".uuid(),
    activateSelfUserId = "ffffffff-0002-0000-0001-000000000008".uuid(),
    deactivateSelfUserId = "ffffffff-0002-0000-0001-000000000009".uuid(),
    deleteSelfUserId = "ffffffff-0002-0000-0001-000000000010".uuid(),

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
)
val adminInput = SelfUsersTestsInput(
    updateOwnUsernameUserId = "ffffffff-0002-0000-0002-000000000001".uuid(),
    updateOwnEmailUserId = "ffffffff-0002-0000-0002-000000000002".uuid(),
    updateOwnRoleUserId = mapOfRole(
        "ffffffff-0002-0000-0002-000000000003".uuid(),
        "ffffffff-0002-0000-0002-000000000004".uuid(),
        "ffffffff-0002-0000-0002-000000000005".uuid(),
    ),
    updateOwnQuotaUserId = "ffffffff-0002-0000-0002-000000000006".uuid(),
    updateOwnPasswordUserId = "ffffffff-0002-0000-0002-000000000007".uuid(),
    activateSelfUserId = "ffffffff-0002-0000-0002-000000000008".uuid(),
    deactivateSelfUserId = "ffffffff-0002-0000-0002-000000000009".uuid(),
    deleteSelfUserId = "ffffffff-0002-0000-0002-000000000010".uuid(),

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
)
val regularUserInput = SelfUsersTestsInput(
    updateOwnUsernameUserId = "ffffffff-0002-0000-0003-000000000001".uuid(),
    updateOwnEmailUserId = "ffffffff-0002-0000-0003-000000000002".uuid(),
    updateOwnRoleUserId = mapOfRole(
        "ffffffff-0002-0000-0003-000000000003".uuid(),
        "ffffffff-0002-0000-0003-000000000004".uuid(),
        "ffffffff-0002-0000-0003-000000000005".uuid(),
    ),
    updateOwnQuotaUserId = "ffffffff-0002-0000-0003-000000000006".uuid(),
    updateOwnPasswordUserId = "ffffffff-0002-0000-0003-000000000007".uuid(),
    activateSelfUserId = "ffffffff-0002-0000-0003-000000000008".uuid(),
    deactivateSelfUserId = "ffffffff-0002-0000-0003-000000000009".uuid(),
    deleteSelfUserId = "ffffffff-0002-0000-0003-000000000010".uuid(),

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
)
val guestInput = UsersTestsInput(
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
)
