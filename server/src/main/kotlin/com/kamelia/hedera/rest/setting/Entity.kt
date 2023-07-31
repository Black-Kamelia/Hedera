package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.user.UserTable
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select

object UserSettingsTable : UUIDTable("users_settings") {

    val defaultFileVisibility = enumerationByName<FileVisibility>("default_file_visibility", 16)
        .clientDefault { FileVisibility.UNLISTED }
    val autoRemoveFiles = bool("auto_remove_files")
        .clientDefault { false }
    val filesSizeScale = enumerationByName<FilesSizeScale>("files_size_scale", 16)
        .clientDefault { FilesSizeScale.BINARY }
    val preferredDateStyle = enumerationByName<DateStyle>("preferred_date_style", 16)
        .clientDefault { DateStyle.SHORT }
    val preferredTimeStyle = enumerationByName<TimeStyle>("preferred_time_style", 16)
        .clientDefault { TimeStyle.MEDIUM }
    val preferredLocale = enumerationByName<Locale>("preferred_locale", 5)
        .clientDefault { Locale.en }

}

class UserSettings(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<UserSettings>(UserSettingsTable) {

        fun getByUserId(userId: UUID): UserSettings = UserTable
            .join(UserSettingsTable, JoinType.INNER, UserTable.settings, UserSettingsTable.id)
            .select { UserTable.id eq userId }
            .first()
            .let { return UserSettings.wrapRow(it) }

    }

    var defaultFileVisibility by UserSettingsTable.defaultFileVisibility
    var autoRemoveFiles by UserSettingsTable.autoRemoveFiles
    var filesSizeScale by UserSettingsTable.filesSizeScale
    var preferredDateStyle by UserSettingsTable.preferredDateStyle
    var preferredTimeStyle by UserSettingsTable.preferredTimeStyle
    var preferredLocale by UserSettingsTable.preferredLocale

    fun update(dto: UserSettingsUpdateDTO): UserSettings = apply {
        dto.defaultFileVisibility?.let { defaultFileVisibility = it }
        dto.autoRemoveFiles?.let { autoRemoveFiles = it }
        dto.filesSizeScale?.let { filesSizeScale = it }
        dto.preferredDateStyle?.let { preferredDateStyle = it }
        dto.preferredTimeStyle?.let { preferredTimeStyle = it }
        dto.preferredLocale?.let { preferredLocale = it }
    }
}
