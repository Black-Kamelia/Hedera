package com.kamelia.hedera.util

import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.util.VariableDelimiter
import io.ktor.server.application.*
import java.util.*
import kotlin.io.path.toPath

val I18N = Translator
    .builder(Locale.ENGLISH)
    .addFile(Application::class.java.getResource("/locales")!!.toURI().toPath())
    .withConfiguration {
        interpolationDelimiter = VariableDelimiter.create("{", "}")
    }
    .build()
