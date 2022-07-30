package com.kamelia.jellyfish.rest.setting

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.Users
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object UserSettingsTable : IdTable<UUID>("users_settings") {

    override val id = reference("user_id", Users)
    val user get() = id

    val locale = enumerationByName<Locale>("locale", 5).clientDefault { Locale.en_US }
    val theme = enumerationByName<Theme>("theme", 5).clientDefault { Theme.LIGHT }
    val autoRemoveFiles = bool("auto_remove_files").clientDefault { false }

    suspend fun createDefaultSettings(user: User): UserSettings = Connection.query {
        UserSettings.new { this.user = user }
    }

    suspend fun update(settings: UserSettings, dto: UserSettingsUpdateDTO): UserSettings = Connection.query {
        settings.apply {
            dto.locale?.let { locale = it }
            dto.theme?.let { theme = it }
            dto.autoRemoveFiles?.let { autoRemoveFiles = it }
        }
    }
}

class UserSettings(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserSettings>(UserSettingsTable)

    var user by User referencedOn UserSettingsTable.id
    var locale by UserSettingsTable.locale
    var theme by UserSettingsTable.theme
    var autoRemoveFiles by UserSettingsTable.autoRemoveFiles
}
