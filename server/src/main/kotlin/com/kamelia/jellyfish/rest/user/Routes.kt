package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.core.deleteOrCatch
import com.kamelia.jellyfish.core.patchOrCatch
import com.kamelia.jellyfish.core.postOrCatch
import com.kamelia.jellyfish.util.getUUID
import com.kamelia.jellyfish.util.respond
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.route


fun Route.userRoutes() = route("/users") {
    signup()
    updateUser()
    updateUserPassword()
    deleteUser()
}

private fun Route.signup() = postOrCatch<UserDTO> { body ->
    call.respond(UserService.signup(body))
}

private fun Route.updateUser() = patchOrCatch<UserUpdateDTO>(path = "/{uuid}") { body ->
    val uuid = call.getUUID()
    call.respond(UserService.updateUser(uuid, body))
}

private fun Route.updateUserPassword() = patchOrCatch<UserPasswordUpdateDTO>(path = "/{uuid}/password") { body ->
    val uuid = call.getUUID()
    call.respond(UserService.updateUserPassword(uuid, body))
}

private fun Route.deleteUser() = deleteOrCatch(path = "/{uuid}") {
    val uuid = call.getUUID()
    call.respond(UserService.deleteUser(uuid))
}
