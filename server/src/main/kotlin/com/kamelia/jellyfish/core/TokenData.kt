package com.kamelia.jellyfish.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.util.Environment
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
        fun from(user: User): TokenData {
            val now = System.currentTimeMillis()

            val accessTokenExpiration = now + Environment.expirationAccess
            val accessToken = JWT.create()
                .withSubject(user.username)
                .withClaim("id", UUID.randomUUID().toString())
                .withExpiresAt(Date(accessTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretAccess))

            val refreshTokenExpiration = now + Environment.expirationRefresh
            val refreshToken = JWT.create()
                .withSubject(user.username)
                .withExpiresAt(Date(refreshTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretRefresh))

            return TokenData(
                accessToken,
                accessTokenExpiration,
                refreshToken,
                // refreshTokenExpiration,
            )
        }
    }
}
