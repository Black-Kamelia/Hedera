@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.util.UUIDSerializer
import java.time.Instant
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun PersonalToken.toRepresentationDTO(
    token: String? = null,
    lastUsed: Instant? = null,
    usage: Long? = null,
    deleted: Boolean? = null,
) = PersonalTokenDTO(
    id = id.value,
    token = if (token != null) "${id.value.toString().replace("-", "")}$token" else null,
    name = name,
    createdAt = createdAt.toString(),
    lastUsed = lastUsed?.toString(),
    usage = usage,
    deleted = deleted,
)

@Serializable
data class PersonalTokenCreationDTO(
    val name: String,
) : DTO

@Serializable
data class PersonalTokenDTO(
    val id: UUID,
    val token: String? = null,
    val name: String,
    val createdAt: String,
    val lastUsed: String? = null,
    val usage: Long? = null,
    val deleted: Boolean? = null,
) : DTO
