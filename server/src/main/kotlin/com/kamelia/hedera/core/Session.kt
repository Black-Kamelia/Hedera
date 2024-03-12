package com.kamelia.hedera.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.Environment.secretAccess
import com.kamelia.hedera.util.Environment.secretRefresh
import com.kamelia.hedera.util.uuid
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
) {
    companion object {
        fun from(userId: UUID, sessionId: UUID): Session {
            val now = System.currentTimeMillis()

            val accessTokenExpiration = now + Environment.expirationAccess
            val accessToken = JWT.create()
                .withClaim(USER_ID_CLAIM, userId.toString())
                .withClaim(SESSION_ID_CLAIM, sessionId.toString())
                .withExpiresAt(Date(accessTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretAccess))

            val refreshTokenExpiration = now + Environment.expirationRefresh
            val refreshToken = JWT.create()
                .withClaim(USER_ID_CLAIM, userId.toString())
                .withClaim(SESSION_ID_CLAIM, sessionId.toString())
                .withExpiresAt(Date(refreshTokenExpiration))
                .withIssuedAt(Date(now))
                .sign(Algorithm.HMAC256(secretRefresh))

            return Session(
                TokenContainer(accessToken, accessTokenExpiration),
                TokenContainer(refreshToken, refreshTokenExpiration),
            )
        }
    }
}
