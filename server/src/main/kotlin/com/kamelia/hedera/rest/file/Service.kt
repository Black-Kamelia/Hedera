package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.ActionResponse
import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientDiskQuotaException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.asMessage
import com.kamelia.hedera.core.validate
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.util.FileUtils
import com.kamelia.hedera.util.uuid
import io.ktor.http.*
import io.ktor.http.content.*
import java.util.*
import kotlin.math.ceil

val CUSTOM_LINK_REGEX = """^[a-z0-9]+(-[a-z0-9]+)*$""".toRegex()

object FileService {

    suspend fun handleFileWithToken(
        part: PartData.FileItem,
        creatorToken: String
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        val token = PersonalToken.findByToken(creatorToken) ?: throw ExpiredOrInvalidTokenException()

        if (token.deleted) throw ExpiredOrInvalidTokenException()

        handleFile(part, token.owner, token)
    }

    suspend fun handleFile(
        part: PartData.FileItem,
        creatorId: UUID
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        val user = User[creatorId]

        handleFile(part, user)
    }

    private suspend fun handleFile(
        part: PartData.FileItem,
        creator: User,
        uploadToken: PersonalToken? = null,
    ): ActionResponse<FileRepresentationDTO> {
        val filename = requireNotNull(part.originalFileName) { Errors.Uploads.EMPTY_FILE_NAME }
        require(filename.isNotBlank()) { Errors.Uploads.EMPTY_FILE_NAME }

        val (code, type, size) = FileUtils.write(creator.uuid, part, filename)

        // TODO: Find a way to determine size BEFORE writing to disk...
        if (!creator.unlimitedDiskQuota && creator.currentDiskQuota + size > creator.maximumDiskQuota) {
            throw InsufficientDiskQuotaException()
        }

        creator.increaseCurrentDiskQuota(size)

        val file = File.create(
            code = code,
            name = filename,
            mimeType = type,
            size = size,
            visibility = creator.settings.defaultFileVisibility,
            creator = creator,
            uploadToken = uploadToken,
        ).toRepresentationDTO()

        return ActionResponse.created(
            title = Actions.Files.Upload.Success.TITLE.asMessage(),
            message = Actions.Files.Upload.Success.MESSAGE.asMessage("name" to filename),
            payload = file
        )
    }

    private fun getFile(
        file: File,
        owner: User,
        authId: UUID? = null,
    ): Response<FileRepresentationDTO> {
        val user = authId?.let { User.findById(it) }

        if (!owner.enabled) {
            throw FileNotFoundException()
        }

        if (file.visibility == FileVisibility.PRIVATE && user?.id != owner.id) {
            throw FileNotFoundException()
        }

        return Response.ok(file.toRepresentationDTO())
    }

    suspend fun getFileFromCode(
        code: String,
        authId: UUID? = null,
    ): Response<FileRepresentationDTO> = Connection.transaction {
        val (file, owner) = File.findByCodeWithOwner(code) ?: throw FileNotFoundException()
        getFile(file, owner, authId)
    }

    suspend fun getFileFromCustomLink(
        customLink: String,
        authId: UUID? = null,
    ): Response<FileRepresentationDTO> = Connection.transaction {
        val (file, owner) = File.findByCustomLinkWithOwner(customLink) ?: throw FileNotFoundException()
        getFile(file, owner, authId)
    }

    suspend fun getFiles(
        userId: UUID,
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean = false,
    ): Response<FilePageDTO> = Connection.transaction {
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
    ): Response<List<String>> = Connection.transaction {
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
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        val oldVisibility = file.visibility
        val updatedFile = updateFile(file, user, FileUpdateDTO(visibility = dto.visibility))
        val payload = updatedFile.toRepresentationDTO()

        ActionResponse.ok(
            title = Actions.Files.Update.Visibility.Success.TITLE.asMessage(),
            message = Actions.Files.Update.Visibility.Success.MESSAGE.asMessage(
                "name" to file.name,
                "oldVisibility" to oldVisibility.toMessageKey(),
                "newVisibility" to payload.visibility.toMessageKey()
            ),
            payload = payload
        )
    }

    suspend fun updateFileName(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        validate {
            val file = File.findById(fileId) ?: throw FileNotFoundException()
            val user = User[userId]

            if (dto.name == null) {
                raiseError("name", Errors.Files.Name.MISSING_NAME)
            } else {
                if (dto.name.length > 255) raiseError("name", Errors.Files.Name.NAME_TOO_LONG)
            }

            catchErrors()

            val oldName = file.name
            val updatedFile = updateFile(file, user, FileUpdateDTO(name = dto.name))
            val payload = updatedFile.toRepresentationDTO()

            ActionResponse.ok(
                payload = payload,
                title = Actions.Files.Update.Name.Success.TITLE.asMessage(),
                message = Actions.Files.Update.Name.Success.MESSAGE.asMessage(
                    "oldName" to oldName,
                    "newName" to payload.name,
                ),
            )
        }
    }

    suspend fun updateCustomLink(
        fileId: UUID,
        userId: UUID,
        dto: FileUpdateDTO,
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        validate {
            val file = File.findById(fileId) ?: throw FileNotFoundException()
            val user = User[userId]

            if (dto.customLink == null) {
                raiseError("customLink", Errors.Files.CustomLink.MISSING_SLUG)
            } else {
                if (!CUSTOM_LINK_REGEX.matches(dto.customLink)) {
                    raiseError("customLink", Errors.Files.CustomLink.INVALID_FORMAT)
                }
                val fileByLink = File.findByCustomLink(dto.customLink)
                if (fileByLink != null && fileByLink != file) {
                    raiseError("customLink", Errors.Files.CustomLink.ALREADY_EXISTS, HttpStatusCode.Forbidden)
                }
            }

            catchErrors()

            val updatedFile = updateFile(file, user, FileUpdateDTO(customLink = dto.customLink))
            val payload = updatedFile.toRepresentationDTO()

            ActionResponse.ok(
                payload = payload,
                title = Actions.Files.Update.CustomLink.Success.TITLE.asMessage(),
                message = Actions.Files.Update.CustomLink.Success.MESSAGE.asMessage(
                    "newCustomLink" to (payload.customLink ?: ""),
                ),
            )
        }
    }

    suspend fun removeCustomLink(
        fileId: UUID,
        userId: UUID,
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        val updatedFile = updateFile(file, user, FileUpdateDTO(customLink = ""))
        val payload = updatedFile.toRepresentationDTO()

        ActionResponse.ok(
            payload = payload,
            title = Actions.Files.Update.RemoveCustomLink.Success.TITLE.asMessage(),
        )
    }

    suspend fun deleteFile(
        fileId: UUID,
        userId: UUID,
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        val file = File.findById(fileId) ?: throw FileNotFoundException()
        val user = User[userId]

        if (file.ownerId != user.uuid && user.role eq UserRole.REGULAR) {
            if (file.visibility != FileVisibility.PRIVATE) {
                throw InsufficientPermissionsException()
            }
            throw FileNotFoundException()
        }

        file.owner.decreaseCurrentDiskQuota(file.size)
        file.delete()
        FileUtils.delete(file.ownerId, file.code)

        ActionResponse.ok(
            title = Actions.Files.Delete.Success.TITLE.asMessage(),
            message = Actions.Files.Delete.Success.MESSAGE.asMessage("name" to file.name),
            payload = file.toRepresentationDTO()
        )
    }
}
