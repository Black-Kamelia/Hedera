@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.upload

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.util.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

fun Upload.toRepresentationDTO(): UploadRepresentationDTO {
    return UploadRepresentationDTO(
        id.value,
        code,
        name,
        mimeType,
        size,
        visibility.toString(),
        owner.id.value
    )
}

/**
 * DTO used to transfer upload update details.
 *
 * **Front -> Back**
 */
@Serializable
data class UploadUpdateDTO(
    val name: String? = null,
    val visibility: UploadVisibility? = null,
) : DTO

/**
 * DTO used to transfer upload details to front.
 *
 * **Back -> Front**
 */
@Serializable
data class UploadRepresentationDTO(
    val id: UUID,
    val code: String,
    val name: String,
    val mimeType: String,
    val size: Long,
    val visibility: String,
    val ownerId: UUID,
) : DTO

@Serializable
data class UploadPageDTO(
    val page: PageDTO<UploadRepresentationDTO>,
) : DTO
