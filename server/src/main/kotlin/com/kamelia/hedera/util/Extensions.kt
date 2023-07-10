package com.kamelia.hedera.util

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.*
import com.kamelia.hedera.plugins.UserPrincipal
import com.kamelia.hedera.rest.auth.UserState
import com.kamelia.hedera.rest.user.UserRole
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.dao.UUIDEntity
import java.io.File
import java.util.*

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

suspend fun ApplicationCall.respondFileInline(file: File, type: ContentType) {
    response.header(HttpHeaders.ContentDisposition, ContentDisposition.Inline.toString())
    val message = LocalFileContent(file, type)
    respond(message)
}

suspend fun ApplicationCall.proxyRedirect(path: String) {
    val cp = object : RequestConnectionPoint by this.request.local {
        override val uri: String = path
    }
    val req = object : ApplicationRequest by this.request {
        override val local: RequestConnectionPoint = cp
    }
    val call = object : ApplicationCall by this {
        override val request: ApplicationRequest = req
    }

    this.application.execute(call)
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
        if (page < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_PAGE_NUMBER)
        page
    }
    val pageSize = (params["pageSize"] ?: "25").let {
        val pageSize = it.toIntOrNull() ?: throw IllegalArgumentException("Invalid page size")
        if (pageSize < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_PAGE_SIZE)
        pageSize
    }
    return page to pageSize
}

fun ApplicationCall.getSliceParameters(): Pair<Int, Int> {
    val params = request.queryParameters
    val first = (params["first"] ?: "0").let {
        val first = it.toIntOrNull() ?: throw IllegalArgumentException("Invalid first number")
        if (first < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_FIRST_NUMBER)
        first
    }
    val last = (params["last"] ?: "25").let {
        val last = it.toIntOrNull() ?: throw IllegalArgumentException("Invalid last number")
        if (last < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_LAST_NUMBER)
        last
    }
    return first to last
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
