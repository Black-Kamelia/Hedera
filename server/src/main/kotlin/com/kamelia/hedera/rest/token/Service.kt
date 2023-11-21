package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.ActionResponse
import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.PersonalTokenNotFoundException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.asMessage
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.User
import java.util.*

object PersonalTokenService {

    suspend fun createPersonalToken(
        userId: UUID,
        dto: PersonalTokenCreationDTO,
    ): ActionResponse<PersonalTokenDTO> = Connection.transaction {
        val owner = User[userId]

        val token = PersonalToken.create(
            name = dto.name,
            owner = owner
        )
        ActionResponse.created(
            title = Actions.Tokens.Create.Success.TITLE.asMessage(),
            message = Actions.Tokens.Create.Success.MESSAGE.asMessage("name" to token.name),
            payload = token.toRepresentationDTO(token = token.token)
        )
    }

    suspend fun getPersonalTokens(
        userId: UUID,
    ): Response<List<PersonalTokenDTO>> = Connection.transaction {
        val tokens = PersonalToken.allWithLastUsed(userId)
            .map { (token, lastUsed) -> token.toRepresentationDTO(lastUsed = lastUsed) }

        Response.ok(tokens)
    }

    suspend fun deletePersonalToken(
        userId: UUID,
        tokenId: UUID,
    ): ActionResponse<Nothing> = Connection.transaction {
        val token = PersonalToken.findById(tokenId) ?: throw PersonalTokenNotFoundException()

        if (token.deleted) throw PersonalTokenNotFoundException()
        if (token.ownerId != userId) throw IllegalActionException()

        token.delete()
        ActionResponse.ok(
            title = Actions.Tokens.Delete.Success.TITLE.asMessage(),
            message = Actions.Tokens.Delete.Success.MESSAGE.asMessage("name" to token.name)
        )
    }

}