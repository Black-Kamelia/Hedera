@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.user

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.file.FileSizeDTO
import com.kamelia.hedera.rest.file.toSizeDTO
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
    currentDiskQuota.toSizeDTO(),
    if(maximumDiskQuota > 0.toLong()) currentDiskQuota.toDouble() / maximumDiskQuota.toDouble() else 0.toDouble(),
    maximumDiskQuota.toSizeDTO(),
    maximumDiskQuota == (-1).toLong(),
    createdAt.toString(),
)

/**
 * DTO used to transfer user creation details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserCreationDTO(
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.REGULAR,
    val diskQuota: FileSizeDTO? = null,
    val forceChangePassword: Boolean = false,
    val unlimitedDiskQuota: Boolean = false,
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
    val currentDiskQuota: FileSizeDTO,
    val currentDiskQuotaRatio: Double,
    val maximumDiskQuota: FileSizeDTO,
    val unlimitedDiskQuota: Boolean,
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
