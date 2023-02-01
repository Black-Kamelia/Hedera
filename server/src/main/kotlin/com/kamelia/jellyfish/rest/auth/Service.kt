package com.kamelia.jellyfish.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.Response
import com.kamelia.jellyfish.core.TokenData
import com.kamelia.jellyfish.rest.user.Users
import java.util.UUID

object AuthService {

    suspend fun login(username: String, password: String): Response<TokenData, List<ErrorDTO>> {
        return SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, List<ErrorDTO>> {
        return SessionManager.refresh(jwt)
    }

    fun logout(token: String): Response<Boolean, List<ErrorDTO>> {
        SessionManager.logout(token)
        return Response.ok()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing, List<ErrorDTO>> {
        val user = Users.findById(userId) ?: return Response.notFound()
        SessionManager.logoutAll(user)
        return Response.ok()
    }
}
