package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.uuid
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

object PersonalTokens : AuditableUUIDTable("upload_tokens") {

    val token = varchar("token", 32).uniqueIndex()
    val name = varchar("name", 255)
    val owner = reference("owner", Users)
    val deleted = bool("deleted")

    init {
        createdBy
        updatedBy
    }

    fun findById(id: UUID): PersonalToken? = PersonalToken.findById(id)


    fun create(
        name: String,
        owner: User,
    ): PersonalToken = PersonalToken.new {
        this.token = UUID.randomUUID().toString().replace("-", "")
        this.name = name
        this.owner = owner
        this.deleted = false

        onCreate(owner)
    }

}

class PersonalToken(id: EntityID<UUID>) : AuditableUUIDEntity(id, PersonalTokens) {
    companion object : UUIDEntityClass<PersonalToken>(PersonalTokens)

    var token by PersonalTokens.token
    var name by PersonalTokens.name
    var owner by User referencedOn PersonalTokens.owner
    var deleted by PersonalTokens.deleted

    val ownerId get() = transaction { owner.uuid }
}
