package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.stream.Stream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractGlobalConfigurationTests(
    private val user: TestUser,
    private val expectedResults: GlobalConfigurationExpectedResults,
) {

    @DisplayName("Get global configuration")
    @Test
    fun getConfiguration() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/configuration") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.getConfiguration, response.status)
    }

    @DisplayName("Get public global configuration")
    @Test
    fun getPublicConfiguration() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/configuration/public") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.getPublicConfiguration, response.status)
    }

    @DisplayName("Update global configuration")
    @ParameterizedTest(name = "Update ''{0}'' setting")
    @MethodSource
    fun updateConfigurationSetting(
        newConfiguration: GlobalConfigurationUpdateDTO,
        dtoCheck: (GlobalConfigurationRepresentationDTO) -> Boolean,
    ) = testApplication {
        val (tokens, _) = user
        val client = client()

        val getResponse = client.get("/api/configuration") {
            tokens?.let { bearerAuth(it.accessToken) }
        }

        isolateGlobalConfiguration {
            if (getResponse.status == HttpStatusCode.OK) {
                val responseDto = Json.decodeFromString<GlobalConfigurationRepresentationDTO>(getResponse.bodyAsText())
                assertFalse(dtoCheck(responseDto))
            }

            val response = client.patch("/api/configuration") {
                contentType(ContentType.Application.Json)
                setBody(newConfiguration)
                tokens?.let { bearerAuth(it.accessToken) }
            }

            assertEquals(expectedResults.updateConfiguration, response.status)

            if (response.status == HttpStatusCode.OK) {
                val responseDto = Json.decodeFromString<GlobalConfigurationRepresentationDTO>(response.bodyAsText())
                assertTrue(dtoCheck(responseDto))
            }
        }
    }

    @DisplayName("Get thumbnail cache size")
    @Test
    fun getThumbnailCacheSize() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.get("/api/configuration/maintenance/thumbnail-cache-size") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.getThumbnailCacheSize, response.status)
    }

    @DisplayName("Clear thumbnail cache size")
    @Test
    fun clearThumbnailCacheSize() = testApplication {
        val (tokens, _) = user
        val client = client()
        val response = client.post("/api/configuration/maintenance/clear-thumbnail-cache") {
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(expectedResults.clearThumbnailCacheSize, response.status)
    }

    companion object {

        @JvmStatic
        fun updateConfigurationSetting(): Stream<Arguments> = listOf(
            Arguments.of(
                Named.of(
                    "Enable registrations",
                    GlobalConfigurationUpdateDTO(enableRegistrations = true)
                ),
                { dto: GlobalConfigurationRepresentationDTO -> dto.enableRegistrations }
            ),
            Arguments.of(
                Named.of(
                    "Default disk quota policy",
                    GlobalConfigurationUpdateDTO(defaultDiskQuotaPolicy = DiskQuotaPolicy.UNLIMITED)
                ),
                { dto: GlobalConfigurationRepresentationDTO -> dto.defaultDiskQuotaPolicy == DiskQuotaPolicy.UNLIMITED }
            ),
            Arguments.of(
                Named.of(
                    "Default disk quota",
                    GlobalConfigurationUpdateDTO(
                        defaultDiskQuotaPolicy = DiskQuotaPolicy.LIMITED,
                        defaultDiskQuota = 1000L,
                    )
                ),
                { dto: GlobalConfigurationRepresentationDTO -> dto.defaultDiskQuota == 1000L }
            ),
        ).stream()
    }
}
