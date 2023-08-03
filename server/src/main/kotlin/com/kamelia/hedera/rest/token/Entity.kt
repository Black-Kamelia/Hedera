package com.kamelia.hedera.rest.token

import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.file.FileTable
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserTable
import com.kamelia.hedera.util.uuid
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PersonalTokenTable : AuditableUUIDTable("personal_tokens") {

    val token = varchar("token", 32).uniqueIndex()
    val name = varchar("name", 255)
    val owner = reference("owner", UserTable)
    val deleted = bool("deleted")

    init {
        createdBy
        updatedBy
    }

}

class PersonalToken(id: EntityID<UUID>) : AuditableUUIDEntity(id, PersonalTokenTable) {

    companion object : UUIDEntityClass<PersonalToken>(PersonalTokenTable) {

        fun findByToken(token: String): PersonalToken? = find { PersonalTokenTable.token eq token }.firstOrNull()

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

        fun allWithLastUsed(
            userId: UUID,
        ): List<Pair<PersonalToken, Instant?>> {
            val lastUsed = FileTable.createdAt.max().alias("lastUsed")
            return PersonalTokenTable
                .leftJoin(FileTable, { id }, { uploadToken })
                .slice(PersonalTokenTable.columns + lastUsed)
                .select { (PersonalTokenTable.owner eq userId) and (PersonalTokenTable.deleted eq false) }
                .groupBy(PersonalTokenTable.id)
                .orderBy(PersonalTokenTable.createdAt, SortOrder.DESC)
                .map { PersonalToken.wrapRow(it) to it.getOrNull(lastUsed) }
        }

    }

    var token by PersonalTokenTable.token
    var name by PersonalTokenTable.name
    var owner by User referencedOn PersonalTokenTable.owner
    var deleted by PersonalTokenTable.deleted

    val ownerId get() = transaction { owner.uuid }

    fun delete(user: User): PersonalToken = apply {
        deleted = true
        onUpdate(user)
    }
}
