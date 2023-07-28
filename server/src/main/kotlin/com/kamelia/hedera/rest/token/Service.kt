package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.PersonalTokenNotFoundException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.User
import java.util.*

object PersonalTokenService {

    suspend fun createPersonalToken(
        userId: UUID,
        dto: PersonalTokenCreationDTO,
    ): Response<MessageDTO<PersonalTokenDTO>, MessageKeyDTO> = Connection.transaction {
        val owner = User.findById(userId) ?: throw UserNotFoundException()

        val token = PersonalTokens.create(
            name = dto.name,
            owner = owner
        )
        Response.created(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Tokens.Create.Success.TITLE),
                message = MessageKeyDTO.of(Actions.Tokens.Create.Success.MESSAGE, "name" to token.name),
                token.toRepresentationDTO(token = token.token)
            )
        )
    }

    suspend fun getPersonalTokens(
        userId: UUID,
    ): Response<List<PersonalTokenDTO>, String> = Connection.transaction {
        val tokens = PersonalTokens.findAllWithLastUsed(userId)
            .map { (token, lastUsed) -> token.toRepresentationDTO(lastUsed = lastUsed) }

        Response.ok(tokens)
    }

    suspend fun deletePersonalToken(
        userId: UUID,
        tokenId: UUID,
    ): Response<MessageDTO<Nothing>, String> = Connection.transaction {
        val token = PersonalTokens.findById(tokenId) ?: throw PersonalTokenNotFoundException()
        val user = User.findById(userId) ?: throw UserNotFoundException()

        if (token.deleted) throw PersonalTokenNotFoundException()
        if (token.ownerId != userId) throw IllegalActionException()

        token.delete(user)
        Response.ok(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Tokens.Delete.Success.TITLE),
                message = MessageKeyDTO.of(Actions.Tokens.Delete.Success.MESSAGE, "name" to token.name)
            )
        )
    }

}