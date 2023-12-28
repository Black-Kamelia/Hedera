package com.kamelia.hedera.rest.file

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.token.PersonalToken
import com.kamelia.hedera.rest.token.PersonalTokenTable
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserTable
import com.kamelia.hedera.util.uuid
import io.trbl.blurhash.BlurHash
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
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

object FileTable : AuditableUUIDTable("files") {

    val code = varchar("code", 10).uniqueIndex()
    val name = varchar("name", 255)
    val mimeType = varchar("mime_type", 64)
    val size = long("size")
    val blurhash = varchar("blurhash", 40).nullable()
    val visibility = enumerationByName("visibility", 16, FileVisibility::class)
    val owner = reference("owner", UserTable)
    val uploadToken = reference("upload_token", PersonalTokenTable).nullable()
    val customLink = varchar("custom_link", 255).nullable()

    init {
        createdBy
        updatedBy
    }

}

class File(id: EntityID<UUID>) : AuditableUUIDEntity(id, FileTable) {

    companion object : UUIDEntityClass<File>(FileTable) {

        fun findByCode(code: String): File? = find { FileTable.code eq code }.firstOrNull()

        fun findByCustomLink(customLink: String): File? = find { FileTable.customLink eq customLink }.firstOrNull()

        fun findByCustomLinkWithOwner(customLink: String): Pair<File, User>? {
            return FileTable.leftJoin(UserTable, { owner }, { id })
                .select { FileTable.customLink eq customLink }
                .firstOrNull()
                ?.let {
                    File.wrapRow(it) to User.wrapRow(it)
                }
        }

        fun findByCodeWithOwner(code: String): Pair<File, User>? {
            return FileTable.leftJoin(UserTable, { owner }, { id })
                .select { FileTable.code eq code }
                .firstOrNull()
                ?.let {
                    File.wrapRow(it) to User.wrapRow(it)
                }
        }

        fun create(
            code: String,
            name: String,
            mimeType: String,
            size: Long,
            blurhash: String? = null,
            visibility: FileVisibility,
            creator: User,
            uploadToken: PersonalToken? = null,
        ): File = new {
            this.code = code
            this.name = name
            this.mimeType = mimeType
            this.size = size
            this.blurhash = blurhash
            this.visibility = visibility
            this.owner = creator
            this.uploadToken = uploadToken

            onCreate(creator)
        }

    }

    var code by FileTable.code
    var name by FileTable.name
    var mimeType by FileTable.mimeType
    var size by FileTable.size
    var blurhash by FileTable.blurhash
    var visibility by FileTable.visibility
    var owner by User referencedOn FileTable.owner
    var uploadToken by PersonalToken optionalReferencedOn FileTable.uploadToken
    var customLink by FileTable.customLink

    val ownerId get() = transaction { owner.uuid }

    fun update(dto: FileUpdateDTO, updater: User): File = apply {
        dto.name?.let { name = it }
        dto.visibility?.let { visibility = it }
        dto.customLink?.let { customLink = it.ifEmpty { null } }

        onUpdate(updater)
    }
}
