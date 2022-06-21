package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.respond
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.userRoutes() = route("/users") {
    signup()
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
