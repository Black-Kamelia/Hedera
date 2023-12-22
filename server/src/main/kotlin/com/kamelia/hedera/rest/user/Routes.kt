package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.adminRestrict
import com.kamelia.hedera.util.authToken
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.getPageParameters
import com.kamelia.hedera.util.getUUID
import com.kamelia.hedera.util.idRestrict
import com.kamelia.hedera.util.ifRegular
import com.kamelia.hedera.util.sessionId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.userRoutes() = route("/users") {
    signup()

    authenticate(AuthJwt) {
        createUser()
        getUserById()
        searchUsers()
        updateUser()
        activateUser()
        deactivateUser()
        updateUserPassword()
        deleteUser()
    }
}

private fun Route.signup() = post<UserCreationDTO>("/signup") { body ->
    call.respond(UserService.signup(body))
}

private fun Route.createUser() = post<UserCreationDTO> { body ->
    adminRestrict()
    val creatorID = call.authenticatedUser!!.uuid

    call.respond(UserService.createUser(body, creatorID))
}

private fun Route.getUserById() = get("/{uuid}") {
    val uuid = call.getUUID()
    ifRegular { idRestrict(uuid) }

    call.respond(UserService.getUserById(uuid))
}

private fun Route.searchUsers() = post<PageDefinitionDTO>("/search") { body ->
    adminRestrict()
    val (page, pageSize) = call.getPageParameters()

    call.respond(UserService.getUsers(page, pageSize, body))
}

private fun Route.updateUser() = patch<UserUpdateDTO>("/{uuid}") { body ->
    val uuid = call.getUUID()
    ifRegular { idRestrict(uuid) }
    val updaterID = call.authenticatedUser!!.uuid

    call.respond(UserService.updateUser(uuid, body, updaterID))
}

private fun Route.activateUser() = post("/{uuid}/activate") {
    val uuid = call.getUUID()
    val updaterID = call.authenticatedUser!!.uuid
    adminRestrict()

    call.respond(UserService.updateUserStatus(uuid, true, updaterID))
}

private fun Route.deactivateUser() = post("/{uuid}/deactivate") {
    val uuid = call.getUUID()
    val updaterID = call.authenticatedUser!!.uuid
    adminRestrict()

    call.respond(UserService.updateUserStatus(uuid, false, updaterID))
}

private fun Route.updateUserPassword() = patch<UserPasswordUpdateDTO>("/{uuid}/password") { body ->
    val uuid = call.getUUID()
    val forced = call.parameters["forced"].toBoolean()
    val sessionId = call.sessionId ?: throw ExpiredOrInvalidTokenException()
    idRestrict(uuid)

    call.respond(UserService.updateUserPassword(uuid, call.authToken, sessionId, body, forced))
}

private fun Route.deleteUser() = delete("/{uuid}") {
    val uuid = call.getUUID()
    val deleterId = call.authenticatedUser!!.uuid
    ifRegular { idRestrict(uuid) }

    call.respond(UserService.deleteUser(uuid, deleterId))
}
