@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.setting

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun UserSettings.toRepresentationDTO() = UserSettingsRepresentationDTO(
    locale,
    autoRemoveFiles,
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
    val locale: Locale,
    val autoRemoveFiles: Boolean,
) : DTO
