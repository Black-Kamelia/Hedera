package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.MissingTokenException
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.response.respond
import com.kamelia.hedera.core.response.respondNoSuccess
import com.kamelia.hedera.core.response.respondNothing
import com.kamelia.hedera.mail.MailService
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.plugins.RefreshJwt
import com.kamelia.hedera.rest.user.PasswordResetService
import com.kamelia.hedera.util.accessToken
import com.kamelia.hedera.util.authToken
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.jwt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authRoutes() = route("/") {
    login()
    requestResetPasswordToken()
    checkResetPasswordToken()
    resetPassword()

    authenticate(AuthJwt) {
        logoutAll()
        logout()
    }
    authenticate(RefreshJwt) {
        refresh()
    }
}

private fun Route.requestResetPasswordToken() = post<ResetPasswordRequestDTO>("/request-reset-password") { body ->
    call.respondNoSuccess(PasswordResetService.requestResetPasswordToken(body))
}

private fun Route.checkResetPasswordToken() = post<CheckResetPasswordTokenDTO>("/check-reset-password-token") { body ->
    call.respond(PasswordResetService.checkResetPasswordToken(body))
}

private fun Route.resetPassword() = post<ResetPasswordDTO>("/reset-password") { body ->
    call.respondNoSuccess(PasswordResetService.resetPassword(body))
}

private fun Route.login() = post<LoginDTO>("/login") { body ->
    call.respond(AuthService.login(body.username, body.password))
}

private fun Route.logoutAll() = post("/logout/all") {
    val userId = call.authenticatedUser!!.uuid
    call.respondNoSuccess(AuthService.logoutAll(userId))
}

private fun Route.logout() = post("/logout") {
    val token = call.accessToken!!
    call.respond(AuthService.logout(token))
}

private fun Route.refresh() = post("/refresh") {
    call.respond(AuthService.refresh(call.jwt, call.authToken))
}
