package com.kamelia.hedera.rest.core.auditable

import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserTable
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp

abstract class AuditableUUIDTable(name: String) : UUIDTable(name) {

    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    open val createdBy: Column<EntityID<UUID>> by lazy { reference("created_by", UserTable) }
    val updatedAt = timestamp("updated_at").nullable()
    open val updatedBy: Column<EntityID<UUID>?> by lazy { reference("updated_by", UserTable).nullable() }
}

abstract class AuditableUUIDEntity(id: EntityID<UUID>, table: AuditableUUIDTable) : UUIDEntity(id) {

    @Suppress("unused") // automatically set, never touched
    var createdAt by table.createdAt
        private set

    var createdBy by User referencedOn table.createdBy
        private set

    var updatedAt by table.updatedAt
        private set

    var updatedBy by User optionalReferencedOn table.updatedBy
        private set

    fun onCreate(creator: User) {
        createdBy = creator
    }

    fun onUpdate(updater: User) {
        updatedBy = updater
        updatedAt = Instant.now()
    }
}
