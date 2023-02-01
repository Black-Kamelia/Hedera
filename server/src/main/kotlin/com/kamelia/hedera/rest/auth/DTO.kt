package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.rest.core.DTO
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
) : DTO
