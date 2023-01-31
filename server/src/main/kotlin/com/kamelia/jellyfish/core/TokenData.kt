package com.kamelia.jellyfish.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.util.Environment.secretAccess
import com.kamelia.jellyfish.util.Environment.secretRefresh
import java.util.Date
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
class TokenData(
    val accessToken: String,
    val accessTokenExpiration: Long,
    val refreshToken: String,
    // val refreshTokenExpiration: Long,
) {
    companion object {
        private const val DEFAULT_TOKEN_LIFETIME = 60L * 60L * 1000L // 1 hour
        private const val DEFAULT_REFRESH_TOKEN_LIFETIME = 60L * 60L * 24L * 30L * 1000L // 30 days

        fun from(user: User): TokenData {
            val now = System.currentTimeMillis()
            val accessToken = JWT.create()
                .withSubject(user.username)
                .withClaim("id", UUID.randomUUID().toString())
                .withExpiresAt(Date(now + DEFAULT_TOKEN_LIFETIME))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretAccess))

            val refreshToken = JWT.create()
                .withSubject(user.username)
                .withExpiresAt(Date(now + DEFAULT_REFRESH_TOKEN_LIFETIME))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretRefresh))

            return TokenData(
                accessToken,
                now + DEFAULT_TOKEN_LIFETIME,
                refreshToken,
                // now + DEFAULT_REFRESH_TOKEN_LIFETIME,
            )
        }
    }
}
