package com.kamelia.hedera.util

import io.ktor.http.ContentType
import io.ktor.server.application.Application

object Environment {

    lateinit var application: Application
    private val config get() = application.environment.config

    private val envKind get() = config.property("ktor.environment").getString()
    val isDev get() = envKind == "dev"
    val isProd get() = envKind == "prod"

    val liquibaseMaster get() = config.property("liquibase.master").getString()

    val uploadFolder get() = config.property("hedera.uploadFolder").getString()
    val searchMaxDistance get() = config.property("hedera.searchMaxDistance").getString().toDouble()
    val loginThrottle get() = config.property("hedera.loginThrottle").getString().toLong()

    val secretAccess get() = config.property("hedera.jwt.secretAccess").getString()
    val expirationAccess get() = config.property("hedera.jwt.expirationAccess").getString().toLong()
    val secretRefresh get() = config.property("hedera.jwt.secretRefresh").getString()
    val expirationRefresh get() = config.property("hedera.jwt.expirationRefresh").getString().toLong()
    val secretWSToken get() = config.property("hedera.jwt.secretWSToken").getString()
    val expirationWSToken get() = config.property("hedera.jwt.expirationWSToken").getString().toLong()
    val jwtRealm get() = config.property("hedera.jwt.realm").getString()

    val databaseHost get() = config.property("hedera.database.host").getString()
    val databasePort get() = config.property("hedera.database.port").getString().toShort()
    val databaseName get() = config.property("hedera.database.name").getString()
    val databaseUsername get() = config.property("hedera.database.username").getString()
    val databasePassword get() = config.property("hedera.database.password").getString()
}

val ApplicationJSON = ContentType.parse("application/json")
