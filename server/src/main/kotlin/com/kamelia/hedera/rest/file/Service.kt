package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.MessageDTO
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.database.Connection
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

    suspend fun handleFileWithToken(
        part: PartData.FileItem,
        creatorToken: String
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val creator = Users.findByUploadToken(creatorToken) ?: throw ExpiredOrInvalidTokenException()
        handleFile(part, creator)
    }

    suspend fun handleFile(
        part: PartData.FileItem,
        creatorId: UUID
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val creator = Users.findById(creatorId) ?: throw ExpiredOrInvalidTokenException()
        handleFile(part, creator)
    }

    private suspend fun handleFile(
        part: PartData.FileItem,
        creator: User
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val filename = requireNotNull(part.originalFileName) { Errors.Uploads.EMPTY_FILE_NAME }
        require(filename.isNotBlank()) { Errors.Uploads.EMPTY_FILE_NAME }

        val (code, type, size) = FileUtils.write(creator.uuid, part, filename)
        Response.created(
            Files.create(
                code = code,
                name = filename,
                mimeType = type,
                size = size,
                visibility = creator.settings.defaultFileVisibility,
                creator = creator
            ).toRepresentationDTO()
        )
    }

    suspend fun getFile(
        code: String,
        authId: UUID?,
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val user = authId?.let { Users.findById(it) }
        Files.findByCode(code)
            ?.takeUnless { file ->
                val isPrivate = file.visibility == FileVisibility.PRIVATE
                val notHasPermission = user?.let { file.ownerId != it.uuid }
                isPrivate && (notHasPermission ?: true)
            }?.let { file ->
                Response.ok(file.toRepresentationDTO())
            }
            ?: Response.notFound()
    }

    suspend fun getFiles(
        userId: UUID,
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean = false,
    ): Response<FilePageDTO, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val (files, total) = user.getFiles(page, pageSize, definition, asOwner)
        Response.ok(
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

    suspend fun getFilesFormats(userId: UUID): Response<List<String>, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        Response.ok(user.getFilesFormats())
    }

    suspend fun updateFile(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: throw FileNotFoundException()

        if (file.ownerId != user.uuid) {
            if (file.visibility != FileVisibility.PRIVATE || !(user.role ne UserRole.OWNER)) {
                throw IllegalActionException()
            }
            throw FileNotFoundException()
        }

        Response.ok(Files.update(file, dto, user).toRepresentationDTO())
    }

    suspend fun updateFileVisibility(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): Response<MessageDTO<FileRepresentationDTO>, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: throw FileNotFoundException()

        if (file.ownerId != user.uuid) {
            if (file.visibility != FileVisibility.PRIVATE || !(user.role ne UserRole.OWNER)) {
                throw IllegalActionException()
            }
            throw FileNotFoundException()
        }

        val oldVisibility = file.visibility
        val payload = Files.update(file, FileUpdateDTO(visibility = dto.visibility), user).toRepresentationDTO()
        Response.ok(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Files.Update.Visibility.Success.TITLE),
                message = MessageKeyDTO.of(
                    Actions.Files.Update.Visibility.Success.MESSAGE,
                    "name" to file.name,
                    "oldVisibility" to oldVisibility.toMessageKey(),
                    "newVisibility" to payload.visibility.toMessageKey()
                ),
                payload = payload
            )
        )
    }

    suspend fun updateFileName(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): Response<MessageDTO<FileRepresentationDTO>, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: throw FileNotFoundException()

        if (file.ownerId != user.uuid) {
            if (file.visibility != FileVisibility.PRIVATE || !(user.role ne UserRole.OWNER)) {
                throw IllegalActionException()
            }
            throw FileNotFoundException()
        }

        val oldName = file.name
        val payload = Files.update(file, FileUpdateDTO(name = dto.name), user).toRepresentationDTO()
        Response.ok(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Files.Update.Name.Success.TITLE),
                message = MessageKeyDTO.of(
                    Actions.Files.Update.Name.Success.MESSAGE,
                    "oldName" to oldName,
                    "newName" to payload.name,
                ),
                payload = payload
            )
        )
    }

    suspend fun deleteFile(
        fileId: UUID,
        userId: UUID,
    ): Response<MessageDTO<FileRepresentationDTO>, String> = Connection.transaction {
        val user = Users.findById(userId) ?: throw ExpiredOrInvalidTokenException()
        val file = Files.findById(fileId) ?: throw FileNotFoundException()

        if (file.ownerId != user.uuid && user.role eq UserRole.REGULAR) {
            if (file.visibility != FileVisibility.PRIVATE) {
                throw InsufficientPermissionsException()
            }
            throw FileNotFoundException()
        }

        FileUtils.delete(file.ownerId, file.code)
        Response.ok(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Files.Delete.Success.TITLE),
                message = MessageKeyDTO.of(Actions.Files.Delete.Success.MESSAGE, "name" to file.name),
                payload = Files.delete(fileId)?.toRepresentationDTO()
            )
        )
    }

    /*
    suspend fun deleteFileByCodeAsAdmin(code: String): QueryResult<FileRepresentationDTO, String> {
        val file = Files.findByCode(code) ?: return QueryResult.notFound()

        FileUtils.delete(file.ownerId, file.code)
        return QueryResult.ok(Files.delete(file.uuid)?.toRepresentationDTO())
    }
     */
}
