package com.kamelia.hedera.rest

import com.kamelia.hedera.GlobalConfigurationSetup
import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.core.response.MessageDTO
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.token.PersonalTokenCreationDTO
import com.kamelia.hedera.rest.token.PersonalTokenDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(GlobalConfigurationSetup::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonalTokensTest {

    @DisplayName("Creating a token")
    @ParameterizedTest(name = "Creating a token as {0} is {1}")
    @MethodSource
    fun createToken(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.post("/api/personalTokens") {
            contentType(ContentType.Application.Json)
            setBody(PersonalTokenCreationDTO("test"))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.Created) {
            val responseDto = Json.decodeFromString<MessageDTO<PersonalTokenDTO>>(response.bodyAsText())
            val tokenDto = responseDto.payload
            assertNotNull(tokenDto)
            assertEquals("test", tokenDto.name)
            assertNull(tokenDto.lastUsed)
            assertNotNull(tokenDto.token)
        }
    }

    @DisplayName("Listing tokens")
    @ParameterizedTest(name = "Listing tokens as {0} is {1}")
    @MethodSource
    fun getTokens(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/personalTokens") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)
        if (response.status == HttpStatusCode.OK) {
            val personalTokens = Json.decodeFromString<List<PersonalTokenDTO>>(response.bodyAsText())
            assertTrue(personalTokens.isNotEmpty())
            assertEquals(1, personalTokens.size)
        }
    }

    @DisplayName("Getting deleted token returns nothing")
    @Test
    fun getDeletedToken() = testApplication {
        val (_, tokens) = login("list_deleted_token", "password")
        val client = client()
        val response = client.get("/api/personalTokens") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val personalTokens = Json.decodeFromString<List<PersonalTokenDTO>>(response.bodyAsText())
        assertTrue(personalTokens.isEmpty())
    }

    @DisplayName("Deleting own token")
    @ParameterizedTest(name = "Deleting own token as {0} is {2}")
    @MethodSource
    fun deleteOwnToken(
        user: TestUser,
        tokenId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/personalTokens/${tokenId}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)
    }

    @DisplayName("Deleting other one token")
    @ParameterizedTest(name = "Deleting other one token as {0} is {2}")
    @MethodSource
    fun deleteOtherToken(
        user: TestUser,
        tokenId: UUID,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.delete("/api/personalTokens/${tokenId}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)
    }

    @DisplayName("Deleting unknown token")
    @Test
    fun deleteUnknownToken() = testApplication {
        val (tokens, _) = superadmin
        val client = client()
        val response = client.delete("/api/personalTokens/${UUID(0, 0)}") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @DisplayName("Deleting already deleted token")
    @Test
    fun deleteAlreadyDeletedToken() = testApplication {
        val (_, tokens) = login("list_deleted_token", "password")
        val client = client()
        val response = client.delete("/api/personalTokens/00000000-0000-0000-0003-000000000001") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private lateinit var superadminListToken: TestUser
        private lateinit var adminListToken: TestUser
        private lateinit var userListToken: TestUser
        private lateinit var superadminDeleteToken: TestUser
        private lateinit var adminDeleteToken: TestUser
        private lateinit var userDeleteToken: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            testApplication {
                superadmin = Pair(
                    login("owner1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
                )
                admin = Pair(
                    login("admin1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000002")
                )
                user = Pair(
                    login("user1", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000000-0000-0000-0000-000000000003")
                )
                superadminListToken = Pair(
                    login("owner_list_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0001-000000000001")
                )
                adminListToken = Pair(
                    login("admin_list_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0001-000000000002")
                )
                userListToken = Pair(
                    login("user_list_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0001-000000000003")
                )
                superadminDeleteToken = Pair(
                    login("owner_delete_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0000-000000000001")
                )
                adminDeleteToken = Pair(
                    login("admin_delete_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0000-000000000002")
                )
                userDeleteToken = Pair(
                    login("user_delete_token", "password").second ?: throw Exception("Login failed"),
                    UUID.fromString("00000002-0000-0000-0000-000000000003")
                )
            }
        }

        @JvmStatic
        fun createToken(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("superadmin", superadmin), HttpStatusCode.Created),
            Arguments.of(Named.of("admin", admin), HttpStatusCode.Created),
            Arguments.of(Named.of("regular user", user), HttpStatusCode.Created),
            Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
        )

        @JvmStatic
        fun getTokens(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of("superadmin", superadminListToken), HttpStatusCode.OK),
            Arguments.of(Named.of("admin", adminListToken), HttpStatusCode.OK),
            Arguments.of(Named.of("regular user", userListToken), HttpStatusCode.OK),
            Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
        )

        @JvmStatic
        fun deleteOwnToken(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadminDeleteToken),
                UUID.fromString("00000000-0000-0000-0001-000000000001"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("admin", adminDeleteToken),
                UUID.fromString("00000000-0000-0000-0001-000000000002"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("regular user", userDeleteToken),
                UUID.fromString("00000000-0000-0000-0001-000000000003"),
                HttpStatusCode.OK
            ),
            Arguments.of(
                Named.of("guest", guest),
                UUID.fromString("00000000-0000-0000-0001-000000000004"),
                HttpStatusCode.Unauthorized
            ),
        )

        @JvmStatic
        fun deleteOtherToken(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of("superadmin", superadminDeleteToken),
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("admin", adminDeleteToken),
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("regular user", userDeleteToken),
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                HttpStatusCode.Forbidden
            ),
            Arguments.of(
                Named.of("guest", guest),
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                HttpStatusCode.Unauthorized
            ),
        )
    }

}