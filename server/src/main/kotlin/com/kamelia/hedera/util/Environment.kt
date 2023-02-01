package com.kamelia.hedera.util

import io.ktor.http.ContentType
import io.ktor.server.application.Application

object Environment {

    lateinit var application: Application
    private val config get() = application.environment.config

    private val envKind get() = config.property("ktor.environment").getString()
    val isDev get() = envKind == "dev"
    val isProd get() = envKind != "dev"

    val liquibaseMaster get() = config.property("liquibase.master").getString()

    val uploadFolder get() = config.property("hedera.uploadFolder").getString()

    val secretAccess get() = config.property("hedera.jwt.secretAccess").getString()
    val expirationAccess get() = config.property("hedera.jwt.expirationAccess").getString().toLong()
    val secretRefresh get() = config.property("hedera.jwt.secretRefresh").getString()
    val expirationRefresh get() = config.property("hedera.jwt.expirationRefresh").getString().toLong()
    val jwtRealm get() = config.property("hedera.jwt.realm").getString()
}

val ApplicationJSON = ContentType.parse("application/json")