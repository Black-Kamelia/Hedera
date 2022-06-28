package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.rest.core.pageable.PageableDTO
import com.kamelia.jellyfish.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

fun User.toDTO(): UserResponseDTO {
    return UserResponseDTO(
        id.value,
        username,
        email,
        role,
        enabled
    )
}

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
) : DTO

/**
 * DTO used to transfer user update details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserUpdateDTO(
    val username: String? = null,
    val email: String? = null,
    val role: UserRole? = null,
    val enabled: Boolean? = null
) : DTO

/**
 * DTO used to change user password.
 *
 * **Front -> Back**
 */
@Serializable
data class UserPasswordUpdateDTO(
    val oldPassword: String,
    val newPassword: String,
) : DTO

/**
 * DTO used to transfer user details to front.
 *
 * **Back -> Front**
 */
@Serializable
@PageableDTO
data class UserResponseDTO(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val enabled: Boolean
) : DTO, PageDTO<UserResponseDTO>()
