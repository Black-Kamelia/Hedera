package com.kamelia.jellyfish.rest.file

import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.QueryResult
import com.kamelia.jellyfish.core.respond
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.FileUtils
import com.kamelia.jellyfish.util.adminRestrict
import com.kamelia.jellyfish.util.doWithForm
import com.kamelia.jellyfish.util.getHeader
import com.kamelia.jellyfish.util.getPageParameters
import com.kamelia.jellyfish.util.getParam
import com.kamelia.jellyfish.util.getUUID
import com.kamelia.jellyfish.util.getUUIDOrNull
import com.kamelia.jellyfish.util.jwt
import com.kamelia.jellyfish.util.jwtOrNull
import com.kamelia.jellyfish.util.receivePageDefinition
import com.kamelia.jellyfish.util.respondFile
import com.kamelia.jellyfish.util.uuid
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.filesRoutes() = route("/files") {
    uploadFileFromToken()

    authenticate("auth-jwt") {
        uploadFile()
        getPagedFiles()
        editFile()
        deleteFile()
    }

    authenticate("auth-jwt", optional = true) {
        getFile()
    }
}

private fun Route.uploadFile() = post("/upload") {
    val uuid = jwt.uuid
    val user = Users.findById(uuid) ?: throw ExpiredOrInvalidTokenException()

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFile(it, user)) }
    ))
}

private fun Route.uploadFileFromToken() = post("/upload/token") {
    val authToken = call.getHeader("Upload-Token")
    val user = Users.findByUploadToken(authToken) ?: throw ExpiredOrInvalidTokenException()

    call.doWithForm(onFiles = mapOf(
        "file" to { call.respond(FileService.handleFile(it, user)) }
    ))
}

private fun Route.getFile() = get("/{code}") {
    val user = jwtOrNull()?.uuid?.let { Users.findById(it) }
    val code = call.getParam("code")

    FileService.getFile(code, user).ifSuccessOrElse(
        onSuccess = { (data) ->
            val file = FileUtils.getOrNull(data!!.ownerId, code)
            if (file != null) {
                call.respondFile(file, data.name)
            } else {
                FileService.deleteFileByCodeAsAdmin(code) // remove orphaned file
                call.respond(QueryResult.notFound())
            }
        },
        onError = {
            call.respond(QueryResult.notFound())
        },
    )
}

private fun Route.getPagedFiles() = get("/paged/{uuid?}") {
    val uuid = call.getUUIDOrNull("uuid")
    val jwtId = jwt.uuid
    val userId = uuid?.apply { if (uuid != jwtId) adminRestrict() } ?: jwtId
    val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
    val (page, pageSize) = call.getPageParameters()
    val definition = call.receivePageDefinition()

    call.respond(FileService.getFiles(user, page, pageSize, definition))
}

private fun Route.editFile() = patch<FileUpdateDTO>("/{uuid}") { body ->
    val fileId = call.getUUID("uuid")
    val userId = jwt.uuid

    call.respond(FileService.updateFile(fileId, userId, body))
}

private fun Route.deleteFile() = delete("/{uuid}") {
    val fileId = call.getUUID("uuid")
    val userId = jwt.uuid

    call.respond(FileService.deleteFile(fileId, userId))
}
