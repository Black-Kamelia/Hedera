@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.util.UUIDSerializer
import com.kamelia.jellyfish.util.uuid
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun User.toRepresentationDTO() = UserRepresentationDTO(
    uuid,
    username,
    email,
    role,
    enabled,
    uploadToken,
)

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
    val role: UserRole = UserRole.REGULAR,
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
    val enabled: Boolean? = null,
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
data class UserRepresentationDTO(
    val id: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val enabled: Boolean,
    val uploadToken: String,
) : DTO

@Serializable
data class UserPageDTO(
    val page: PageDTO<UserRepresentationDTO>,
) : DTO
