package com.kamelia.jellyfish.util

import io.ktor.http.ContentType
import io.ktor.server.application.Application

object Environment {
    lateinit var application: Application
    private val config get() = application.environment.config

    private val envKind get() = config.propertyOrNull("ktor.environment")?.getString()
    val isDev get() = envKind != null && envKind == "dev"
    val isProd get() = envKind != null && envKind != "dev"

    val liquibaseMaster get() = config.propertyOrNull("liquibase.master")?.getString()

    val uploadFolder get() = config.propertyOrNull("jellyfish.uploadFolder")?.getString() ?: "uploads"

    val secretAccess get() = config.property("jwt.secretAccess").getString()
    val secretRefresh get() = config.property("jwt.secretRefresh").getString()
    val jwtRealm get() = config.property("jwt.realm").getString()
}

val ApplicationJSON = ContentType.parse("application/json")
