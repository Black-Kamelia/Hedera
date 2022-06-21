package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.database.Connection
import com.kamelia.jellyfish.util.Hasher
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.exposedLogger

enum class UserRole {
    REGULAR,
    ADMIN,
    OWNER,
}

object Users : UUIDTable() {
    var email = varchar("email", 255).uniqueIndex()
    var username = varchar("username", 128).uniqueIndex()
    var password = varchar("password", 255)
    var role = enumerationByName("role", 32, UserRole::class)
    var enabled = bool("enabled")

    suspend fun getAll(): Iterable<User> = Connection.query {
        User.all()
    }

    suspend fun findByUsername(username: String): User? = Connection.query {
        User.find { Users.username eq username }.firstOrNull()
    }

    suspend fun findByEmail(email: String): User? = Connection.query {
        User.find { Users.email eq email }.firstOrNull()
    }

    suspend fun create(user: UserDTO): User = Connection.query {
        User.new {
            username = user.username
            email = user.email
            password = Hasher.hash(user.password)
            role = user.role
            enabled = false
        }.also {
            exposedLogger.info("Created user: $it")
        }
    }

    suspend fun read(id: UUID): User? = Connection.query {
        User.findById(id)
    }

    suspend fun update(id: UUID, user: UserUpdateDTO): User? = Connection.query {
        User.findById(id)?.apply {
            username = user.username
            email = user.email
            password = user.password
            role = user.role
            enabled = user.enabled
        }
    }

    suspend fun delete(id: UUID): User? = Connection.query {
        User.findById(id)?.apply { delete() }
    }
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

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
