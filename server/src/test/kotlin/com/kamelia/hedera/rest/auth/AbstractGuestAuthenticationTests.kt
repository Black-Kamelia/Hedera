package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.rest.configuration.DiskQuotaPolicy
import com.kamelia.hedera.rest.configuration.GlobalConfigurationService
import com.kamelia.hedera.rest.configuration.GlobalConfigurationUpdateDTO
import com.kamelia.hedera.rest.configuration.isolateGlobalConfiguration
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractGuestAuthenticationTests(
    user: TestUser,
    private val expectedResults: GuestAuthenticationExpectedResults,
    private val input: GuestAuthenticationTestsInput,
) : AbstractAuthenticationTests(user, expectedResults, input) {

    @DisplayName("Sign up")
    @Test
    fun signUp() = testApplication {
        isolateGlobalConfiguration {
            GlobalConfigurationService.updateConfiguration(
                GlobalConfigurationUpdateDTO(enableRegistrations = true)
            )

            val response = client().post("/api/users/signup") {
                contentType(ContentType.Application.Json)
                setBody(input.signUp)
            }
            assertEquals(expectedResults.signUp, response.status)

            if (response.status == HttpStatusCode.Created) {
                val responseDto = Json.decodeFromString<UserRepresentationDTO>(response.bodyAsText())
                assertEquals(input.signUp.username, responseDto.username)
                assertEquals(input.signUp.email, responseDto.email)
                assertEquals(UserRole.REGULAR, responseDto.role)
                when (GlobalConfigurationService.currentConfiguration.defaultDiskQuotaPolicy) {
                    DiskQuotaPolicy.UNLIMITED -> {
                        assertEquals(-1, responseDto.maximumDiskQuota)
                    }

                    DiskQuotaPolicy.LIMITED -> {
                        assertEquals(
                            GlobalConfigurationService.currentConfiguration.defaultDiskQuota,
                            responseDto.maximumDiskQuota
                        )
                    }
                }
            }
        }
    }
}
