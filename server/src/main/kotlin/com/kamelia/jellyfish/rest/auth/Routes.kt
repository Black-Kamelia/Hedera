package com.kamelia.jellyfish.rest.auth

import com.kamelia.jellyfish.core.respond
import com.kamelia.jellyfish.util.authenticatedUser
import com.kamelia.jellyfish.util.jwt
import com.kamelia.jellyfish.util.uuid
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() = route("/login") {
    login()

    authenticate("auth-jwt") {
        logoutAll()
    }
    authenticate("refresh-jwt") {
        refresh()
    }
}

private fun Route.login() = post<LoginDTO> { body ->
    call.respond(AuthService.login(body.username, body.password))
}

private fun Route.logoutAll() = delete {
    call.respond(AuthService.logoutAll(authenticatedUser.uuid))
}

private fun Route.refresh() = patch {
    call.respond(AuthService.refresh(jwt))
}
