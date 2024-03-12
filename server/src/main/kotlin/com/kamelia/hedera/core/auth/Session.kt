package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.hedera.util.Environment
import java.util.*
import kotlinx.serialization.Serializable

const val USER_ID_CLAIM = "userId"
const val SESSION_ID_CLAIM = "sessionId"

@Serializable
data class TokenContainer(val token: String, val expiration: Long)

@Serializable
class Session(
    val accessToken: TokenContainer,
    val refreshToken: TokenContainer,
    var lastUsed: Long,
) {
    companion object {
        fun from(userId: UUID, sessionId: UUID): Session {
            val now = System.currentTimeMillis()

            val accessTokenExpiration = now + Environment.expirationAccess
            val accessToken = JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withClaim(USER_ID_CLAIM, userId.toString())
                .withClaim(SESSION_ID_CLAIM, sessionId.toString())
                .withExpiresAt(Date(accessTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(Environment.secretAccess))

            val refreshTokenExpiration = now + Environment.expirationRefresh
            val refreshToken = JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withClaim(USER_ID_CLAIM, userId.toString())
                .withClaim(SESSION_ID_CLAIM, sessionId.toString())
                .withExpiresAt(Date(refreshTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(Environment.secretRefresh))

            return Session(
                TokenContainer(accessToken, accessTokenExpiration),
                TokenContainer(refreshToken, refreshTokenExpiration),
                now
            )
        }
    }
}
