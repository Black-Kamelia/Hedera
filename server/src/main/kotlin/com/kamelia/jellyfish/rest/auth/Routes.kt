package com.kamelia.jellyfish.rest.auth

import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.MissingTokenException
import com.kamelia.jellyfish.core.respond
import com.kamelia.jellyfish.util.accessToken
import com.kamelia.jellyfish.util.authenticatedUser
import com.kamelia.jellyfish.util.jwt
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
        logout()
    }
    authenticate("refresh-jwt") {
        refresh()
    }
}

private fun Route.login() = post<LoginDTO> { body ->
    call.respond(AuthService.login(body.username, body.password))
}

private fun Route.logoutAll() = delete("/all") {
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    call.respond(AuthService.logoutAll(userId))
}

private fun Route.logout() = delete {
    val token = accessToken ?: throw MissingTokenException()
    call.respond(AuthService.logout(token))
}

private fun Route.refresh() = patch {
    call.respond(AuthService.refresh(jwt))
}
