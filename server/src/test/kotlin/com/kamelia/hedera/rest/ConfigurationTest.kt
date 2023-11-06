package com.kamelia.hedera.rest

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.configuration.GlobalConfigurationRepresentationDTO
import com.kamelia.hedera.rest.configuration.GlobalConfigurationUpdateDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigurationTest {

    @DisplayName("Get global configuration")
    @ParameterizedTest(name = "Get global configuration as {0} is {1}")
    @MethodSource
    fun getConfiguration(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/configuration") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)
    }

    @DisplayName("Get global configuration publicly")
    @Test
    fun getConfigurationPublic() = testApplication {
        val client = client()
        val response = client.get("/api/configuration/public")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @DisplayName("Update global configuration")
    @ParameterizedTest(name = "Update global configuration as {0} is {1}")
    @MethodSource
    fun updateConfiguration(
        user: TestUser,
        statusCode: HttpStatusCode,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.patch("/api/configuration") {
            contentType(ContentType.Application.Json)
            setBody(GlobalConfigurationUpdateDTO(enableRegistrations = true))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(statusCode, response.status)

        if (response.status == HttpStatusCode.OK) {
            val responseDto = Json.decodeFromString<GlobalConfigurationRepresentationDTO>(response.bodyAsText())
            assertEquals(true, responseDto.enableRegistrations)
        }
    }

    companion object {

        private lateinit var superadmin: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
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
            }
        }

        @JvmStatic
        fun getConfiguration(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), HttpStatusCode.Forbidden),
                Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
            )
        }

        @JvmStatic
        fun updateConfiguration(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Named.of("owner", superadmin), HttpStatusCode.OK),
                Arguments.of(Named.of("admin", admin), HttpStatusCode.OK),
                Arguments.of(Named.of("regular user", user), HttpStatusCode.Forbidden),
                Arguments.of(Named.of("guest", guest), HttpStatusCode.Unauthorized),
            )
        }
    }

}