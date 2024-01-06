@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.setting.UserSettingsRepresentationDTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import com.kamelia.hedera.util.UUIDSerializer
import java.time.Instant
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
) : DTO

@Serializable
data class ResetPasswordDTO(
    val token: String,
    val password: String,
) : DTO

@Serializable
data class ResetPasswordRequestDTO(
    val email: String,
) : DTO

@Serializable
data class CheckResetPasswordTokenDTO(
    val token: String,
) : DTO

@Serializable
data class ResetPasswordTokenDTO(
    val userId: UUID,
    val expiration: String,
) : DTO

@Serializable
data class SessionOpeningDTO(
    val sessionId: UUID,
    val tokens: TokenData,
    val user: UserRepresentationDTO,
    val userSettings: UserSettingsRepresentationDTO,
) : DTO
