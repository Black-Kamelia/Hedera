package com.kamelia.jellyfish.util

import io.ktor.http.ContentType
import io.ktor.server.application.Application

object Environment {

    lateinit var application: Application
    private val config get() = application.environment.config

    private val envKind get() = config.property("ktor.environment").getString()
    val isDev get() = envKind == "dev"
    val isProd get() = envKind != "dev"

    val liquibaseMaster get() = config.property("liquibase.master").getString()

    val uploadFolder get() = config.property("jellyfish.uploadFolder").getString()

    val secretAccess get() = config.property("jellyfish.jwt.secretAccess").getString()
    val expirationAccess get() = config.property("jellyfish.jwt.expirationAccess").getString().toLong()
    val secretRefresh get() = config.property("jellyfish.jwt.secretRefresh").getString()
    val expirationRefresh get() = config.property("jellyfish.jwt.expirationRefresh").getString().toLong()
    val jwtRealm get() = config.property("jellyfish.jwt.realm").getString()
}

val ApplicationJSON = ContentType.parse("application/json")
