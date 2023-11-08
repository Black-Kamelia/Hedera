package com.kamelia.hedera

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class GlobalConfigurationSetup : BeforeAllCallback, AfterAllCallback {

    private lateinit var testFolder: File

    override fun beforeAll(context: ExtensionContext?) {
        println("CREATING TEST FOLDER")

        val testFolderPath = Path.of("_tests")
        if (Files.exists(testFolderPath)) testFolderPath.toFile().deleteRecursively()
        testFolder = Files.createDirectory(testFolderPath).toFile()

        val testConfiguration = File(
            GlobalConfigurationSetup::class.java.getResource("/test_files/global_configuration.json")?.toURI()
                ?: throw Exception("Missing resource file")
        )
        testConfiguration.copyTo(testFolder.resolve("global_configuration.json"), true)
    }

    override fun afterAll(context: ExtensionContext?) {
        println("DELETING TEST FOLDER")
        testFolder.deleteRecursively()
    }
}