package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.jellyfish.rest.core.auditable.AuditableUUIDEntityClass
import com.kamelia.jellyfish.rest.core.auditable.AuditableUUIDTable
import com.kamelia.jellyfish.util.Hasher
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.exposedLogger

enum class UserRole {
    REGULAR,
    ADMIN,
    OWNER,
}

object Users : AuditableUUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 128).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName("role", 32, UserRole::class)
    val enabled = bool("enabled")

    override val createdBy = reference("created_by", this)
    override val updatedBy = reference("updated_by", this).nullable()

    suspend fun getAll(): Iterable<User> = Connection.query {
        User.all()
    }

    suspend fun findById(uuid: UUID): User? = Connection.query {
        User.findById(uuid)
    }

    suspend fun findByUsername(username: String): User? = Connection.query {
        User.find { Users.username eq username }.firstOrNull()
    }

    suspend fun findByEmail(email: String): User? = Connection.query {
        User.find { Users.email eq email }.firstOrNull()
    }

    suspend fun create(user: UserDTO, creator: User? = null): User = Connection.query {
        User.new {
            username = user.username
            email = user.email
            password = Hasher.hash(user.password)
            role = user.role
            enabled = false

            onCreate(creator ?: this)
        }.also {
            exposedLogger.info("Created user: $it")
        }
    }

    suspend fun update(id: UUID, user: UserUpdateDTO, updater: User? = null): User? = Connection.query {
        User.findById(id)?.apply {
            username = user.username
            email = user.email
            password = user.password
            role = user.role
            enabled = user.enabled

            onUpdate(updater ?: this)
        }
    }

    suspend fun delete(id: UUID): User? = Connection.query {
        User.findById(id)?.apply { delete() }
    }
}

class User(id: EntityID<UUID>) : AuditableUUIDEntity(id, Users) {
    companion object : AuditableUUIDEntityClass<User>(Users)

    var username by Users.username
    var email by Users.email
    var password by Users.password
    var role by Users.role
    var enabled by Users.enabled

    fun toDTO(): UserResponseDTO {
        return UserResponseDTO(
            id.value,
            username,
            email,
            role,
            enabled
        )
    }
}
