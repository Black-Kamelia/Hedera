package com.kamelia.hedera.rest.token

import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.getUUID
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.personalTokensRoutes() = route("/personalTokens") {
    authenticate(AuthJwt) {
        createPersonalToken()
        getPersonalTokens()
        deletePersonalToken()
    }
}

private fun Route.createPersonalToken() = post<PersonalTokenCreationDTO> { body ->
    val uuid = authenticatedUser!!.uuid
    call.respond(PersonalTokenService.createPersonalToken(uuid, body))
}

private fun Route.getPersonalTokens() = get {
    val uuid = authenticatedUser!!.uuid
    call.respond(PersonalTokenService.getPersonalTokens(uuid))
}

private fun Route.deletePersonalToken() = delete("/{uuid}") {
    val uuid = authenticatedUser!!.uuid
    val tokenId = call.getUUID("uuid")
    call.respond(PersonalTokenService.deletePersonalToken(uuid, tokenId))
}