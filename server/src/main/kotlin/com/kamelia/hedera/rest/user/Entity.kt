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
import com.kamelia.hedera.rest.file.FileTable
import com.kamelia.hedera.rest.setting.UserSettings
import com.kamelia.hedera.rest.setting.UserSettingsTable
import com.kamelia.hedera.util.uuid
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

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

object UserTable : AuditableUUIDTable("users") {

    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 128).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName("role", 32, UserRole::class)
    val enabled = bool("enabled")
    val forceChangePassword = bool("force_change_password")
    val settings = reference("settings", UserSettingsTable)
    val currentDiskQuota = long("current_disk_quota")
    val maximumDiskQuota = long("maximum_disk_quota")

    override val createdBy = reference("created_by", this)
    override val updatedBy = reference("updated_by", this).nullable()

}

class User(id: EntityID<UUID>) : AuditableUUIDEntity(id, UserTable) {

    companion object : UUIDEntityClass<User>(UserTable) {

        fun findByUsername(username: String): User? = find { UserTable.username eq username }.firstOrNull()

        fun findByEmail(email: String): User? = find { UserTable.email eq email }.firstOrNull()

        fun search(
            page: Long,
            pageSize: Int,
            definition: PageDefinitionDTO
        ): Pair<List<User>, Long> = UserTable
            .selectAll()
            .applyFilters(definition.filters) {
                when (it.field) {
                    "username" -> UserTable.username.filter(it)
                    "email" -> UserTable.email.filter(it)
                    "role" -> UserTable.role.filter(it)
                    "enabled" -> UserTable.enabled.filter(it)
                    else -> throw UnknownFilterFieldException(it.field)
                }
            }.applySort(definition.sorter) {
                when (it) {
                    "username" -> UserTable.username
                    "email" -> UserTable.email
                    "role" -> UserTable.role
                    "enabled" -> UserTable.enabled
                    "createdAt" -> UserTable.createdAt
                    "currentDiskQuota" -> UserTable.currentDiskQuota
                    else -> throw UnknownFilterFieldException(it)
                }
            }.let {
                val rows = User.wrapRows(it)
                rows.limit(pageSize, page * pageSize).toList() to rows.count()
            }

        fun create(user: UserCreationDTO, creator: User? = null): User = new {
            username = user.username
            email = user.email
            password = Hasher.hash(user.password)
            role = user.role
            enabled = true
            forceChangePassword = user.forceChangePassword
            currentDiskQuota = 0
            maximumDiskQuota = user.diskQuota?.toLong() ?: -1
            settings = UserSettings.new {}

            onCreate(creator ?: this)
        }

    }

    var username by UserTable.username
    var email by UserTable.email
    var password by UserTable.password
    var role by UserTable.role
    var enabled by UserTable.enabled
    var forceChangePassword by UserTable.forceChangePassword
    var settings by UserSettings referencedOn UserTable.settings
    var currentDiskQuota by UserTable.currentDiskQuota
    var maximumDiskQuota by UserTable.maximumDiskQuota

    private val files by File referrersOn FileTable.owner

    fun getFiles(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO,
        asOwner: Boolean
    ): Pair<List<File>, Long> = FileTable
        .selectAll()
        .andWhere { FileTable.owner eq uuid }
        .apply { if (!asOwner) andWhere { FileTable.visibility eq FileVisibility.PUBLIC } }
        .applyFilters(definition.filters) {
            when (it.field) {
                "name" -> FileTable.name.filter(it)
                "mimeType" -> FileTable.mimeType.filter(it)
                "size" -> FileTable.size.filter(it)
                "visibility" -> FileTable.visibility.filter(it)
                "createdAt" -> FileTable.createdAt.filter(it)
                else -> throw UnknownFilterFieldException(it.field)
            }
        }
        .applySort(definition.sorter) {
            when (it) {
                "name" -> FileTable.name
                "mimeType" -> FileTable.mimeType
                "size" -> FileTable.size
                "visibility" -> FileTable.visibility
                "createdAt" -> FileTable.createdAt
                else -> throw UnknownSortFieldException(it)
            }
        }
        .limit(pageSize, page * pageSize)
        .let {
            val rows = File.wrapRows(it)
            rows.toList() to rows.count()
        }

    fun getFilesFormats(): List<String> = files
        .map { it.mimeType }
        .distinct()
        .sorted()

    suspend fun update(dto: UserUpdateDTO, updater: User = this): User = apply {
        dto.username?.let { username = it }
        dto.email?.let { email = it }
        dto.role?.let { role = it }

        SessionManager.updateSession(uuid, this)
        onUpdate(updater)
    }

    suspend fun updateStatus(enabled: Boolean, updater: User = this): User = apply {
        this.enabled = enabled

        SessionManager.updateSession(uuid, this)
        onUpdate(updater)
    }

    fun updatePassword(dto: UserPasswordUpdateDTO): User = apply {
        password = Hasher.hash(dto.newPassword)
        onUpdate(this)
    }

    suspend fun increaseCurrentDiskQuota(size: Long): User = apply {
        require(size >= 0)
        if (maximumDiskQuota >= 0) {
            require(size <= maximumDiskQuota - currentDiskQuota)
        }
        currentDiskQuota += size
        SessionManager.updateSession(uuid, this)
    }

    suspend fun decreaseCurrentDiskQuota(size: Long): User = apply {
        require(size >= 0)
        require(size <= currentDiskQuota)
        currentDiskQuota -= size
        SessionManager.updateSession(uuid, this)
    }

}
