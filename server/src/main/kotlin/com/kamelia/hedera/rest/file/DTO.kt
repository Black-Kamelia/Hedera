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
    size,
    visibility,
    customLink,
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
    val customLink: String? = null,
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
    val visibility: FileVisibility,
    val customLink: String? = null,
    val owner: FileOwnerDTO,
    val createdAt: String,
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
