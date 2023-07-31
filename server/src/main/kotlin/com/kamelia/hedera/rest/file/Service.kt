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
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.token.PersonalTokenTable
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
        val token = PersonalToken.findByToken(creatorToken) ?: throw ExpiredOrInvalidTokenException()

        if (token.deleted) throw ExpiredOrInvalidTokenException()

        handleFile(part, token.owner, token)
    }

    suspend fun handleFile(
        part: PartData.FileItem,
        creatorId: UUID
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val user = User[creatorId]

        handleFile(part, user)
    }

    private suspend fun handleFile(
        part: PartData.FileItem,
        creator: User,
        uploadToken: PersonalToken? = null,
    ): Response<FileRepresentationDTO, String> {
        val filename = requireNotNull(part.originalFileName) { Errors.Uploads.EMPTY_FILE_NAME }
        require(filename.isNotBlank()) { Errors.Uploads.EMPTY_FILE_NAME }

        val (code, type, size) = FileUtils.write(creator.uuid, part, filename)

        return Response.created(
            File.create(
                code = code,
                name = filename,
                mimeType = type,
                size = size,
                visibility = creator.settings.defaultFileVisibility,
                creator = creator,
                uploadToken = uploadToken,
            ).toRepresentationDTO()
        )
    }

    suspend fun getFile(
        code: String,
        authId: UUID? = null,
    ): Response<FileRepresentationDTO, String> = Connection.transaction {
        val file = File.findByCode(code) ?: throw FileNotFoundException()
        val user = authId?.let { User.findById(it) }

        if (file.visibility == FileVisibility.PRIVATE && user?.uuid != file.ownerId) {
            throw FileNotFoundException()
        }

        Response.ok(file.toRepresentationDTO())
    }

    suspend fun getFiles(
        userId: UUID,
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean = false,
    ): Response<FilePageDTO, String> = Connection.transaction {
        val user = User[userId]
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

    suspend fun getFilesFormats(
        userId: UUID
    ): Response<List<String>, String> = Connection.transaction {
        val user = User[userId]

        Response.ok(user.getFilesFormats())
    }

    private fun updateFile(
        file: File,
        user: User,
        dto: FileUpdateDTO,
    ): File {
        if (file.ownerId != user.uuid) {
            if (file.visibility != FileVisibility.PRIVATE || !(user.role ne UserRole.OWNER)) {
                throw IllegalActionException()
            }
            throw FileNotFoundException()
        }

        return file.update(dto, user)
    }

    suspend fun updateFileVisibility(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): Response<MessageDTO<FileRepresentationDTO>, String> = Connection.transaction {
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        val oldVisibility = file.visibility
        val updatedFile = updateFile(file, user, FileUpdateDTO(visibility = dto.visibility))
        val payload = updatedFile.toRepresentationDTO()

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
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        val oldName = file.name
        val updatedFile = updateFile(file, user, FileUpdateDTO(name = dto.name))
        val payload = updatedFile.toRepresentationDTO()

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
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        if (file.ownerId != user.uuid && user.role eq UserRole.REGULAR) {
            if (file.visibility != FileVisibility.PRIVATE) {
                throw InsufficientPermissionsException()
            }
            throw FileNotFoundException()
        }

        file.delete()
        FileUtils.delete(file.ownerId, file.code)

        Response.ok(
            MessageDTO(
                title = MessageKeyDTO.of(Actions.Files.Delete.Success.TITLE),
                message = MessageKeyDTO.of(Actions.Files.Delete.Success.MESSAGE, "name" to file.name),
                payload = file.toRepresentationDTO()
            )
        )
    }
}
