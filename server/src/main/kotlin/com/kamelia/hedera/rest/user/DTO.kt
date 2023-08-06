@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.user

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.util.UUIDSerializer
import com.kamelia.hedera.util.uuid
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun User.toRepresentationDTO() = UserRepresentationDTO(
    uuid,
    username,
    email,
    role,
    enabled,
    createdAt.toString(),
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
    val createdAt: String,
) : DTO

@Serializable
data class UserPageDTO(
    val page: PageDTO<UserRepresentationDTO>,
) : DTO

@Serializable
data class UserForcefullyLoggedOutDTO(
    val userId: UUID,
    val reason: String,
)
