package com.kamelia.hedera.rest.token

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

abstract class AbstractUserPersonalTokensTests(
    private val user: TestUser,
    private val expectedResults: UserPersonalTokensTestsExpectedResults,
    private val input: UserPersonalTokensTestsInput,
) : AbstractPersonalTokensTests(user, expectedResults, input) {

    @DisplayName("Delete own personal token")
    @Test
    fun deleteOwnPersonalTokenTest() = testApplication {
        val (tokens, _) = user
        val client = client()

        val tokenId = input.deleteOwnPersonalTokenId
        val response = client.delete("/api/personalTokens/$tokenId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOwnPersonalToken, response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
            assertEquals(Actions.Tokens.Delete.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Tokens.Delete.Success.MESSAGE, responseDto.message!!.key)
        }
    }
}

class UserPersonalTokensTestsExpectedResults(
    val deleteOwnPersonalToken: HttpStatusCode,

    createPersonalToken: HttpStatusCode,
    listPersonalTokens: HttpStatusCode,
    deleteOthersPersonalToken: Map<UserRole, HttpStatusCode>,
) : PersonalTokensTestsExpectedResults(
    createPersonalToken,
    listPersonalTokens,
    deleteOthersPersonalToken
)

class UserPersonalTokensTestsInput(
    val deleteOwnPersonalTokenId: UUID,

    deleteOthersPersonalTokenId: Map<UserRole, UUID>,
) : PersonalTokensTestsInput(
    deleteOthersPersonalTokenId
)
