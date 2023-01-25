package com.kamelia.jellyfish.rest.file

import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.IllegalActionException
import com.kamelia.jellyfish.core.InsufficientPermissionsException
import com.kamelia.jellyfish.core.QueryResult
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.rest.core.pageable.PageDefinitionDTO
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.UserRole
import com.kamelia.jellyfish.rest.user.Users
import com.kamelia.jellyfish.util.FileUtils
import com.kamelia.jellyfish.util.uuid
import io.ktor.http.content.PartData
import java.util.UUID
import kotlin.math.ceil

object FileService {

    suspend fun handleFile(part: PartData.FileItem, creator: User): QueryResult<FileRepresentationDTO, List<ErrorDTO>> {
        val filename = requireNotNull(part.originalFileName) { "errors.file.name.empty" }
        require(filename.isNotBlank()) { "errors.file.name.empty" }

        val (code, type, size) = FileUtils.write(creator.uuid, part, filename)
        return QueryResult.ok(
            Files.create(
                code = code,
                name = filename,
                mimeType = type,
                size = size,
                creator = creator
            ).toRepresentationDTO()
        )
    }

    suspend fun getFile(
        code: String,
        user: User?,
    ): QueryResult<FileRepresentationDTO, ErrorDTO> = Files
        .findByCode(code)
        ?.takeUnless { file ->
            val isPrivate = file.visibility == FileVisibility.PRIVATE
            val notHasPermission = user?.let { file.ownerId != it.uuid }
            isPrivate && (notHasPermission ?: true)
        }?.let { file ->
            QueryResult.ok(file.toRepresentationDTO())
        }
        ?: QueryResult.notFound()

    suspend fun getFiles(
        user: User,
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean = false,
    ): QueryResult<FilePageDTO, List<ErrorDTO>> {
        val (files, total) = user.getFiles(page, pageSize, definition, asOwner)
        return QueryResult.ok(
            FilePageDTO(
                PageDTO(
                    files.map { it.toRepresentationDTO() },
                    page,
                    pageSize,
                    ceil(total / pageSize.toDouble()).toLong(),
                    total
                )
            )
        )
    }

    suspend fun updateFile(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): QueryResult<FileRepresentationDTO, List<ErrorDTO>> {
        val user = Users.findById(userId) ?: return QueryResult.unauthorized()
        val file = Files.findById(fileId) ?: return QueryResult.notFound()

        if (file.ownerId != user.uuid) {
            if (file.visibility == FileVisibility.PRIVATE && user.role ne UserRole.OWNER) {
                return QueryResult.notFound()
            }
            throw IllegalActionException()
        }

        return QueryResult.ok(Files.update(file, dto, user).toRepresentationDTO())
    }

    suspend fun deleteFile(
        fileId: UUID,
        userId: UUID,
    ): QueryResult<FileRepresentationDTO, List<ErrorDTO>> {
        val user = Users.findById(userId) ?: return QueryResult.unauthorized()
        val file = Files.findById(fileId) ?: return QueryResult.notFound()

        if (file.ownerId != user.uuid && user.role eq UserRole.REGULAR) {
            if (file.visibility == FileVisibility.PRIVATE) {
                return QueryResult.notFound()
            }
            throw InsufficientPermissionsException()
        }

        FileUtils.delete(file.ownerId, file.code)
        return QueryResult.ok(Files.delete(fileId)?.toRepresentationDTO())
    }

    /*
    suspend fun deleteFileByCodeAsAdmin(code: String): QueryResult<FileRepresentationDTO, List<ErrorDTO>> {
        val file = Files.findByCode(code) ?: return QueryResult.notFound()

        FileUtils.delete(file.ownerId, file.code)
        return QueryResult.ok(Files.delete(file.uuid)?.toRepresentationDTO())
    }
     */
}
