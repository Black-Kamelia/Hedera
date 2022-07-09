package com.kamelia.jellyfish.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.util.Environment.secret
import com.kamelia.jellyfish.util.Environment.secretRefresh
import java.util.Date
import kotlinx.serialization.Serializable

@Serializable
class TokenPair(val token: String, val refreshToken: String) {
    companion object {
        private const val DEFAULT_TOKEN_LIFETIME = 60L * 60L * 1000L // 1 hour
        private const val DEFAULT_REFRESH_TOKEN_LIFETIME = 60L* 60L * 24L * 30L * 1000L // 30 days

        fun from(user: User): TokenPair {
            val now = System.currentTimeMillis()
            val token = JWT.create()
                .withSubject(user.username)
                .withClaim("id", user.id.value.toString())
                .withClaim("username", user.username)
                .withClaim("email", user.email)
                .withClaim("role", user.role.name)
                .withExpiresAt(Date(now + DEFAULT_TOKEN_LIFETIME))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secret))

            val refreshToken = JWT.create()
                .withSubject(user.username)
                .withExpiresAt(Date(now + DEFAULT_REFRESH_TOKEN_LIFETIME))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretRefresh))

            return TokenPair(token, refreshToken)
        }
    }
}
