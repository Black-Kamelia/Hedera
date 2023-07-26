package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.DTO
import java.time.Instant

fun PersonalToken.toRepresentationDTO() = PersonalTokenDTO(
    name = name,
    lastUsed = Instant.now(),
)

data class PersonalTokenCreationDTO(
    val name: String,
) : DTO

data class PersonalTokenDTO(
    val name: String,
    val lastUsed: Instant? = null,
) : DTO
