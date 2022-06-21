package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * DTO used to transfer user signup details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserDTO(
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.REGULAR
)

/**
 * DTO used to transfer user update details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserUpdateDTO(
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val enabled: Boolean
)

/**
 * DTO used to transfer user details to front.
 *
 * **Back -> Front**
 */
@Serializable
data class UserResponseDTO(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val enabled: Boolean
)