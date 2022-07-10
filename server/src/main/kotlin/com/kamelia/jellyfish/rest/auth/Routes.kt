package com.kamelia.jellyfish.rest.auth

import com.kamelia.jellyfish.core.deleteOrCatch
import com.kamelia.jellyfish.core.patchOrCatch
import com.kamelia.jellyfish.core.postOrCatch
import com.kamelia.jellyfish.util.jwt
import com.kamelia.jellyfish.util.respond
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
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

private fun Route.login() = postOrCatch<LoginDTO> { body ->
    call.respond(AuthService.verify(body.username, body.password))
}

private fun Route.logoutAll() = deleteOrCatch {
    val username = jwt.subject
    call.respond(AuthService.logoutAll(username))
}

private fun Route.refresh() = patchOrCatch {
    call.respond(AuthService.refresh(jwt))
}
