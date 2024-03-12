package com.kamelia.hedera.util

import io.ktor.server.config.*

lateinit var Environment: EnvironmentValues

class EnvironmentValues(
    config: ApplicationConfig,
) {

    private val envKind = config.property("ktor.environment").getString()
    val isDev = envKind == "dev"
    val isProd = envKind == "prod"

    val liquibaseMaster = config.property("liquibase.master").getString()

    val url = config.property("hedera.url").getString()
    val uploadFolder = config.property("hedera.uploadFolder").getString()
    val thumbnailFolder = config.property("hedera.thumbnailFolder").getString()
    val globalConfigurationFile = config.property("hedera.globalConfigurationFile").getString()
    val searchMaxDistance = config.property("hedera.searchMaxDistance").getString().toDouble()

    val loginThrottle = config.property("hedera.auth.loginThrottle").getString().toLong()
    val sessionPurgePeriod = config.property("hedera.auth.sessionPurgePeriod").getString().toLong()
    val maximumSessionsPerUser = config.property("hedera.auth.maximumSessionsPerUser").getString().toInt()

    val secretAccess = config.property("hedera.jwt.secretAccess").getString()
    val expirationAccess = config.property("hedera.jwt.expirationAccess").getString().toLong()
    val secretRefresh = config.property("hedera.jwt.secretRefresh").getString()
    val expirationRefresh = config.property("hedera.jwt.expirationRefresh").getString().toLong()
    val secretWSToken = config.property("hedera.jwt.secretWSToken").getString()
    val expirationWSToken = config.property("hedera.jwt.expirationWSToken").getString().toLong()
    val jwtRealm = config.property("hedera.jwt.realm").getString()

    val databaseHost = config.property("hedera.database.host").getString()
    val databasePort = config.property("hedera.database.port").getString().toShort()
    val databaseName = config.property("hedera.database.name").getString()
    val databaseUsername = config.property("hedera.database.username").getString()
    val databasePassword = config.property("hedera.database.password").getString()

    val mailHost = config.property("hedera.mail.host").getString()
    val mailPort = config.property("hedera.mail.port").getString().toInt()
    val mailUseTLS = config.property("hedera.mail.tls").getString()
    val mailUseAuth = config.property("hedera.mail.auth").getString()
    val mailUsername = config.property("hedera.mail.username").getString()
    val mailPassword = config.property("hedera.mail.password").getString()
    val mailFrom = config.property("hedera.mail.from").getString()
    val mailFromName = config.property("hedera.mail.fromName").getString()

    init {
        check(searchMaxDistance in 0.0..1.0) { "searchMaxDistance must be between 0.0 and 1.0" }
        check(loginThrottle >= 0) { "loginThrottle must be greater or equal to 0" }
        check(sessionPurgePeriod > 0) { "sessionPurgePeriod must be greater than 0" }
        check(maximumSessionsPerUser > 0) { "maximumSessionsPerUser must be greater than 0" }
        check(expirationAccess > 0) { "expirationAccess must be greater than 0" }
        check(expirationRefresh > 0) { "expirationRefresh must be greater than 0" }
        check(expirationWSToken > 0) { "expirationWSToken must be greater than 0" }
    }
}
