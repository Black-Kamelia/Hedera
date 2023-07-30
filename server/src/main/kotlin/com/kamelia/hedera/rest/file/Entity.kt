package com.kamelia.hedera.rest.file

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.token.PersonalTokens
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.uuid
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class FileVisibility {
    PRIVATE,
    UNLISTED,
    PUBLIC,

    ;

    companion object {

        fun valueOfOrNull(value: String) = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun toMessageKey() = "pages.files.visibility.${name.lowercase()}"
}

object Files : AuditableUUIDTable("files") {

    val code = varchar("code", 10).uniqueIndex()
    val name = varchar("name", 255)
    val mimeType = varchar("mime_type", 64)
    val size = long("size")
    val visibility = enumerationByName("visibility", 16, FileVisibility::class)
    val owner = reference("owner", Users)
    val uploadToken = reference("upload_token", PersonalTokens).nullable()

    init {
        createdBy
        updatedBy
    }

    fun countAll(): Long = File.count()

    fun getAll(): List<File> = File.all().toList()

    fun getAll(page: Long, pageSize: Int): List<File> = File
        .all()
        .limit(pageSize, page * pageSize)
        .toList()

    fun findById(uuid: UUID): File? = File.findById(uuid)

    fun findByCode(code: String): File? = File.find { Files.code eq code }.firstOrNull()

    fun create(
        code: String,
        name: String,
        mimeType: String,
        size: Long,
        visibility: FileVisibility,
        creator: User,
        uploadToken: PersonalToken? = null,
    ): File = File.new {
        this.code = code
        this.name = name
        this.mimeType = mimeType
        this.size = size
        this.visibility = visibility
        this.owner = creator
        this.uploadToken = uploadToken

        onCreate(creator)
    }

    fun update(file: File, dto: FileUpdateDTO, updater: User): File = file.apply {
        dto.name?.let {
            name = it
            // TODO: Mime Type update is under discussion
            // mimeType = MimeTypes.typeFromFile(it)
        }
        dto.visibility?.let { visibility = it }

        onUpdate(updater)
    }

    fun delete(uuid: UUID): File? = File.findById(uuid)?.apply { delete() }
}

class File(id: EntityID<UUID>) : AuditableUUIDEntity(id, Files) {
    companion object : UUIDEntityClass<File>(Files)

    var code by Files.code
    var name by Files.name
    var mimeType by Files.mimeType
    var size by Files.size
    var visibility by Files.visibility
    var owner by User referencedOn Files.owner
    var uploadToken by PersonalToken optionalReferencedOn Files.uploadToken

    val ownerId get() = transaction { owner.uuid }
}
