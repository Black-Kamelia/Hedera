package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.UnknownFilterFieldException
import com.kamelia.hedera.core.UnknownSortFieldException
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDEntity
import com.kamelia.hedera.rest.core.auditable.AuditableUUIDTable
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.core.pageable.applyFilters
import com.kamelia.hedera.rest.core.pageable.applySort
import com.kamelia.hedera.rest.core.pageable.filter
import com.kamelia.hedera.rest.file.File
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.file.Files
import com.kamelia.hedera.util.uuid
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import java.util.*

enum class UserRole(private val power: Int) {
    REGULAR(1),
    ADMIN(10),
    OWNER(100),

    ;

    companion object {

        fun valueOfOrNull(value: String) = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    infix fun le(other: UserRole): Boolean = power <= other.power
    infix fun ge(other: UserRole): Boolean = power >= other.power
    infix fun gt(other: UserRole): Boolean = power > other.power
    infix fun lt(other: UserRole): Boolean = power < other.power
    infix fun eq(other: UserRole): Boolean = power == other.power
    infix fun ne(other: UserRole): Boolean = power != other.power
}

object Users : AuditableUUIDTable("users") {

    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 128).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName("role", 32, UserRole::class)
    val enabled = bool("enabled")
    val uploadToken = varchar("upload_token", 32).uniqueIndex()

    override val createdBy = reference("created_by", this)
    override val updatedBy = reference("updated_by", this).nullable()

    fun countAll(): Long = User.count()

    fun getAll(): List<User> = User.all().toList()

    fun getAll(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO
    ): Pair<List<User>, Long> = Users
        .selectAll()
        .applyFilters(definition.filters) {
            when (it.field) {
                username.name -> username.filter(it)
                email.name -> email.filter(it)
                role.name -> role.filter(it)
                enabled.name -> enabled.filter(it)
                else -> throw UnknownFilterFieldException(it.field)
            }
        }.applySort(definition.sorter) {
            when (it) {
                username.name -> username
                email.name -> email
                role.name -> role
                enabled.name -> enabled
                else -> throw UnknownFilterFieldException(it)
            }
        }.let {
            val rows = User.wrapRows(it)
            rows.limit(pageSize, page * pageSize).toList() to rows.count()
        }

    fun findById(uuid: UUID): User? = User.findById(uuid)

    fun findByUsername(username: String): User? = User
        .find { Users.username eq username }
        .firstOrNull()

    fun findByEmail(email: String): User? = User
        .find { Users.email eq email }
        .firstOrNull()

    fun findByUploadToken(uploadToken: String): User? = User
        .find { Users.uploadToken eq uploadToken }
        .firstOrNull()

    fun create(user: UserDTO, creator: User? = null): User = User.new {
        username = user.username
        email = user.email
        password = Hasher.hash(user.password)
        role = user.role
        enabled = false
        uploadToken = UUID.randomUUID().toString().replace("-", "")

        onCreate(creator ?: this)
    }

    suspend fun update(user: User, dto: UserUpdateDTO, updater: User): User = user.apply {
        dto.username?.let { username = it }
        dto.email?.let { email = it }
        dto.role?.let { role = it }
        dto.enabled?.let { enabled = it }

        SessionManager.updateSession(uuid, this)
        onUpdate(updater)
    }

    fun updatePassword(user: User, dto: UserPasswordUpdateDTO): User = user.apply {
        password = Hasher.hash(dto.newPassword)
        onUpdate(user)
    }

    fun delete(id: UUID): User? = User
        .findById(id)
        ?.apply { delete() }

    suspend fun regenerateUploadToken(user: User): User = user.apply {
        uploadToken = UUID.randomUUID().toString().replace("-", "")
        SessionManager.updateSession(uuid, this)
    }
}

class User(id: EntityID<UUID>) : AuditableUUIDEntity(id, Users) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.username
    var email by Users.email
    var password by Users.password
    var role by Users.role
    var enabled by Users.enabled
    var uploadToken by Users.uploadToken

    private val files by File referrersOn Files.owner

    fun countFiles(): Long = files.count()

    fun getFiles(): List<File> = files.toList()

    fun getFiles(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean
    ): Pair<List<File>, Long> = Files
        .selectAll()
        .andWhere { Files.owner eq uuid }
        .apply { if (!asOwner) andWhere { Files.visibility eq FileVisibility.PUBLIC } }
        .applyFilters(definition.filters) {
            when (it.field) {
                Files.name.name -> Files.name.filter(it)
                Files.mimeType.name -> Files.mimeType.filter(it)
                Files.size.name -> Files.size.filter(it)
                Files.visibility.name -> Files.visibility.filter(it)
                else -> throw UnknownFilterFieldException(it.field)
            }
        }.applySort(definition.sorter) {
            when (it) {
                Files.name.name -> Files.name
                Files.mimeType.name -> Files.mimeType
                Files.size.name -> Files.size
                Files.visibility.name -> Files.visibility
                else -> throw UnknownSortFieldException(it)
            }
        }.let {
            val rows = File.wrapRows(it)
            rows.limit(pageSize, page * pageSize).toList() to rows.count()
        }
}
