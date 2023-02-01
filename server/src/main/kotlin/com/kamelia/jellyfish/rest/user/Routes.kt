package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.respond
import com.kamelia.jellyfish.util.adminRestrict
import com.kamelia.jellyfish.util.authenticatedUser
import com.kamelia.jellyfish.util.getPageParameters
import com.kamelia.jellyfish.util.getUUID
import com.kamelia.jellyfish.util.idRestrict
import com.kamelia.jellyfish.util.ifRegular
import com.kamelia.jellyfish.util.receivePageDefinition
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.userRoutes() = route("/users") {
    signup()

    authenticate("auth-jwt") {
        getUserById()
        getAllUsers()
        getPagedUsers()
        updateUser()
        updateUserPassword()
        deleteUser()
        regenerateUploadToken()
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

private fun Route.getPagedUsers() = get("/paged") {
    adminRestrict()
    val (page, pageSize) = call.getPageParameters()
    val definition = call.receivePageDefinition()

    call.respond(UserService.getUsers(page, pageSize, definition))
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

    call.respond(UserService.updateUserPassword(uuid, body, uuid))
}

private fun Route.deleteUser() = delete("/{uuid}") {
    val uuid = call.getUUID()
    ifRegular { idRestrict(uuid) }

    call.respond(UserService.deleteUser(uuid))
}

private fun Route.regenerateUploadToken() = post("/uploadToken") {
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    idRestrict(userId)

    call.respond(UserService.regenerateUploadToken(userId))
}
