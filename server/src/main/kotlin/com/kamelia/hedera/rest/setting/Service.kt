package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.core.Response
import com.kamelia.hedera.database.Connection
import java.util.*

object UserSettingsService {

    suspend fun getUserSettings(
        userId: UUID
    ): Response<UserSettingsRepresentationDTO> = Connection.transaction {
        val userSettings = UserSettings.getByUserId(userId)

        Response.ok(userSettings.toRepresentationDTO())
    }

    suspend fun updateUserSettings(
        userId: UUID,
        dto: UserSettingsUpdateDTO
    ): Response<UserSettingsRepresentationDTO> = Connection.transaction {
        val userSettings = UserSettings.getByUserId(userId)
        val updatedUserSettings = userSettings.update(dto)

        Response.ok(updatedUserSettings.toRepresentationDTO())
    }

}