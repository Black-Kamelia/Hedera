package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.response.respond
import com.kamelia.hedera.core.response.respondNothing
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.thumbnail.ThumbnailService
import com.kamelia.hedera.util.adminRestrict
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.globalConfigurationRoutes() = route("/configuration") {
    authenticate(AuthJwt) {
        getConfiguration()
        updateConfiguration()

        route("/maintenance") {
            getThumbnailCacheSize()
            clearThumbnailCache()
        }
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

private fun Route.getThumbnailCacheSize() = get("/thumbnail-cache-size") {
    adminRestrict()
    call.respond(ThumbnailService.getFolderSize())
}

private fun Route.clearThumbnailCache() = post("/clear-thumbnail-cache") {
    adminRestrict()
    ThumbnailService.clearFolder()
    call.respondNothing(Response.ok())
}
