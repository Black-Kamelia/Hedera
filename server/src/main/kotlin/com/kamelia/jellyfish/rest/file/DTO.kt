@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.file

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.util.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.jetbrains.exposed.sql.transactions.transaction

fun File.toRepresentationDTO(): FileRepresentationDTO = transaction {
    FileRepresentationDTO(
        this@toRepresentationDTO.id.value,
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
    val size: Long,
    val visibility: String,
    val ownerId: UUID,
) : DTO

@Serializable
data class UploadPageDTO(
    val page: PageDTO<FileRepresentationDTO>,
) : DTO
