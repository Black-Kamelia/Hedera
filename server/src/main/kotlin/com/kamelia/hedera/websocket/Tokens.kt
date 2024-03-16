package com.kamelia.hedera.websocket

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.kamelia.hedera.core.auth.UserState
import com.kamelia.hedera.util.Environment
import java.util.*

fun validateWebsocketToken(token: String): Pair<UUID, UUID>? {
    val secret = Environment.secretWSToken
    val verifier = JWT.require(Algorithm.HMAC256(secret)).build()
    val decoded = try {
        verifier.verify(token)
    } catch (e: JWTVerificationException) {
        return null
    }

    return try {
        val userId = UUID.fromString(decoded.subject)
        val sessionId = UUID.fromString(decoded.getClaim("sessionId").asString())
        Pair(userId, sessionId)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun createWebsocketToken(user: UserState, sessionId: UUID): String {
    val now = System.currentTimeMillis()
    val expiration = now + Environment.expirationWSToken
    val secret = Environment.secretWSToken

    return JWT.create()
        .withSubject(user.uuid.toString())
        .withClaim("sessionId", sessionId.toString())
        .withExpiresAt(Date(expiration))
        .withIssuedAt(Date(now))
        .sign(Algorithm.HMAC256(secret))
}
