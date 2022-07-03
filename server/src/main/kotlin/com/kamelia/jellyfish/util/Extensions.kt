package com.kamelia.jellyfish.util

import com.kamelia.jellyfish.core.InvalidUUIDException
import com.kamelia.jellyfish.core.MissingParameterException
import io.ktor.server.application.ApplicationCall
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity

fun ApplicationCall.getParamOrNull(name: String): String? = parameters[name]

fun ApplicationCall.getParam(name: String): String =
    getParamOrNull(name) ?: throw MissingParameterException(name)

fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

val UUIDEntity.uuid: UUID
    get() = id.value

fun ApplicationCall.getUUIDOrNull(name: String = "uuid"): UUID? =
    getParamOrNull(name)?.toUUIDOrNull()

fun ApplicationCall.getUUID(name: String = "uuid"): UUID =
    getUUIDOrNull(name) ?: throw InvalidUUIDException()
