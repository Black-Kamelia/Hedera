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
    preferredDateStyle = preferredDateStyle,
    preferredTimeStyle = preferredTimeStyle,
    preferredLocale = preferredLocale,
    uploadBehavior = uploadBehavior,
)

/**
 * DTO used to transfer user update details.
 *
 * **Front -> Back**
 */
@Serializable
data class UserSettingsUpdateDTO(
    val defaultFileVisibility: FileVisibility? = null,
    val autoRemoveFiles: Boolean? = null,
    val filesSizeScale: FilesSizeScale? = null,
    val preferredDateStyle: DateStyle? = null,
    val preferredTimeStyle: TimeStyle? = null,
    val preferredLocale: Locale? = null,
    val uploadBehavior: UploadBehavior? = null,
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
    val preferredDateStyle: DateStyle,
    val preferredTimeStyle: TimeStyle,
    val preferredLocale: Locale,
    val uploadBehavior: UploadBehavior,
) : DTO
