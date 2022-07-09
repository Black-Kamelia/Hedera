package com.kamelia.jellyfish.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.Environment
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    install(Authentication) {
        configureJWT("auth-jwt", Environment.secret)
        configureJWT("refresh-jwt", Environment.secretRefresh)
    }
}

private fun AuthenticationConfig.configureJWT(name: String, secret: String) = jwt(name) {
    realm = Environment.jwtRealm

    val jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build()
    verifier(jwtVerifier)

    validate {credential ->
        val subject = credential.subject ?: return@validate null
        val user = Users.findByUsername(subject) ?: return@validate null
        val lastInvalidation = user.lastInvalidation
        if (lastInvalidation != null && lastInvalidation.isAfter(credential.issuedAt!!.toInstant())) {
            return@validate null
        }
        JWTPrincipal(credential.payload)
    }
}
