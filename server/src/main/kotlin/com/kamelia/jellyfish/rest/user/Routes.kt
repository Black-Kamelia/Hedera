package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.respond
import com.kamelia.jellyfish.util.toUUIDOrNull
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.userRoutes() = route("/users") {
    signup()
    updateUser()
    updateUserPassword()
    deleteUser()
}

private fun Route.signup() = post {
    val user = call.receive<UserDTO>()
    call.respond(UserService.signup(user))
}

private fun Route.updateUser() = patch("/{uuid}") {
    val id = call.parameters["uuid"]!!
    val update = call.receive<UserUpdateDTO>()
    val uuid = id.toUUIDOrNull()
        ?: return@patch call.respond(QueryResult.badRequest("UUID is not valid"))

    call.respond(UserService.updateUser(uuid, update))
}

private fun Route.updateUserPassword() = patch("/{uuid}/password") {
    val id = call.parameters["uuid"]!!
    val passwords = call.receive<UserPasswordUpdateDTO>()
    val uuid = id.toUUIDOrNull()
        ?: return@patch call.respond(QueryResult.badRequest("UUID is not valid"))

    call.respond(UserService.updateUserPassword(uuid, passwords))
}

private fun Route.deleteUser() = delete("/{uuid}") {
    val id = call.parameters["uuid"]!!
    val uuid = id.toUUIDOrNull()
        ?: return@delete call.respond(QueryResult.badRequest("UUID is not valid"))

    call.respond(UserService.deleteUser(uuid))
}
