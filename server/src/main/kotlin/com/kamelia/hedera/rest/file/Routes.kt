package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.respond
import com.kamelia.hedera.core.respondNoSuccess
import com.kamelia.hedera.core.respondNothing
import com.kamelia.hedera.plugins.AuthJwt
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.FileUtils
import com.kamelia.hedera.util.adminRestrict
import com.kamelia.hedera.util.authenticatedUser
import com.kamelia.hedera.util.doWithForm
import com.kamelia.hedera.util.getHeader
import com.kamelia.hedera.util.getPageParameters
import com.kamelia.hedera.util.getParam
import com.kamelia.hedera.util.getUUID
import com.kamelia.hedera.util.getUUIDOrNull
import com.kamelia.hedera.util.proxyRedirect
import com.kamelia.hedera.util.respondFile
import com.kamelia.hedera.util.respondFileInline
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*

fun Route.filesRoutes() = route("/files") {
    uploadFileFromToken()

    authenticate(AuthJwt) {
        uploadFile()
        searchFiles()
        getFilesFormats()
        // editFile()
        editFileVisibility()
        editFileName()
        editFileCustomLink()
        removeFileCustomLink()
        deleteFile()

        route("/bulk") {
            editFileVisibilityBulk()
            deleteBulk()
        }
    }

    authenticate(AuthJwt, optional = true) {
        getFile()
    }
}


fun Route.rawFileRoute() = get("""/m/(?<code>[a-zA-Z0-9]{10})""".toRegex()) {
    val authedId = call.authenticatedUser?.uuid
    val code = call.getParam("code")

    try {
        FileService.getFileFromCode(code, authedId).ifSuccess { (data) ->
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

fun Route.rawFileCustomLinkRoute() = get("""/c/(?<link>[a-z0-9\-]+)""".toRegex()) {
    val link = call.getParam("link")

    try {
        FileService.getFileFromCustomLink(link).ifSuccess { (data) ->
            checkNotNull(data) { "File not found" }
            val file = FileUtils.getOrNull(data.owner.id, data.code)
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
    val userId = call.authenticatedUser!!.uuid

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
    val authedId = call.authenticatedUser?.uuid
    val code = call.getParam("code")

    FileService.getFileFromCode(code, authedId).ifSuccessOrElse(
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
    val jwtId = call.authenticatedUser!!.uuid
    val userId = uuid?.apply { if (uuid != jwtId) adminRestrict() } ?: jwtId
    val (page, pageSize) = call.getPageParameters()

    call.respond(FileService.getFiles(userId, page, pageSize, body, asOwner = uuid == null))
}

private fun Route.getFilesFormats() = get("/formats") {
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.getFilesFormats(userId))
}

private fun Route.editFileVisibility() = put<FileUpdateDTO>("/{uuid}/visibility") { body ->
    val fileId = call.getUUID("uuid")
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.updateFileVisibility(fileId, userId, body))
}

private fun Route.editFileVisibilityBulk() = post<BulkUpdateDTO>("/visibility") { body ->
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.updateFilesVisibility(userId, body))
}

private fun Route.editFileName() = put<FileUpdateDTO>("/{uuid}/name") { body ->
    val fileId = call.getUUID("uuid")
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.updateFileName(fileId, userId, body))
}

private fun Route.editFileCustomLink() = put<FileUpdateDTO>("/{uuid}/custom-link") { body ->
    val fileId = call.getUUID("uuid")
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.updateCustomLink(fileId, userId, body))
}

private fun Route.removeFileCustomLink() = delete("/{uuid}/custom-link") {
    val fileId = call.getUUID("uuid")
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.removeCustomLink(fileId, userId))
}

private fun Route.deleteFile() = delete("/{uuid}") {
    val fileId = call.getUUID("uuid")
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.deleteFile(fileId, userId))
}

private fun Route.deleteBulk() = post<BulkDeleteDTO>("/delete") { body ->
    val userId = call.authenticatedUser!!.uuid

    call.respond(FileService.deleteFiles(body.ids, userId))
}
