package com.kamelia.jellyfish.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.core.Response
import com.kamelia.jellyfish.core.TokenPair
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.Users
import java.util.UUID

object AuthService {

    suspend fun login(username: String, password: String): Response<TokenPair, List<ErrorDTO>> {
        return SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): Response<TokenPair, List<ErrorDTO>> {
        return SessionManager.refresh(jwt)
    }

    fun logout(token: String): Response<Boolean, List<ErrorDTO>> {
        SessionManager.logout(token) ?: throw ExpiredOrInvalidTokenException()
        return Response.ok()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing, List<ErrorDTO>> {
        val user = Users.findById(userId) ?: return Response.notFound()
        SessionManager.logoutAll(user)
        return Response.ok()
    }
}

object SessionManager {

    private val lock = Any()
    private val sessions = mutableMapOf<String, User>()
    private val refreshToAccess = mutableMapOf<String, String>()

    private fun generateTokens(user: User): TokenPair {
        val tokens = TokenPair.from(user)
        synchronized(lock) {
            sessions[tokens.token] = user
            refreshToAccess[tokens.refreshToken] = tokens.token
        }
        return tokens
    }

    suspend fun login(username: String, password: String): Response<TokenPair, List<ErrorDTO>> {
        val unauthorized = Response.unauthorized("errors.auth.verify.unauthorized")
        val user = Users.findByUsername(username) ?: return unauthorized

        val isCorrect = Hasher.verify(password, user.password).verified
        if (!isCorrect) {
            return unauthorized
        }

        return Response.ok(generateTokens(user))
    }

    suspend fun refresh(jwt: Payload): Response<TokenPair, List<ErrorDTO>> {
        val user = Users.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        synchronized(sessions) {
            refreshToAccess[jwt.id]?.let { sessions.remove(it) }
            return Response.ok(generateTokens(user))
        }
    }

    fun verify(token: String): User? = synchronized(sessions) {
        return sessions[token]
    }

    fun logout(token: String): User? = synchronized(sessions) {
        return sessions.remove(token)
    }

    fun logoutAll(user: User): Unit = synchronized(sessions) {
        sessions.entries.removeIf { it.value.id == user.id }
    }

}
