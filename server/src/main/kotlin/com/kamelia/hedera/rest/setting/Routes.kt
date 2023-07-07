package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.authenticatedUser
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.userSettingsRoutes() = route("/users/settings") {
    authenticate(AuthJwt) {
        getSettings()
        updateSettings()
    }
}

private fun Route.getSettings() = get {
    val uuid = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    call.respond(UserSettingsService.getUserSettings(uuid))
}

private fun Route.updateSettings() = patch<UserSettingsUpdateDTO> { body ->
    val uuid = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    call.respond(UserSettingsService.updateUserSettings(uuid, body))
}
