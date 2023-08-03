package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.User
import java.util.*

object AuthService {

    suspend fun login(username: String, password: String): Response<SessionOpeningDTO, MessageKeyDTO> = Connection.transaction {
        SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, MessageKeyDTO> = Connection.transaction {
        SessionManager.refresh(jwt)
    }

    suspend fun logout(token: String): Response<Boolean, MessageKeyDTO> = Connection.transaction {
        SessionManager.logout(token)
        Response.ok()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing, MessageKeyDTO> = Connection.transaction {
        val user = User[userId]
        SessionManager.logoutAll(user)
        Response.ok()
    }
}
