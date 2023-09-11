package com.kamelia.hedera.util

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.IllegalFilterException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidUUIDException
import com.kamelia.hedera.core.MissingHeaderException
import com.kamelia.hedera.core.MissingParameterException
import com.kamelia.hedera.core.MultipartParseException
import com.kamelia.hedera.plugins.UserPrincipal
import com.kamelia.hedera.rest.auth.UserState
import com.kamelia.hedera.rest.core.pageable.FilterObject
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
import java.io.File
import java.util.*
import kotlin.math.roundToLong
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.sql.ComplexExpression
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.LikeEscapeOp
import org.jetbrains.exposed.sql.LikePattern
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.append
import org.jetbrains.exposed.sql.stringParam
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect

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

fun ApplicationCall.jwtOrNull(): Payload? = this.principal<JWTPrincipal>()?.payload

fun ApplicationCall.userOrNull(): UserPrincipal? = this.principal()

val ApplicationCall.jwt: Payload
    get() = jwtOrNull() ?: throw ExpiredOrInvalidTokenException()

val ApplicationCall.authenticatedUser: UserState?
    get() = userOrNull()?.state

val ApplicationCall.accessToken: String?
    get() = userOrNull()?.accessToken

operator fun Payload.get(key: String): Claim = this.getClaim(key)

val Payload.uuid get() = this["id"].asString().toUUID()

inline fun PipelineContext<*, ApplicationCall>.ifRegular(block: () -> Unit) {
    if (call.authenticatedUser?.role == UserRole.REGULAR) block()
}

inline fun PipelineContext<*, ApplicationCall>.ifNotRegular(block: () -> Unit) {
    if (call.authenticatedUser?.role != UserRole.REGULAR) block()
}

fun PipelineContext<*, ApplicationCall>.adminRestrict() {
    ifRegular { throw InsufficientPermissionsException() }
}

fun PipelineContext<*, ApplicationCall>.idRestrict(uuid: UUID) {
    if (call.authenticatedUser?.uuid != uuid) throw IllegalActionException()
}

fun ApplicationCall.getPageParameters(): Pair<Long, Int> {
    val params = request.queryParameters
    val page = (params["page"] ?: "0").let {
        val page = it.toLongOrNull()
        if (page == null || page < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_PAGE_NUMBER)
        page
    }
    val pageSize = (params["pageSize"] ?: "25").let {
        val pageSize = it.toIntOrNull()
        if (pageSize == null || pageSize < 0) throw IllegalArgumentException(Errors.Pagination.INVALID_PAGE_SIZE)
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
            throw MissingHeaderException("Content-Type")
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

fun FilterObject.adaptFileSize(): FilterObject {
    val (value, unit) = value.split(";")
    val size = value.toDoubleOrNull() ?: throw IllegalFilterException(this)
    val shift = unit.toIntOrNull() ?: throw IllegalFilterException(this)
    val bytes = size * (1 shl shift)
    return copy(value = bytes.roundToLong().toString())
}

/** Checks if this expression fuzzy matches the specified [pattern]. */
infix fun <T : String?> Expression<T>.fuzzy(pattern: String) = fuzzy(LikePattern(pattern))

/** Checks if this expression fuzzy matches the specified [pattern]. */
infix fun <T : String?> Expression<T>.fuzzy(pattern: LikePattern): Op<Boolean> = when (currentDialect) {
    is PostgreSQLDialect -> FuzzyMatchOp(this, stringParam(pattern.pattern))
    else -> LikeEscapeOp(this, stringParam("%${pattern.pattern}%"), true, pattern.escapeChar)
}

class FuzzyMatchOp(
    private val expr1: Expression<*>,
    private val expr2: Expression<*>
) : Op<Boolean>(), ComplexExpression {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(expr1)
        append(" <<-> ")
        append(expr2)
        append(" <= ")
        append(Environment.searchMaxDistance.toString())
    }
}
