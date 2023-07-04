@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.setting

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun UserSettings.toRepresentationDTO() = UserSettingsRepresentationDTO(
    defaultFileVisibility = defaultFileVisibility,
    autoRemoveFiles = autoRemoveFiles,
    filesSizeScale = filesSizeScale,
    enableAnimations = enableAnimations,
    preferredDateStyle = preferredDateStyle,
    preferredTimeStyle = preferredTimeStyle,
    preferredLocale = preferredLocale,
)

/**
 * DTO used to transfer user update details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserSettingsUpdateDTO(
    val locale: Locale? = null,
    val autoRemoveFiles: Boolean? = null,
) : DTO

/**
 * DTO used to transfer user details to front.
 *
 * **Back -> Front**
 */
@Serializable
data class UserSettingsRepresentationDTO(
    val defaultFileVisibility: FileVisibility,
    val autoRemoveFiles: Boolean,
    val filesSizeScale: FilesSizeScale,
    val enableAnimations: Boolean,
    val preferredDateStyle: DateStyle,
    val preferredTimeStyle: TimeStyle,
    val preferredLocale: Locale,
) : DTO
