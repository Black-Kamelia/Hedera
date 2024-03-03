package com.kamelia.hedera.rest.token

import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.rest.file.FileTable
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.rest.user.UserTable
import com.kamelia.hedera.util.uuid
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PersonalTokenTable : UUIDTable("personal_tokens") {

    val token = varchar("token", 60).uniqueIndex()
    val name = varchar("name", 255)
    val owner = reference("owner", UserTable)
    val deleted = bool("deleted")
    val createdAt = timestamp("created_at")

}

class PersonalToken(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<PersonalToken>(PersonalTokenTable) {

        /**
         * @return the unencrypted token and the entity
         */
        fun create(
            name: String,
            owner: User,
        ): Pair<String, PersonalToken> {
            val token = UUID.randomUUID().toString().replace("-", "")
            return token to PersonalToken.new {
                this.token = Hasher.hash(token)
                this.name = name
                this.owner = owner
                this.deleted = false
                this.createdAt = Instant.now()
            }
        }

        fun allWithLastUsed(
            userId: UUID,
        ): List<Pair<PersonalToken, Instant?>> {
            val lastUsed = FileTable.createdAt.max()
            return PersonalTokenTable
                .leftJoin(FileTable, { id }, { uploadToken })
                .slice(PersonalTokenTable.columns + lastUsed)
                .select { (PersonalTokenTable.owner eq userId) and (PersonalTokenTable.deleted eq false) }
                .groupBy(PersonalTokenTable.id)
                .orderBy(PersonalTokenTable.createdAt, SortOrder.DESC)
                .map { PersonalToken.wrapRow(it) to it.getOrNull(lastUsed) }
        }

        fun allWithUsage(
            userId: UUID
        ): List<Pair<PersonalToken, Long>> {
            val usage = FileTable.id.count()
            return PersonalTokenTable
                .leftJoin(FileTable, { id }, { uploadToken })
                .slice(PersonalTokenTable.columns + usage)
                .select { (PersonalTokenTable.owner eq userId) }
                .groupBy(PersonalTokenTable.id)
                .orderBy(usage, SortOrder.DESC)
                .map { PersonalToken.wrapRow(it) to it[usage] }
        }

    }

    var token by PersonalTokenTable.token
    var name by PersonalTokenTable.name
    var owner by User referencedOn PersonalTokenTable.owner
    var deleted by PersonalTokenTable.deleted
    var createdAt by PersonalTokenTable.createdAt

    val ownerId get() = readValues[PersonalTokenTable.owner].value

    override fun delete() {
        token = "deleted_${id.value}"
        deleted = true
    }
}
