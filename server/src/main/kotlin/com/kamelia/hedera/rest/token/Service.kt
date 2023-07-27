package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.PersonalTokenNotFoundException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.user.User
import java.util.*

object PersonalTokenService {

    suspend fun createPersonalToken(
        ownerId: UUID,
        dto: PersonalTokenCreationDTO,
    ): Response<PersonalTokenDTO, MessageKeyDTO> = Connection.transaction {
        val owner = User.findById(ownerId) ?: throw UserNotFoundException()

        Response.created(
            PersonalTokens.create(
                name = dto.name,
                owner = owner
            ).toRepresentationDTO()
        )
    }

    suspend fun getPersonalTokens(
        ownerId: UUID,
    ): Response<List<PersonalTokenDTO>, String> = Connection.transaction {
        val tokens = PersonalTokens.findAllWithLastUsed(ownerId)
            .map { (token, lastUsed) -> token.toRepresentationDTO(lastUsed) }

        Response.ok(tokens)
    }

    suspend fun deletePersonalToken(
        userId: UUID,
        tokenId: UUID,
    ): Response<String, String> = Connection.transaction {
        val token = PersonalTokens.findById(tokenId) ?: throw PersonalTokenNotFoundException()
        val user = User.findById(userId) ?: throw UserNotFoundException()

        if (token.ownerId != userId) {
            throw IllegalActionException()
        }

        token.delete(user)
        Response.ok()
    }

}