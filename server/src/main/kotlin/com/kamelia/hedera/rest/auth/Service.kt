package com.kamelia.hedera.rest.auth

import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.AccountDisabledException
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.InvalidCredentialsException
import com.kamelia.hedera.core.SESSION_ID_CLAIM
import com.kamelia.hedera.core.Session
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.setting.toRepresentationDTO
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.toRepresentationDTO
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.toUUID
import com.kamelia.hedera.util.uuid
import java.util.*
import kotlinx.coroutines.delay

object AuthService {

    suspend fun login(username: String, password: String): Response<SessionOpeningDTO> = Connection.transaction {
        val user = User.findByUsername(username)

        if (user == null) {
            delay(Environment.loginThrottle)
            throw InvalidCredentialsException()
        }
        if (!Hasher.verify(password, user.password).verified) {
            delay(Environment.loginThrottle)
            throw InvalidCredentialsException()
        }

        if (!user.enabled) {
            throw AccountDisabledException()
        }

        val session = SessionManager.createSession(user.uuid, user.toUserState())
        Response.created(
            SessionOpeningDTO(
                tokens = session,
                user = user.toRepresentationDTO(),
                userSettings = user.settings.toRepresentationDTO()
            )
        )
    }

    suspend fun refresh(refreshToken: String): Response<Session> = Connection.transaction {
        val session = SessionManager.refreshSession(refreshToken) ?: throw ExpiredOrInvalidTokenException()
        Response.ok(session)
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
