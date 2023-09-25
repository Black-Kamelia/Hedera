package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.*
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.filesRoutes() = route("/files") {
    uploadFileFromToken()

    authenticate(AuthJwt) {
        getFile()
        uploadFile()
        searchFiles()
        getFilesFormats()
        // editFile()
        editFileVisibility()
        editFileName()
        deleteFile()
    }

    // authenticate(AuthJwt, optional = true) {
    //     getFile()
    // }
}


fun Route.rawFileRoute() = get("""/(?<code>\$[a-zA-Z0-9]{10})""".toRegex()) {
    val authedId = authenticatedUser?.uuid
    val code = call.getParam("code")

    try {
        FileService.getFile(code, authedId).ifSuccess { (data) ->
            checkNotNull(data) { "File not found" }
            val file = FileUtils.getOrNull(data.owner.id, code)
            if (file != null) {
                call.respondFileInline(file, ContentType.parse(data.mimeType))
            } else {
                // TODO notify orphaned file
                call.proxyRedirect("/")
            }
        }
    } catch (e: FileNotFoundException) {
        call.proxyRedirect("/")
    }
}

private fun Route.uploadFile() = post("/upload") {
    val userId = authenticatedUser!!.uuid

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFile(it, userId)) }
    ), onMissing = {
        call.respondNoSuccess(Response.badRequest(Errors.Uploads.MISSING_FILE))
    })
}

private fun Route.uploadFileFromToken() = post("/upload/token") {
    val authToken = call.getHeader("Upload-Token")

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFileWithToken(it, authToken)) }
    ), onMissing = {
        call.respondNoSuccess(Response.badRequest(Errors.Uploads.MISSING_FILE))
    })
}

private fun Route.getFile() = get("/{code}") {
    val authedId = authenticatedUser?.uuid
    val code = call.getParam("code")

    FileService.getFile(code, authedId).ifSuccessOrElse(
        onSuccess = { (data) ->
            checkNotNull(data) { "File not found" }
            val file = FileUtils.getOrNull(data.owner.id, code)
            if (file != null) {
                call.respondFile(file, data.name, data.mimeType)
            } else {
                // TODO notify orphaned file
                call.respondNothing(Response.notFound())
            }
        },
        onError = {
            call.respondNothing(Response.notFound())
        },
    )
}

private fun Route.searchFiles() = post<PageDefinitionDTO>("/search/{uuid?}") { body ->
    val uuid = call.getUUIDOrNull("uuid")
    val jwtId = authenticatedUser!!.uuid
    val userId = uuid?.apply { if (uuid != jwtId) adminRestrict() } ?: jwtId
    val (page, pageSize) = call.getPageParameters()

    call.respond(FileService.getFiles(userId, page, pageSize, body, asOwner = uuid == null))
}

private fun Route.getFilesFormats() = get("/formats") {
    val userId = authenticatedUser!!.uuid

    call.respond(FileService.getFilesFormats(userId))
}

private fun Route.editFileVisibility() = put<FileUpdateDTO>("/{uuid}/visibility") { body ->
    val fileId = call.getUUID("uuid")
    val userId = authenticatedUser!!.uuid

    call.respond(FileService.updateFileVisibility(fileId, userId, body))
}

private fun Route.editFileName() = put<FileUpdateDTO>("/{uuid}/name") { body ->
    val fileId = call.getUUID("uuid")
    val userId = authenticatedUser!!.uuid

    call.respond(FileService.updateFileName(fileId, userId, body))
}

private fun Route.deleteFile() = delete("/{uuid}") {
    val fileId = call.getUUID("uuid")
    val userId = authenticatedUser!!.uuid

    call.respond(FileService.deleteFile(fileId, userId))
}
