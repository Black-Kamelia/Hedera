package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.FileUtils
import com.kamelia.hedera.util.uuid
import io.ktor.http.content.*
import java.util.*
import kotlin.math.ceil

object FileService {

    suspend fun handleFile(part: PartData.FileItem, creator: User): Response<FileRepresentationDTO, String> {
        val filename = requireNotNull(part.originalFileName) { "errors.file.name.empty" }
        require(filename.isNotBlank()) { "errors.file.name.empty" }

        val (code, type, size) = FileUtils.write(creator.uuid, part, filename)
        return Response.ok(
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
    ): Response<FileRepresentationDTO, String> = Files
        .findByCode(code)
        ?.takeUnless { file ->
            val isPrivate = file.visibility == FileVisibility.PRIVATE
            val notHasPermission = user?.let { file.ownerId != it.uuid }
            isPrivate && (notHasPermission ?: true)
        }?.let { file ->
            Response.ok(file.toRepresentationDTO())
        }
        ?: Response.notFound()

    suspend fun getFiles(
        user: User,
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean = false,
    ): Response<FilePageDTO, String> {
        val (files, total) = user.getFiles(page, pageSize, definition, asOwner)
        return Response.ok(
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
    ): Response<FileRepresentationDTO, String> {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: return Response.notFound()

        if (file.ownerId != user.uuid) {
            if (file.visibility != FileVisibility.PRIVATE || !(user.role ne UserRole.OWNER)) {
                throw IllegalActionException()
            }
            return Response.notFound()
        }

        return Response.ok(Files.update(file, dto, user).toRepresentationDTO())
    }

    suspend fun deleteFile(
        fileId: UUID,
        userId: UUID,
    ): Response<FileRepresentationDTO, String> {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: return Response.notFound()

        if (file.ownerId != user.uuid && user.role eq UserRole.REGULAR) {
            if (file.visibility != FileVisibility.PRIVATE) {
                throw InsufficientPermissionsException()
            }
            return Response.notFound()
        }

        FileUtils.delete(file.ownerId, file.code)
        return Response.ok(Files.delete(fileId)?.toRepresentationDTO())
    }

    /*
    suspend fun deleteFileByCodeAsAdmin(code: String): QueryResult<FileRepresentationDTO, String> {
        val file = Files.findByCode(code) ?: return QueryResult.notFound()

        FileUtils.delete(file.ownerId, file.code)
        return QueryResult.ok(Files.delete(file.uuid)?.toRepresentationDTO())
    }
     */
}
