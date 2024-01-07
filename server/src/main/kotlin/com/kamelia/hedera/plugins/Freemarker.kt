package com.kamelia.hedera.plugins

import freemarker.cache.*
import freemarker.template.Configuration
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.velocity.*
import java.io.StringWriter
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader

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
