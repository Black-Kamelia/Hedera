package com.kamelia.jellyfish.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.core.TokenPair
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.ErrorDTO
import com.kamelia.jellyfish.util.QueryResult

object AuthService {

    suspend fun verify(username: String, password: String): QueryResult<TokenPair, List<ErrorDTO>> {
        val unauthorized = QueryResult.unauthorized("errors.auth.verify.unauthorized")
        val user = Users.findByUsername(username)
            ?: return unauthorized

        val isCorrect = Hasher.verify(password, user.password).verified
        if (!isCorrect) {
            return unauthorized
        }

        val tokens = TokenPair.from(user)
        return QueryResult.ok(tokens)
    }

    suspend fun logoutAll(username: String): QueryResult<Nothing, List<ErrorDTO>> {
        val user = Users.findByUsername(username)
            ?: return QueryResult.notFound()

        Users.logoutAll(user)
        return QueryResult.ok()
    }

    suspend fun refresh(jwt: Payload): QueryResult<TokenPair, List<ErrorDTO>> {
        val user = Users.findByUsername(jwt.subject)
            ?: return QueryResult.unauthorized("errors.auth.refresh.unauthorized")

        val tokens = TokenPair.from(user)
        return QueryResult.ok(tokens)
    }
}
