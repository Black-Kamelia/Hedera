package com.kamelia.hedera.rest.file

import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.MimeTypes
import com.kamelia.hedera.util.uuid
import java.util.UUID
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
}

object Files : AuditableUUIDTable("files") {

    val code = varchar("code", 10).uniqueIndex()
    val name = varchar("name", 255)
    val mimeType = varchar("mime_type", 64)
    val size = long("size")
    val visibility = enumerationByName("visibility", 16, FileVisibility::class)
    val owner = reference("owner", Users)

    init {
        createdBy
        updatedBy
    }

    suspend fun countAll(): Long = Connection.transaction {
        File.count()
    }

    suspend fun getAll(): List<File> = Connection.transaction {
        File.all()
            .toList()
    }

    suspend fun getAll(page: Long, pageSize: Int): List<File> = Connection.transaction {
        File.all()
            .limit(pageSize, page * pageSize)
            .toList()
    }

    suspend fun findById(uuid: UUID): File? = Connection.transaction {
        File.findById(uuid)
    }

    suspend fun findByCode(code: String): File? = Connection.transaction {
        File.find { Files.code eq code }.firstOrNull()
    }

    suspend fun create(
        code: String,
        name: String,
        mimeType: String,
        size: Long,
        creator: User,
    ): File = Connection.transaction {
        File.new {
            this.code = code
            this.name = name
            this.mimeType = mimeType
            this.size = size
            this.visibility = FileVisibility.PRIVATE
            this.owner = creator

            onCreate(creator)
        }
    }

    suspend fun update(file: File, dto: FileUpdateDTO, updater: User): File = Connection.transaction {
        file.apply {
            dto.name?.let {
                name = it
                mimeType = MimeTypes.typeFromFile(it)
            }
            dto.visibility?.let { visibility = it }

            onUpdate(updater)
        }
    }

    suspend fun delete(uuid: UUID): File? = Connection.transaction {
        File.findById(uuid)?.apply { delete() }
    }
}

class File(id: EntityID<UUID>) : AuditableUUIDEntity(id, Files) {
    companion object : UUIDEntityClass<File>(Files)

    var code by Files.code
    var name by Files.name
    var mimeType by Files.mimeType
    var size by Files.size
    var visibility by Files.visibility
    var owner by User referencedOn Files.owner

    val ownerId get() = transaction { owner.uuid }
}
