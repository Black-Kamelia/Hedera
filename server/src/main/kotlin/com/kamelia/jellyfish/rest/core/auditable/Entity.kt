package com.kamelia.jellyfish.rest.core.auditable

import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.rest.user.Users
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.toEntity
import org.jetbrains.exposed.sql.javatime.timestamp

abstract class AuditableUUIDTable(name: String) : UUIDTable(name) {
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    //val createdBy = reference("created_by", Users)
    val updatedAt = timestamp("updated_at").clientDefault { Instant.now() }
    //val updatedBy = reference("updated_by", Users)
}

abstract class AuditableUUIDEntity(id: EntityID<UUID>, table: AuditableUUIDTable) : UUIDEntity(id) {
    var createdAt by table.createdAt
    //var createdBy by User referencedOn table.createdBy
    var updatedAt by table.updatedAt
    //var updatedBy by User referencedOn table.updatedBy

    fun onCreate(creator: User) {
//        createdBy = creator
//        updatedBy = creator
    }

    fun onUpdate(updater: User) {
        //updatedBy = updater
    }
}

abstract class AuditableUUIDEntityClass<E : AuditableUUIDEntity>(
    table: AuditableUUIDTable
) : UUIDEntityClass<E>(table) {

    init {
        EntityHook.subscribe { change ->
            if (change.changeType != EntityChangeType.Updated) return@subscribe
            change.toEntity(this)?.updatedAt = Instant.now()
        }
    }
}
