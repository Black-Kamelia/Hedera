package com.kamelia.hedera.rest.file

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.token.PersonalTokenTable
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
    val uploadToken = reference("upload_token", PersonalTokenTable).nullable()

    init {
        createdBy
        updatedBy
    }

}

class File(id: EntityID<UUID>) : AuditableUUIDEntity(id, Files) {

    companion object : UUIDEntityClass<File>(Files) {

        fun findByCode(code: String): File? = find { Files.code eq code }.firstOrNull()

        fun create(
            code: String,
            name: String,
            mimeType: String,
            size: Long,
            visibility: FileVisibility,
            creator: User,
            uploadToken: PersonalToken? = null,
        ): File = new {
            this.code = code
            this.name = name
            this.mimeType = mimeType
            this.size = size
            this.visibility = visibility
            this.owner = creator
            this.uploadToken = uploadToken

            onCreate(creator)
        }

    }

    var code by Files.code
    var name by Files.name
    var mimeType by Files.mimeType
    var size by Files.size
    var visibility by Files.visibility
    var owner by User referencedOn Files.owner
    var uploadToken by PersonalToken optionalReferencedOn Files.uploadToken

    val ownerId get() = transaction { owner.uuid }

    fun update(dto: FileUpdateDTO, updater: User): File = apply {
        dto.name?.let { name = it }
        dto.visibility?.let { visibility = it }

        onUpdate(updater)
    }
}
