package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.IllegalActionException
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
    ): Response<PersonalTokenDTO, Nothing> = Connection.transaction {
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
    ): Response<List<PersonalTokenDTO>, Nothing> = Connection.transaction {
        val owner = User.findById(ownerId) ?: throw UserNotFoundException()
        val tokens = owner.getPersonalTokens().map { it.toRepresentationDTO() }

        Response.ok(tokens)
    }

    suspend fun deletePersonalToken(
        ownerId: UUID,
        tokenId: UUID,
    ): Response<Nothing, Nothing> = Connection.transaction {
        val token = PersonalTokens.findById(tokenId) ?: throw PersonalTokenNotFoundException()

        if (token.ownerId != ownerId) {
            throw IllegalActionException()
        }

        token.delete()
        Response.ok()
    }

}