package com.kamelia.hedera.rest.token

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.uuid
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PersonalTokensEdgeCasesTests {

    @DisplayName("List token does not list deleted tokens")
    @Test
    fun listTokenDoesNotListDeletedTokens() = testApplication {
        val (tokens, _) = deletedTokenUser

        val response = client().get("/api/personalTokens") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val tokensList = Json.decodeFromString<List<PersonalTokenDTO>>(response.bodyAsText())
        tokensList.forEach { token ->
            token.deleted?.let { assertFalse(it) }
            assertEquals("not_deleted_token", token.name)
        }
    }

    @DisplayName("List token does not list tokens of other users")
    @Test
    fun listTokenDoesNotListTokensOfOtherUsers() = testApplication {
        val (tokens, _) = ownTokensUser

        val response = client().get("/api/personalTokens") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val tokensList = Json.decodeFromString<List<PersonalTokenDTO>>(response.bodyAsText())
        assertTrue(tokensList.all { it.name == "my_very_own_token" })
    }

    @DisplayName("Delete unknown token")
    @Test
    fun deleteUnknownToken() = testApplication {
        val (tokens, _) = user

        val response = client().delete("/api/personalTokens/${UUID(0, 0)}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.NOT_FOUND, responseDto.title.key)
    }

    @DisplayName("Delete already deleted token")
    @Test
    fun deleteAlreadyDeletedToken() = testApplication {
        val (tokens, _) = user

        val response = client().delete("/api/personalTokens/00000000-000a-0000-0001-000000000001") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseDto = Json.decodeFromString<MessageDTO<Nothing>>(response.bodyAsText())
        assertEquals(Errors.PersonalTokens.NOT_FOUND, responseDto.title.key)
    }

    companion object {

        private lateinit var user: TestUser
        private lateinit var deletedTokenUser: TestUser
        private lateinit var ownTokensUser: TestUser

        init {
            testApplication {
                user = Pair(
                    login("personal.tokens.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0004-0000-0001-000000000000".uuid()
                )
                deletedTokenUser = Pair(
                    login("dummy.personal.tokens.deleted", "password").second ?: throw Exception("Login failed"),
                    "00000000-000a-0000-0001-000000000000".uuid()
                )
                ownTokensUser = Pair(
                    login("dummy.personal.tokens.own.tokens", "password").second ?: throw Exception("Login failed"),
                    "00000000-000a-0000-0002-000000000000".uuid()
                )
            }
        }
    }

}