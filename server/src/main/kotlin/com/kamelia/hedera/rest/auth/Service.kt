package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.User
import java.util.*

object AuthService {

    suspend fun login(username: String, password: String): Response<SessionOpeningDTO> = Connection.transaction {
        SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload, token: String): Response<TokenData> = Connection.transaction {
        SessionManager.refresh(jwt, token)
    }

    suspend fun logout(token: String): Response<Boolean> = Connection.transaction {
        SessionManager.logout(token)
        Response.noContent()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing> = Connection.transaction {
        val user = User[userId]
        SessionManager.logoutAll(user)
        Response.noContent()
    }
}
