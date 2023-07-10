package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.database.Connection
import java.util.*

object UserSettingsService {

    suspend fun getUserSettings(userId: UUID): Response<UserSettingsRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        val userSettings = UserSettingsTable.findByUserId(userId)
        Response.ok(userSettings.toRepresentationDTO())
    }

    suspend fun updateUserSettings(userId: UUID, dto: UserSettingsUpdateDTO): Response<UserSettingsRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        val userSettings = UserSettingsTable.findByUserId(userId)
        val updatedUserSettings = UserSettingsTable.update(userSettings, dto)
        Response.ok(updatedUserSettings.toRepresentationDTO())
    }

}