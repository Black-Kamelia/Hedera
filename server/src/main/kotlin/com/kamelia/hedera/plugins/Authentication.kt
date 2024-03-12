package com.kamelia.hedera.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.auth.UserState
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.getHeader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

const val AuthJwt = "auth-jwt"
const val RefreshJwt = "refresh-jwt"

fun Application.configureAuthentication() {
    install(Authentication) {
        configureJWT(AuthJwt, Environment.secretAccess) { call, _ ->
            val token = call.getHeader(HttpHeaders.Authorization).replace("Bearer ", "")
            val userState = SessionManager.verify(token) ?: throw ExpiredOrInvalidTokenException()
            handleForceChangePassword(userState, call)
            UserPrincipal(userState.uuid, userState, token)
        }
        configureJWT(RefreshJwt, Environment.secretRefresh) { call, cred ->
            val token = call.getHeader(HttpHeaders.Authorization).replace("Bearer ", "")
            SessionManager.verifyRefresh(token)
            JWTPrincipal(cred.payload)
        }
    }
}

private fun AuthenticationConfig.configureJWT(
    name: String,
    secret: String,
    block: suspend (ApplicationCall, JWTCredential) -> Principal,
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

data class UserPrincipal(
    val sessionId: UUID,
    val state: UserState,
    val accessToken: String
) : Principal
