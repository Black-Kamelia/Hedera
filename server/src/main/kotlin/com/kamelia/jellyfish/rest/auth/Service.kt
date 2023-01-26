package com.kamelia.jellyfish.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.core.QueryResult
import com.kamelia.jellyfish.core.TokenPair
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.Users
import java.util.UUID

object AuthService {

    suspend fun login(username: String, password: String): QueryResult<TokenPair, List<ErrorDTO>> {
        return SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): QueryResult<TokenPair, List<ErrorDTO>> {
        return SessionManager.refresh(jwt)
    }

    fun logout(token: String): QueryResult<Boolean, List<ErrorDTO>> {
        SessionManager.logout(token) ?: throw ExpiredOrInvalidTokenException()
        return QueryResult.ok()
    }

    suspend fun logoutAll(userId: UUID): QueryResult<Nothing, List<ErrorDTO>> {
        val user = Users.findById(userId) ?: return QueryResult.notFound()
        SessionManager.logoutAll(user)
        return QueryResult.ok()
    }
}

object SessionManager {

    private val sessions = mutableMapOf<String, User>()
    private val refreshToAccess = mutableMapOf<String, String>()

    private fun generateTokens(user: User): TokenPair {
        val tokens = TokenPair.from(user)
        synchronized(sessions) {
            sessions[tokens.token] = user
            refreshToAccess[tokens.refreshToken] = tokens.token
        }
        return tokens
    }

    suspend fun login(username: String, password: String): QueryResult<TokenPair, List<ErrorDTO>> {
        val unauthorized = QueryResult.unauthorized("errors.auth.verify.unauthorized")
        val user = Users.findByUsername(username) ?: return unauthorized

        val isCorrect = Hasher.verify(password, user.password).verified
        if (!isCorrect) {
            return unauthorized
        }

        return QueryResult.ok(generateTokens(user))
    }

    suspend fun refresh(jwt: Payload): QueryResult<TokenPair, List<ErrorDTO>> {
        val user = Users.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        refreshToAccess[jwt.id]?.let { sessions.remove(it) }
        return QueryResult.ok(generateTokens(user))
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
