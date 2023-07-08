package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.core.TokenData
import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.setting.UserSettingsRepresentationDTO
import com.kamelia.hedera.rest.user.UserRepresentationDTO
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
) : DTO

@Serializable
data class SessionOpeningDTO(
    val tokens: TokenData,
    val user: UserRepresentationDTO,
    val userSettings: UserSettingsRepresentationDTO,
) : DTO
