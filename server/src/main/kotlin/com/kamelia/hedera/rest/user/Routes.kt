package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.adminRestrict
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.getPageParameters
import com.kamelia.hedera.util.getUUID
import com.kamelia.hedera.util.idRestrict
import com.kamelia.hedera.util.ifRegular
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.userRoutes() = route("/users") {
    signup()

    authenticate(AuthJwt) {
        getUserById()
        getAllUsers()
        searchUsers()
        updateUser()
        updateUserPassword()
        deleteUser()
        // regenerateUploadToken()
    }
}

private fun Route.signup() = post<UserDTO>("/signup") { body ->
    call.respond(UserService.signup(body))
}

private fun Route.getUserById() = get("/{uuid}") {
    val uuid = call.getUUID()
    ifRegular { idRestrict(uuid) }

    call.respond(UserService.getUserById(uuid))
}

private fun Route.getAllUsers() = get("/all") {
    adminRestrict()

    call.respond(UserService.getUsers())
}

private fun Route.searchUsers() = post<PageDefinitionDTO>("/search") { body ->
    adminRestrict()
    val (page, pageSize) = call.getPageParameters()

    call.respond(UserService.getUsers(page, pageSize, body))
}

private fun Route.updateUser() = patch<UserUpdateDTO>("/{uuid}") { body ->
    val uuid = call.getUUID()
    ifRegular {
        idRestrict(uuid)
    }
    val updaterID = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()

    call.respond(UserService.updateUser(uuid, body, updaterID))
}

private fun Route.updateUserPassword() = patch<UserPasswordUpdateDTO>("/{uuid}/password") { body ->
    val uuid = call.getUUID()
    idRestrict(uuid)

    call.respond(UserService.updateUserPassword(uuid, body))
}

private fun Route.deleteUser() = delete("/{uuid}") {
    val uuid = call.getUUID()
    ifRegular { idRestrict(uuid) }

    call.respond(UserService.deleteUser(uuid))
}

// private fun Route.regenerateUploadToken() = post("/uploadToken") {
//     val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
//     idRestrict(userId)
//
//     call.respond(UserService.regenerateUploadToken(userId))
// }
