package com.kamelia.jellyfish.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.Environment
import com.kamelia.jellyfish.util.Environment.isDev
import com.kamelia.jellyfish.util.Environment.isProd
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondRedirect

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = Environment.jwtRealm

            val jwtVerifier = JWT.require(Algorithm.HMAC256(Environment.secret)).build()
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
            
            challenge {_, _ ->
                when {
                    isDev -> call.respondRedirect("http://localhost:3000/login")
                    isProd -> call.respondRedirect("/login")
                }
            }
        }
    }
}