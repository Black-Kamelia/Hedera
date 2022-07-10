package com.kamelia.jellyfish.rest.auth

import com.kamelia.jellyfish.rest.core.DTO
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
) : DTO
