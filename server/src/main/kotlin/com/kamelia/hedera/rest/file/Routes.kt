package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.filesRoutes() = route("/files") {
    uploadFileFromToken()

    authenticate(AuthJwt) {
        uploadFile()
        getPagedFiles()
        editFile()
        deleteFile()
    }

    authenticate(AuthJwt, optional = true) {
        getFile()
    }
}

private fun Route.uploadFile() = post("/upload") {
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFile(it, user)) }
    ), onMissing = {
        call.respond(Response.badRequest("errors.uploads.missing_file"))
    })
}

private fun Route.uploadFileFromToken() = post("/upload/token") {
    val authToken = call.getHeader("Upload-Token")
    val user = Users.findByUploadToken(authToken) ?: throw ExpiredOrInvalidTokenException()

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFile(it, user)) }
    ), onMissing = {
        call.respond(Response.badRequest("errors.uploads.missing_file"))
    })
}

private fun Route.getFile() = get("/{code}") {
    val user = authenticatedUser?.let { Users.findById(it.uuid) }
    val code = call.getParam("code")

    FileService.getFile(code, user).ifSuccessOrElse(
        onSuccess = { (data) ->
            checkNotNull(data) { "File not found" }
            val file = FileUtils.getOrNull(data.ownerId, code)
            if (file != null) {
                call.respondFile(file, data.name, data.mimeType)
            } else {
                // TODO notify orphaned file
                call.respond(Response.notFound())
            }
        },
        onError = {
            call.respond(Response.notFound())
        },
    )
}

private fun Route.getPagedFiles() = get("/paged/{uuid?}") {
    val uuid = call.getUUIDOrNull("uuid")
    val jwtId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()
    val userId = uuid?.apply { if (uuid != jwtId) adminRestrict() } ?: jwtId
    val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
    val (page, pageSize) = call.getPageParameters()
    val definition = call.receivePageDefinition()

    call.respond(FileService.getFiles(user, page, pageSize, definition, asOwner = uuid == null))
}

private fun Route.editFile() = patch<FileUpdateDTO>("/{uuid}") { body ->
    val fileId = call.getUUID("uuid")
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()

    call.respond(FileService.updateFile(fileId, userId, body))
}

private fun Route.deleteFile() = delete("/{uuid}") {
    val fileId = call.getUUID("uuid")
    val userId = authenticatedUser?.uuid ?: throw ExpiredOrInvalidTokenException()

    call.respond(FileService.deleteFile(fileId, userId))
}
