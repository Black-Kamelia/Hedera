package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.rest.auth.UserState
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

private const val PASSWORD_CHANGE_PATH = "/api/users/password"
private const val PASSWORD_CHANGE_METHOD = "PATCH"

private val WHITELISTED_ROUTES = listOf(
    PASSWORD_CHANGE_PATH,
    "/api/ws",
)

suspend fun handleForceChangePassword(user: UserState, call: ApplicationCall) {
    val path = call.request.path()
    if (WHITELISTED_ROUTES.contains(path)) return

    val method = call.request.httpMethod.value
    if (user.forceChangePassword && path != PASSWORD_CHANGE_PATH && method != PASSWORD_CHANGE_METHOD) {
        call.respond(Response.error(HttpStatusCode.Conflict, MessageKeyDTO(Errors.Users.FORCE_CHANGE_PASSWORD)))
    }
}
