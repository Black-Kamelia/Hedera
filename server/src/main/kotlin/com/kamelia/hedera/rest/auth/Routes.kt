package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.MissingTokenException
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.util.accessToken
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.jwt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authRoutes() = route("/") {
    login()

    authenticate("auth-jwt") {
        logoutAll()
        logout()
    }
    authenticate("refresh-jwt") {
        refresh()
    }
}

private fun Route.login() = post<LoginDTO>("/login") { body ->
    call.respond(AuthService.login(body.username, body.password))
}

private fun Route.logoutAll() = post("/logout/all") {
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    call.respond(AuthService.logoutAll(userId))
}

private fun Route.logout() = post("/logout") {
    val token = accessToken ?: throw MissingTokenException()
    call.respond(AuthService.logout(token))
}

private fun Route.refresh() = post("/refresh") {
    call.respond(AuthService.refresh(jwt))
}
