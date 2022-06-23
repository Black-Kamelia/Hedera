package com.kamelia.jellyfish.util

import java.lang.IllegalArgumentException
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity

fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

val UUIDEntity.uuid: UUID
    get() = id.value
