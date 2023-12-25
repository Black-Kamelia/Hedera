package com.kamelia.hedera

import java.nio.file.Path
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CleanUp : AfterAllCallback {

    override fun afterAll(context: ExtensionContext?) {
        Path.of("_tests").toFile().deleteRecursively()
    }

}