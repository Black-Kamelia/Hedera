package com.kamelia.hedera.plugins

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import java.io.StringWriter

private lateinit var configuration: Configuration

fun Application.configureFreemarker() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        configuration = this
    }
}

fun FreeMarkerContent.toHTML(): String {
    val writer = StringWriter()
    configuration.getTemplate(template).process(model, writer)
    return writer.toString()
}
