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

    val secretAccess get() = config.property("jellyfish.jwt.secretAccess").getString()
    val expirationAccess get() = config.propertyOrNull("jellyfish.jwt.expirationAccess")
        ?.getString()
        ?.toLong()
        ?: 3600000L // 1 hour
    val secretRefresh get() = config.property("jellyfish.jwt.secretRefresh").getString()
    val expirationRefresh get() = config.propertyOrNull("jellyfish.jwt.expirationRefresh")
        ?.getString()
        ?.toLong()
        ?: 2592000000L // 30 days
    val jwtRealm get() = config.property("jellyfish.jwt.realm").getString()
}

val ApplicationJSON = ContentType.parse("application/json")
