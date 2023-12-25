package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.login
import com.kamelia.hedera.uuid
import io.ktor.server.testing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Global configuration tests")
class GlobalConfigurationTests {

    @DisplayName("Global configuration tests as owner")
    @Nested
    inner class OwnerGlobalConfigurationTests : AbstractGlobalConfigurationTests(
        owner,
        ownerExpectedResults,
    )

    @DisplayName("Global configuration tests as admin")
    @Nested
    inner class AdminGlobalConfigurationTests : AbstractGlobalConfigurationTests(
        admin,
        adminExpectedResults,
    )

    @DisplayName("Global configuration tests as regular user")
    @Nested
    inner class RegularUserGlobalConfigurationTests : AbstractGlobalConfigurationTests(
        user,
        regularUserExpectedResults,
    )

    @DisplayName("Global configuration tests as guest")
    @Nested
    inner class GuestGlobalConfigurationTests : AbstractGlobalConfigurationTests(
        guest,
        guestExpectedResults,
    )

    companion object {

        private lateinit var owner: TestUser
        private lateinit var admin: TestUser
        private lateinit var user: TestUser
        private val guest: TestUser = Pair(null, UUID(0, 0))

        init {
            initEnvironment()

            testApplication {
                owner = Pair(
                    login("global.configuration.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0005-0000-0001-000000000000".uuid()
                )
                admin = Pair(
                    login("global.configuration.admin", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0005-0000-0002-000000000000".uuid()
                )
                user = Pair(
                    login("global.configuration.user", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0005-0000-0003-000000000000".uuid()
                )
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            Path.of("_tests").toFile().deleteRecursively()
        }

        private fun initEnvironment() {
            val testFolderPath = Path.of("_tests")
            if (Files.exists(testFolderPath)) testFolderPath.toFile().deleteRecursively()
            val testFolder = Files.createDirectory(testFolderPath).toFile()

            val testConfiguration = File(
                GlobalConfigurationTests::class.java.getResource("/test_files/global_configuration.json")?.toURI()
                    ?: throw Exception("Missing resource file")
            )
            testConfiguration.copyTo(testFolder.resolve("global_configuration.json"), true)
        }

    }

}