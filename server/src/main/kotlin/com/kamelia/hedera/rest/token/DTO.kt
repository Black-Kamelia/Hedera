@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.util.UUIDSerializer
import java.time.Instant
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun PersonalToken.toRepresentationDTO() = PersonalTokenDTO(
    id = id.value,
    name = name,
    creationDate = createdAt.toString(),
    lastUsed = Instant.now().toString(),
)

@Serializable
data class PersonalTokenCreationDTO(
    val name: String,
) : DTO

@Serializable
data class PersonalTokenDTO(
    val id: UUID,
    val name: String,
    val creationDate: String,
    val lastUsed: String? = null,
) : DTO
