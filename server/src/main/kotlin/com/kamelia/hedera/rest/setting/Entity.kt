package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object UserSettingsTable : IdTable<UUID>("users_settings") {

    override val id = reference("user_id", Users)
    val user get() = id

    val defaultFileVisibility = enumerationByName<FileVisibility>("default_file_visibility", 16)
        .clientDefault { FileVisibility.UNLISTED }
    val autoRemoveFiles = bool("auto_remove_files")
        .clientDefault { false }
    val filesSizeScale = enumerationByName<FilesSizeScale>("files_size_scale", 16)
        .clientDefault { FilesSizeScale.BINARY }
    val enableAnimations = bool("enable_animations")
        .clientDefault { true }
    val preferredDateStyle = enumerationByName<DateStyle>("preferred_date_style", 16)
        .clientDefault { DateStyle.SHORT }
    val preferredTimeStyle = enumerationByName<TimeStyle>("preferred_time_style", 16)
        .clientDefault { TimeStyle.MEDIUM }
    val preferredLocale = enumerationByName<Locale>("preferred_locale", 5)
        .clientDefault { Locale.en_US }

    fun createDefaultSettings(user: User): UserSettings = UserSettings.new {
        this.user = user
    }

    fun update(settings: UserSettings, dto: UserSettingsUpdateDTO): UserSettings = settings.apply {
        dto.locale?.let { preferredLocale = it }
        dto.autoRemoveFiles?.let { autoRemoveFiles = it }
    }
}

class UserSettings(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserSettings>(UserSettingsTable)

    var user by User referencedOn UserSettingsTable.id
    var defaultFileVisibility by UserSettingsTable.defaultFileVisibility
    var autoRemoveFiles by UserSettingsTable.autoRemoveFiles
    var filesSizeScale by UserSettingsTable.filesSizeScale
    var enableAnimations by UserSettingsTable.enableAnimations
    var preferredDateStyle by UserSettingsTable.preferredDateStyle
    var preferredTimeStyle by UserSettingsTable.preferredTimeStyle
    var preferredLocale by UserSettingsTable.preferredLocale
}
