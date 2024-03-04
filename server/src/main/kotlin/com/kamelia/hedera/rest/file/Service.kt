package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidPersonalTokenException
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.constant.BulkActions
import com.kamelia.hedera.core.response.ActionResponse
import com.kamelia.hedera.core.response.BulkActionResponse
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.core.response.asMessage
import com.kamelia.hedera.core.validate
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.thumbnail.ThumbnailService
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.user.DiskQuotaService.decreaseDiskQuota
import com.kamelia.hedera.rest.user.DiskQuotaService.increaseDiskQuota
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.rest.user.UserTable
import com.kamelia.hedera.util.toUUIDShort
import com.kamelia.hedera.util.uuid
import io.ktor.http.*
import io.ktor.http.content.*
import java.time.Instant
import java.util.*
import kotlin.math.ceil
import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

val CUSTOM_LINK_REGEX = """^[a-z0-9]+(-[a-z0-9]+)*$""".toRegex()
val PERSONAL_TOKEN_REGEX = """^[a-f0-9]{64}$""".toRegex()

object FileService {

    suspend fun handleFileWithToken(
        part: PartData.FileItem,
        creatorToken: String
    ): ActionResponse<FileRepresentationDTO> = Connection.transaction {
        if (!PERSONAL_TOKEN_REGEX.matches(creatorToken)) throw InvalidPersonalTokenException()

        val tokenId = creatorToken.substring(0, 32).toUUIDShort()
        val token = creatorToken.substring(32)

        val personalToken = PersonalToken.findById(tokenId) ?: throw InvalidPersonalTokenException()
        val locale = personalToken.owner.settings.preferredLocale
        if (personalToken.deleted) throw InvalidPersonalTokenException(locale)
        if (personalToken.token.startsWith("deleted_")) throw InvalidPersonalTokenException(locale)
        if (!Hasher.verify(token, personalToken.token).verified) throw InvalidPersonalTokenException(locale)

        handleFile(part, personalToken.owner, personalToken)
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
        val fileName = requireNotNull(part.originalFileName) { Errors.Uploads.EMPTY_FILE_NAME }
        require(fileName.isNotBlank()) { Errors.Uploads.EMPTY_FILE_NAME }

        val uploadedFile = DiskFileService.receiveFile(creator, part, fileName)
        creator.increaseDiskQuota(uploadedFile.size)

        // TODO: handle thumbnail creation failure
        val thumbnail = ThumbnailService.createThumbnail(uploadedFile.file, creator.id.value, uploadedFile.mimeType, uploadedFile.code)
        val blurhash = ThumbnailService.getBlurhashOrNull(thumbnail)
        val file = File.create(
            code = uploadedFile.code,
            name = fileName,
            mimeType = uploadedFile.mimeType,
            size = uploadedFile.size,
            blurhash = blurhash,
            visibility = creator.settings.defaultFileVisibility,
            creator = creator,
            uploadToken = uploadToken,
        )

        return ActionResponse.created(
            title = Actions.Files.Upload.success.title,
            message = Actions.Files.Upload.success.message.withParameters("name" to fileName),
            payload = file.toRepresentationDTO()
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
            title = Actions.Files.Update.Visibility.success.title,
            message = Actions.Files.Update.Visibility.success.message.withParameters(
                "name" to file.name,
                "oldVisibility" to oldVisibility.toMessageKey(),
                "newVisibility" to payload.visibility.toMessageKey()
            ),
            payload = payload
        )
    }

    suspend fun bulkUpdateFilesVisibility(
        userId: UUID,
        dto: BulkUpdateVisibilityDTO,
    ): BulkActionResponse<String> = Connection.transaction {
        val condition = (FileTable.id inList dto.ids) and (FileTable.owner eq DaoEntityID(userId, UserTable))

        val files = File.find(condition).toSet()
        FileTable.update({ condition }) {
            it[visibility] = dto.fileVisibility
            it[updatedAt] = Instant.now()
            it[updatedBy] = DaoEntityID(userId, UserTable)
        }

        BulkActionResponse.of(
            BulkActions.Files.Update.Visibility,
            success = files.map { it.id.value.toString() },
            total = dto.ids.size,
        ).withMessageParameters("newVisibility" to dto.fileVisibility.toMessageKey().asMessage())
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
                title = Actions.Files.Update.Name.success.title,
                message = Actions.Files.Update.Name.success.message.withParameters(
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
                title = Actions.Files.Update.CustomLink.success.title,
                message = Actions.Files.Update.CustomLink.success.message.withParameters(
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
            title = Actions.Files.Update.RemoveCustomLink.success.title,
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

        file.owner.decreaseDiskQuota(file.size)
        file.delete()
        DiskFileService.delete(file.ownerId, file.code)

        ActionResponse.ok(
            title = Actions.Files.Delete.success.title,
            message = Actions.Files.Delete.success.message.withParameters("name" to file.name),
            payload = file.toRepresentationDTO()
        )
    }

    suspend fun bulkDeleteFiles(
        fileIds: List<UUID>,
        userId: UUID,
    ): BulkActionResponse<String> = Connection.transaction {
        // Select all files to delete
        val files = File.find {
            (FileTable.id inList fileIds) and (FileTable.owner eq DaoEntityID(userId, UserTable))
        }.toSet()
        // Try to delete all files from the disk
        val deletedFiles = files.filter { DiskFileService.delete(it.ownerId, it.code) }.toSet()
        // Delete all disk-deleted files from the database
        FileTable.deleteWhere { FileTable.id inList deletedFiles.map { it.id } }

        // Decrease the current disk quota of the user
        val user = User[userId]
        user.decreaseDiskQuota(deletedFiles.sumOf { it.size })

        BulkActionResponse.of(
            BulkActions.Files.Delete,
            success = deletedFiles.map { it.id.value.toString() },
            fail = files.minus(deletedFiles).map { it.id.value.toString() },
            total = fileIds.size,
        )
    }
}
