package com.kamelia.hedera.rest.token

import com.kamelia.hedera.mapOfRole
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.uuid
import io.ktor.http.*
import java.util.*


open class PersonalTokensTestsExpectedResults(
    val createPersonalToken: HttpStatusCode,
    val listPersonalTokens: HttpStatusCode,
    val listPersonalTokensWithUsages: HttpStatusCode,
    val deleteOthersPersonalToken: Map<UserRole, HttpStatusCode>,
)

open class PersonalTokensTestsInput(
    val deleteOthersPersonalTokenId: Map<UserRole, UUID>,
)

class UserPersonalTokensTestsExpectedResults(
    val deleteOwnPersonalToken: HttpStatusCode,

    createPersonalToken: HttpStatusCode,
    listPersonalTokens: HttpStatusCode,
    listPersonalTokensWithUsages: HttpStatusCode,
    deleteOthersPersonalToken: Map<UserRole, HttpStatusCode>,
) : PersonalTokensTestsExpectedResults(
    createPersonalToken,
    listPersonalTokens,
    listPersonalTokensWithUsages,
    deleteOthersPersonalToken,
)

class UserPersonalTokensTestsInput(
    val deleteOwnPersonalTokenId: UUID,

    deleteOthersPersonalTokenId: Map<UserRole, UUID>,
) : PersonalTokensTestsInput(
    deleteOthersPersonalTokenId
)

val ownerExpectedResults = UserPersonalTokensTestsExpectedResults(
    deleteOwnPersonalToken = HttpStatusCode.OK,

    listPersonalTokens = HttpStatusCode.OK,
    listPersonalTokensWithUsages = HttpStatusCode.OK,
    createPersonalToken = HttpStatusCode.Created,
    deleteOthersPersonalToken = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
)
val adminExpectedResults = UserPersonalTokensTestsExpectedResults(
    deleteOwnPersonalToken = HttpStatusCode.OK,

    listPersonalTokens = HttpStatusCode.OK,
    listPersonalTokensWithUsages = HttpStatusCode.OK,
    createPersonalToken = HttpStatusCode.Created,
    deleteOthersPersonalToken = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
)
val regularUserFilesTests = UserPersonalTokensTestsExpectedResults(
    deleteOwnPersonalToken = HttpStatusCode.OK,

    listPersonalTokens = HttpStatusCode.OK,
    listPersonalTokensWithUsages = HttpStatusCode.OK,
    createPersonalToken = HttpStatusCode.Created,
    deleteOthersPersonalToken = mapOfRole(HttpStatusCode.Forbidden, HttpStatusCode.Forbidden, HttpStatusCode.Forbidden),
)
val guestExpectedResults = PersonalTokensTestsExpectedResults(
    listPersonalTokens = HttpStatusCode.Unauthorized,
    listPersonalTokensWithUsages = HttpStatusCode.Unauthorized,
    createPersonalToken = HttpStatusCode.Unauthorized,
    deleteOthersPersonalToken = mapOfRole(
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Unauthorized
    ),
)

val ownerInput = UserPersonalTokensTestsInput(
    deleteOwnPersonalTokenId = "00000001-0002-0000-0001-000000000000".uuid(),

    deleteOthersPersonalTokenId = mapOfRole(
        "00000001-0002-0000-0001-000000000001".uuid(),
        "00000002-0002-0000-0001-000000000001".uuid(),
        "00000003-0002-0000-0001-000000000001".uuid(),
    )
)
val adminInput = UserPersonalTokensTestsInput(
    deleteOwnPersonalTokenId = "00000002-0002-0000-0001-000000000000".uuid(),

    deleteOthersPersonalTokenId = mapOfRole(
        "00000001-0002-0000-0001-000000000002".uuid(),
        "00000002-0002-0000-0001-000000000002".uuid(),
        "00000003-0002-0000-0001-000000000002".uuid(),
    ),
)
val regularUserInput = UserPersonalTokensTestsInput(
    deleteOwnPersonalTokenId = "00000003-0002-0000-0001-000000000000".uuid(),

    deleteOthersPersonalTokenId = mapOfRole(
        "00000001-0002-0000-0001-000000000003".uuid(),
        "00000002-0002-0000-0001-000000000003".uuid(),
        "00000003-0002-0000-0001-000000000003".uuid(),
    ),
)
val guestInput = PersonalTokensTestsInput(
    deleteOthersPersonalTokenId = mapOfRole(
        "00000001-0002-0000-0001-000000000004".uuid(),
        "00000002-0002-0000-0001-000000000004".uuid(),
        "00000003-0002-0000-0001-000000000004".uuid(),
    ),
)
