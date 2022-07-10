package com.kamelia.jellyfish.rest.upload

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.jellyfish.rest.core.auditable.AuditableUUIDTable
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.UserDTO
import com.kamelia.jellyfish.rest.user.Users
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID

enum class UploadVisibility {
    PRIVATE,
    NON_LISTED,
    PUBLIC,
}

object Uploads : AuditableUUIDTable("uploads") {

    val code = varchar("code", 10).uniqueIndex()
    val name = varchar("name", 255)
    val mimeType = varchar("mime_type", 64)
    val size = long("size")
    val visibility = enumerationByName("visibility", 16, UploadVisibility::class)
    val owner = reference("owner", Users)

    suspend fun countAll(): Long = Connection.query {
        Upload.count()
    }

    suspend fun getAll(): List<Upload> = Connection.query {
        Upload.all()
            .toList()
    }

    suspend fun getAll(page: Long, pageSize: Int): List<Upload> = Connection.query {
        Upload.all()
            .limit(pageSize, page * pageSize)
            .toList()
    }

    suspend fun findById(uuid: UUID): Upload? = Connection.query {
        Upload.findById(uuid)
    }

    // TODO: not finished yet
    suspend fun create(user: UserDTO, creator: User): Upload = Connection.query {
        Upload.new {
            code = ""
            name = ""
            mimeType = ""
            size = 0
            visibility = UploadVisibility.PRIVATE
            owner = creator

            onCreate(creator)
        }
    }

    suspend fun update(upload: Upload, dto: UploadUpdateDTO): Upload = Connection.query {
        upload.apply {
            dto.name?.let { name = it }
            dto.visibility?.let { visibility = it }

            onUpdate(owner)
        }
    }
}

class Upload(id: EntityID<UUID>) : AuditableUUIDEntity(id, Uploads) {
    companion object : UUIDEntityClass<Upload>(Uploads)

    var code by Uploads.code
    var name by Uploads.name
    var mimeType by Uploads.mimeType
    var size by Uploads.size
    var visibility by Uploads.visibility
    var owner by User referencedOn Uploads.owner
}
