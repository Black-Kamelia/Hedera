@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.file

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.util.UUIDSerializer
import com.kamelia.hedera.util.uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

fun File.toRepresentationDTO(): FileRepresentationDTO = FileRepresentationDTO(
    uuid,
    code,
    name,
    mimeType,
    size.toSizeDTO(),
    visibility,
    FileOwnerDTO(owner.uuid, owner.username),
    createdAt.toString(),
)

/**
 * DTO used to transfer upload update details.
 *
 * **Front -> Back**
 */
@Serializable
data class FileUpdateDTO(
    val name: String? = null,
    val visibility: FileVisibility? = null,
) : DTO

/**
 * DTO used to transfer upload details to front.
 *
 * **Back -> Front**
 */
@Serializable
data class FileRepresentationDTO(
    val id: UUID,
    val code: String,
    val name: String,
    val mimeType: String,
    val size: FileSizeDTO,
    val visibility: FileVisibility,
    val owner: FileOwnerDTO,
    val createdAt: String,
) : DTO

@Serializable
data class FileSizeDTO(
    val value: String,
    val shift: Int,
) : DTO

@Serializable
data class FileOwnerDTO(
    val id: UUID,
    val username: String,
) : DTO

@Serializable
data class FilePageDTO(
    val page: PageDTO<FileRepresentationDTO>,
) : DTO

fun Long.toSizeDTO(): FileSizeDTO {
    var size = toDouble()
    var shift = 0
    do {
        size /= 1024
        shift += 10
    } while (size > 1024)
    return FileSizeDTO(String.format("%.2f", size), shift)
}
