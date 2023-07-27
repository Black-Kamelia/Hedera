package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.file.Files
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.Users
import com.kamelia.hedera.util.uuid
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PersonalTokens : AuditableUUIDTable("personal_tokens") {

    val token = varchar("token", 32).uniqueIndex()
    val name = varchar("name", 255)
    val owner = reference("owner", Users)
    val deleted = bool("deleted")

    init {
        createdBy
        updatedBy
    }

    fun findById(id: UUID): PersonalToken? = PersonalToken.findById(id)

    fun findAllWithLastUsed(
        userId: UUID,
    ): List<Pair<PersonalToken, Instant?>> {
        val lastUsed = Files.createdAt.max().alias("lastUsed")
        return transaction {
            PersonalTokens
                .leftJoin(Files, { id }, { uploadToken })
                .slice(PersonalTokens.columns + lastUsed)
                .select { (owner eq userId) and (deleted eq false) }
                .groupBy(PersonalTokens.id)
                .sortedByDescending { it[PersonalTokens.createdAt] }
                .map { PersonalToken.wrapRow(it) to it.getOrNull(lastUsed) }
        }
    }

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

    fun delete(user: User): PersonalToken = apply {
        deleted = true
        onUpdate(user)
    }
}
