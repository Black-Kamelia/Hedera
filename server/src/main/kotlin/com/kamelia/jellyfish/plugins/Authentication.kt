package com.kamelia.jellyfish.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.rest.auth.SessionManager
import com.kamelia.jellyfish.util.Environment
import com.kamelia.jellyfish.util.getHeader
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.Principal
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    install(Authentication) {
        configureJWT("auth-jwt", Environment.secretAccess) { call, _ ->
            val token = call.getHeader("Authorization").replace("Bearer ", "")
            val user = SessionManager.verify(token)
            requireNotNull(user)
            user
        }
        configureJWT("refresh-jwt", Environment.secretRefresh) { _, cred ->
            JWTPrincipal(cred.payload)
        }
    }
}

private fun AuthenticationConfig.configureJWT(
    name: String,
    secret: String,
    block: (ApplicationCall, JWTCredential) -> Principal,
) = jwt(name) {
    realm = Environment.jwtRealm

    val jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build()
    verifier(jwtVerifier)

    validate { credential ->
        runCatching {
            block(this, credential)
        }.onFailure {
            if (it !is IllegalArgumentException) throw it
        }.getOrNull()
    }

    challenge { _, _ -> throw ExpiredOrInvalidTokenException() }
}
