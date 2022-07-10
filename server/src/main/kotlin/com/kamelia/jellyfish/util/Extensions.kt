package com.kamelia.jellyfish.util

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.InvalidUUIDException
import com.kamelia.jellyfish.core.MissingParameterException
import com.kamelia.jellyfish.rest.user.UserRole
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.util.pipeline.PipelineContext
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity

fun ApplicationCall.getParamOrNull(name: String): String? = parameters[name]

fun ApplicationCall.getParam(name: String): String = getParamOrNull(name) ?: throw MissingParameterException(name)

fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

val UUIDEntity.uuid: UUID
    get() = id.value

fun ApplicationCall.getUUIDOrNull(name: String = "uuid"): UUID? = getParamOrNull(name)?.toUUIDOrNull()

fun ApplicationCall.getUUID(name: String = "uuid"): UUID = getUUIDOrNull(name) ?: throw InvalidUUIDException()

val PipelineContext<*, ApplicationCall>.jwt: Payload
    get() = this.call.principal<JWTPrincipal>()?.payload ?: throw ExpiredOrInvalidTokenException()

operator fun Payload.get(key: String): Claim = this.getClaim(key)

inline fun PipelineContext<*, ApplicationCall>.ifRegular(block: () -> Unit) {
    val role = UserRole.valueOf(jwt["role"].asString())
    if (role == UserRole.REGULAR) block()
}

inline fun PipelineContext<*, ApplicationCall>.ifNotRegular(block: () -> Unit) {
    val role = UserRole.valueOf(jwt["role"].asString())
    if (role != UserRole.REGULAR) block()
}

fun PipelineContext<*, ApplicationCall>.adminRestrict() {
    ifRegular { throw ExpiredOrInvalidTokenException() }
}

fun PipelineContext<*, ApplicationCall>.idRestrict(uuid: UUID) {
    val id = jwt["id"].asString()
    if (id != uuid.toString()) throw ExpiredOrInvalidTokenException()
}
