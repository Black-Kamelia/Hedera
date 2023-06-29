package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.Users
import java.util.*

object AuthService {

    suspend fun login(username: String, password: String): Response<TokenData, MessageDTO> = Connection.transaction {
        SessionManager.login(username, password)
    }

    suspend fun refresh(jwt: Payload): Response<TokenData, MessageDTO> = Connection.transaction {
        SessionManager.refresh(jwt)
    }

    suspend fun logout(token: String): Response<Boolean, MessageDTO> = Connection.transaction {
        SessionManager.logout(token)
        Response.ok()
    }

    suspend fun logoutAll(userId: UUID): Response<Nothing, MessageDTO> = Connection.transaction {
        val user = Users.findById(userId) ?: return@transaction Response.notFound()
        SessionManager.logoutAll(user)
        Response.ok()
    }
}
