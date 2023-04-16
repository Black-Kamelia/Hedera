package com.kamelia.hedera.util

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidUUIDException
import com.kamelia.hedera.core.MissingHeaderException
import com.kamelia.hedera.core.MissingParameterException
import com.kamelia.hedera.core.MultipartParseException
import com.kamelia.hedera.plugins.UserPrincipal
import com.kamelia.hedera.rest.auth.UserState
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.content.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.util.pipeline.PipelineContext
import java.io.File
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity

suspend fun ApplicationCall.respondFile(file: File, name: String, type: String) {
    response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment
            .withParameter(ContentDisposition.Parameters.FileName, name)
            .toString()
    )
    response.header("Mime-Type", type)
    respondFile(file)
}

fun ApplicationCall.getParamOrNull(name: String): String? = parameters[name]

fun ApplicationCall.getParam(name: String): String = getParamOrNull(name) ?: throw MissingParameterException(name)

fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

fun String.toUUID(): UUID = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    throw InvalidUUIDException()
}

val UUIDEntity.uuid: UUID
    get() = id.value

fun ApplicationCall.getUUIDOrNull(name: String = "uuid"): UUID? = getParamOrNull(name)?.toUUIDOrNull()

fun ApplicationCall.getUUID(name: String = "uuid"): UUID = getUUIDOrNull(name) ?: throw InvalidUUIDException()

suspend fun ApplicationCall.receivePageDefinition(): PageDefinitionDTO = if (request.contentType() == ApplicationJSON) {
    receive()
} else {
    PageDefinitionDTO()
}

fun PipelineContext<*, ApplicationCall>.jwtOrNull(): Payload? = this.call.principal<JWTPrincipal>()?.payload

fun PipelineContext<*, ApplicationCall>.userOrNull(): UserPrincipal? = this.call.principal()

val PipelineContext<*, ApplicationCall>.jwt: Payload
    get() = jwtOrNull() ?: throw ExpiredOrInvalidTokenException()

val PipelineContext<*, ApplicationCall>.authenticatedUser: UserState?
    get() = userOrNull()?.state

val PipelineContext<*, ApplicationCall>.accessToken: String?
    get() = userOrNull()?.accessToken

operator fun Payload.get(key: String): Claim = this.getClaim(key)

val Payload.uuid get() = this["id"].asString().toUUID()

inline fun PipelineContext<*, ApplicationCall>.ifRegular(block: () -> Unit) {
    if (authenticatedUser?.role == UserRole.REGULAR) block()
}

inline fun PipelineContext<*, ApplicationCall>.ifNotRegular(block: () -> Unit) {
    if (authenticatedUser?.role != UserRole.REGULAR) block()
}

fun PipelineContext<*, ApplicationCall>.adminRestrict() {
    ifRegular { throw InsufficientPermissionsException() }
}

fun PipelineContext<*, ApplicationCall>.idRestrict(uuid: UUID) {
    if (authenticatedUser?.uuid != uuid) throw IllegalActionException()
}

fun ApplicationCall.getPageParameters(): Pair<Long, Int> {
    val params = request.queryParameters
    val page = (params["page"] ?: "0").let {
        val page = it.toLongOrNull() ?: throw IllegalArgumentException("Invalid page number")
        if (page < 0) throw IllegalArgumentException("errors.number.negative")
        page
    }
    val pageSize = (params["pageSize"] ?: "25").let {
        val pageSize = it.toIntOrNull() ?: throw IllegalArgumentException("Invalid page size")
        if (pageSize < 0) throw IllegalArgumentException("errors.number.negative")
        pageSize
    }
    return page to pageSize
}

fun ApplicationCall.getHeader(header: String) = request.headers[header] ?: throw MissingHeaderException(header)

suspend fun ApplicationCall.doWithForm(
    onFields: Map<String, suspend (PartData.FormItem) -> Unit> = mapOf(),
    onFiles: Map<String, suspend (PartData.FileItem) -> Unit> = mapOf(),
    onMissing: suspend (field: String) -> Unit = {},
): Result<MultiPartData> {
    getHeader("Content-Type").let { contentType ->
        if (!contentType.startsWith("multipart/form-data")) {
            throw MissingHeaderException("content-type")
        }
    }
    return runCatching {
        receiveMultipart()
    }.onSuccess {
        val visitedFormItem = mutableSetOf<String>()
        val visitedFileItem = mutableSetOf<String>()
        it.forEachPart { part ->
            val field = part.name!!
            when (part) {
                is PartData.FormItem -> {
                    visitedFormItem.add(field)
                    onFields[field]?.invoke(part)
                }

                is PartData.FileItem -> {
                    visitedFileItem.add(field)
                    onFiles[field]?.invoke(part)
                }

                else -> {}
            }
            part.dispose()
        }
        onFields.keys.forEach { field ->
            if (field !in visitedFormItem) onMissing(field)
        }
        onFiles.keys.forEach { field ->
            if (field !in visitedFileItem) onMissing(field)
        }
    }.onFailure { throw MultipartParseException() }
}

private const val CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
fun String.Companion.random(size: Int) = (1..size)
    .map { CHARSET.random() }
    .joinToString("")
