package com.kamelia.hedera.core.auth

import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.rest.user.UserRole
import java.time.Instant
import java.util.*

data class UserState(
    val uuid: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val enabled: Boolean,
    val forceChangePassword: Boolean,
    val currentDiskQuota: Long,
    val maximumDiskQuota: Long,
    val createdAt: Instant,
) {

    fun toUserRepresentationDTO() = UserRepresentationDTO(
        uuid,
        username,
        email,
        role,
        enabled,
        forceChangePassword,
        currentDiskQuota,
        if (maximumDiskQuota > 0L) currentDiskQuota.toDouble() / maximumDiskQuota.toDouble() else 0.0,
        maximumDiskQuota,
        createdAt.toString(),
    )
}


fun User.toUserState() = UserState(
    id.value,
    username,
    email,
    role,
    enabled,
    forceChangePassword,
    currentDiskQuota,
    maximumDiskQuota,
    createdAt
)
