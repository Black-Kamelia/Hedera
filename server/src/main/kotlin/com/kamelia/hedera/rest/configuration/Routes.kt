package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.response.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.util.adminRestrict
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.globalConfigurationRoutes() = route("/configuration") {
    authenticate(AuthJwt) {
        getConfiguration()
        updateConfiguration()
    }
    getConfigurationPublic()
}

private fun Route.getConfiguration() = get {
    adminRestrict()
    call.respond(GlobalConfigurationService.getConfiguration())
}

private fun Route.getConfigurationPublic() = get("/public") {
    call.respond(GlobalConfigurationService.getConfigurationPublic())
}

private fun Route.updateConfiguration() = patch<GlobalConfigurationUpdateDTO> { body ->
    adminRestrict()
    call.respond(GlobalConfigurationService.updateConfiguration(body))
}
