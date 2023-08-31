package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.getUUID
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.personalTokensRoutes() = route("/personalTokens") {
    authenticate(AuthJwt) {
        createPersonalToken()
        getPersonalTokens()
        deletePersonalToken()
    }
}

private fun Route.createPersonalToken() = post<PersonalTokenCreationDTO> { body ->
    val userId = authenticatedUser!!.uuid

    call.respond(PersonalTokenService.createPersonalToken(userId, body))
}

private fun Route.getPersonalTokens() = get {
    val userId = authenticatedUser!!.uuid

    call.respond(PersonalTokenService.getPersonalTokens(userId))
}

private fun Route.deletePersonalToken() = delete("/{uuid}") {
    val userId = authenticatedUser!!.uuid
    val tokenId = call.getUUID("uuid")

    call.respond(PersonalTokenService.deletePersonalToken(userId, tokenId))
}
