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
import java.util.stream.Stream
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractPersonalTokensTests(
    private val user: TestUser,
    private val expectedResults: PersonalTokensTestsExpectedResults,
    private val input: PersonalTokensTestsInput,
) {

    @DisplayName("Create personal token")
    @Test
    fun createPersonalTokenTest() = testApplication {
        val (tokens, userId) = user
        val client = client()

        val tokenName = "$userId-create-token"
        val response = client.post("/api/personalTokens") {
            contentType(ContentType.Application.Json)
            setBody(PersonalTokenCreationDTO(tokenName))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.createPersonalToken, response.status)

        if (response.status == HttpStatusCode.Created) {
            val responseDto = Json.decodeFromString<MessageDTO<PersonalTokenDTO>>(response.bodyAsText())
            assertEquals(Actions.Tokens.Create.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Tokens.Create.Success.MESSAGE, responseDto.message!!.key)
            assertEquals(tokenName, responseDto.payload!!.name)
            assertNotNull(responseDto.payload!!.token)
        }
    }

    @DisplayName("List personal tokens")
    @Test
    fun listPersonalTokensTest() = testApplication {
        val (tokens, _) = user
        val client = client()

        val response = client.get("/api/personalTokens") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.listPersonalTokens, response.status)

        if (response.status == HttpStatusCode.OK) {
            val tokensList = Json.decodeFromString<List<PersonalTokenDTO>>(response.bodyAsText())
                .filter { !it.name.matches(""".*(create|delete).*""".toRegex()) }

            assertEquals(2, tokensList.size)
            tokensList.forEach { assertNull(it.token) }
        }
    }

    @DisplayName("Delete others personal token")
    @ParameterizedTest(name = "Delete {0}''s personal token")
    @MethodSource("roles")
    fun deleteOthersPersonalTokenTest(
        target: UserRole,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val tokenId = input.deleteOthersPersonalTokenId[target]!!
        val response = client.delete("/api/personalTokens/$tokenId") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.deleteOthersPersonalToken[target]!!, response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
            assertEquals(Actions.Tokens.Delete.Success.TITLE, responseDto.title.key)
            assertEquals(Actions.Tokens.Delete.Success.MESSAGE, responseDto.message!!.key)
        }
    }

    companion object {

        @JvmStatic
        fun roles(): Stream<Arguments> = Stream.of(
            Named.of("owner", UserRole.OWNER),
            Named.of("admin", UserRole.ADMIN),
            Named.of("regular user", UserRole.REGULAR),
        ).map { Arguments.of(it) }

    }
}

open class PersonalTokensTestsExpectedResults(
    val createPersonalToken: HttpStatusCode,
    val listPersonalTokens: HttpStatusCode,
    val deleteOthersPersonalToken: Map<UserRole, HttpStatusCode>,
)

open class PersonalTokensTestsInput(
    val deleteOthersPersonalTokenId: Map<UserRole, UUID>,
)