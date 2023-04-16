package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.ErrorDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.rest.user.Users
import java.util.*

object AuthService {

    suspend fun login(username: String, password: String): Response<TokenData, ErrorDTO> {
        return SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, ErrorDTO> {
        return SessionManager.refresh(jwt)
    }

    fun logout(token: String): Response<Boolean, ErrorDTO> {
        SessionManager.logout(token)
        return Response.ok()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing, ErrorDTO> {
        val user = Users.findById(userId) ?: return Response.notFound()
        SessionManager.logoutAll(user)
        return Response.ok()
    }
}
