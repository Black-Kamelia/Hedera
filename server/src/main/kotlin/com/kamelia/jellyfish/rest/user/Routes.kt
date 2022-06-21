package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.respond
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.lang.AssertionError
import java.lang.IllegalArgumentException
import java.util.*


fun Route.userRoutes() = route("/users") {
    signup()
    deleteUser()
}

/*
get("/api/test") {
        val user = UserRepository.create(UserDTO(
            0, "bonsoir", "hi@ergerg.io", "eld", true, true
        )).result

        call.respond(user)
    }

    get("/api/all") {
        call.respond(UserRepository.getAll().result)
    }
 */

private fun Route.signup() = post {
    val user = call.receive<UserDTO>()
    call.respond(UserService.signup(user))
}

private fun Route.deleteUser() = delete("/{uuid}") {
    val uuid = call.parameters["uuid"] ?: throw AssertionError("Ktor is literally dying")

    val id = try {
        UUID.fromString(uuid)
    } catch (e: IllegalArgumentException) {
        call.respond(QueryResult.badRequest("UUID is not valid"))
        return@delete
    }

    call.respond(UserService.delete(id))
}
