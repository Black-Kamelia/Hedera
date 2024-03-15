package com.kamelia.hedera.core.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.toUUID
import java.util.*
import kotlinx.serialization.Serializable

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
                .withSubject(userId.toString())
                .withClaim("tokenId", UUID.randomUUID().toString())
                .withClaim(SESSION_ID_CLAIM, sessionId.toString())
                .withExpiresAt(Date(accessTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(Environment.secretAccess))

            val refreshTokenExpiration = now + Environment.expirationRefresh
            val refreshToken = JWT.create()
                .withSubject(userId.toString())
                .withClaim("tokenId", UUID.randomUUID().toString())
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

val DecodedJWT.userId: UUID
    get() = subject.toString().toUUID()

val DecodedJWT.sessionId: UUID
    get() = getClaim(SESSION_ID_CLAIM).asString().toUUID()
