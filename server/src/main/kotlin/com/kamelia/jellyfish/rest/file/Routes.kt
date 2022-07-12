package com.kamelia.jellyfish.rest.file

import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.deleteOrCatch
import com.kamelia.jellyfish.core.patchOrCatch
import com.kamelia.jellyfish.core.postOrCatch
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.get
import com.kamelia.jellyfish.util.getUUID
import com.kamelia.jellyfish.util.jwt
import com.kamelia.jellyfish.util.respond
import com.kamelia.jellyfish.util.toUUIDOrNull
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.filesRoutes() = route("/files") {
    uploadFileFromToken()

    authenticate("auth-jwt") {
        uploadFile()
        editFile()
        deleteFile()
    }
}

private fun Route.uploadFile() = postOrCatch(path = "/upload") {
    val uuid = jwt["id"].asString().toUUIDOrNull()!!
    val user = Users.findById(uuid) ?: throw ExpiredOrInvalidTokenException()

    call.receiveMultipart().forEachPart { part ->
        if (part is PartData.FileItem && part.name == "file") {
            call.respond(FileService.handleFile(part, user))
        } else {
            part.dispose()
        }
    }
}

private fun Route.uploadFileFromToken() = postOrCatch(path = "/upload/token") {
    val authToken = call.request.headers["Upload-Token"]
        ?: return@postOrCatch call.respond(QueryResult.badRequest("errors.upload.no_token"))

    val user = Users.findByUploadToken(authToken) ?: throw ExpiredOrInvalidTokenException()

    call.receiveMultipart().forEachPart { part ->
        if (part is PartData.FileItem && part.name == "file") {
            call.respond(FileService.handleFile(part, user))
        } else {
            part.dispose()
        }
    }
}

private fun Route.editFile() = patchOrCatch<FileUpdateDTO>(path = "/{uuid}") { body ->
    val fileId = call.getUUID("uuid")
    val userId = jwt["id"].asString().toUUIDOrNull()!!

    call.respond(FileService.updateFile(fileId, userId, body))
}

private fun Route.deleteFile() = deleteOrCatch(path = "/{uuid}") {
    val fileId = call.getUUID("uuid")
    val userId = jwt["id"].asString().toUUIDOrNull()!!

    call.respond(FileService.deleteFile(fileId, userId))
}
