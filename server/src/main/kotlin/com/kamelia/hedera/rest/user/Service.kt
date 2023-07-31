package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.uuid
import java.util.*
import kotlin.math.ceil

private val USERNAME_REGEX = Regex("""^[a-z0-9_\-.]+$""")

object UserService {

    suspend fun signup(dto: UserDTO): Response<UserRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        checkEmail(dto.email)?.let { return@transaction it }
        checkUsername(dto.username)?.let { return@transaction it }
        checkPassword(dto.password)?.let { return@transaction it }

        if (dto.role != UserRole.REGULAR) {
            throw IllegalActionException()
        }

        Response.created(User
            .create(dto)
            .toRepresentationDTO())
    }

    suspend fun getUserById(id: UUID): Response<UserRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        val user = User.findById(id) ?: throw UserNotFoundException()
        Response.ok(user.toRepresentationDTO())
    }

    suspend fun getUsers(): Response<UserPageDTO, MessageKeyDTO> = Connection.transaction {
        val users = User.all()
        val total = User.count()
        Response.ok(UserPageDTO(
            PageDTO(
                users.map { it.toRepresentationDTO() },
                0,
                -1,
                1,
                total
            )
        ))
    }

    suspend fun getUsers(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO
    ): Response<UserPageDTO, String> = Connection.transaction {
        val (users, total) = User.search(page, pageSize, definition)
        Response.ok(UserPageDTO(
            PageDTO(
                users.map { it.toRepresentationDTO() },
                page,
                pageSize,
                ceil(total / pageSize.toDouble()).toLong(),
                total
            )
        ))
    }

    suspend fun updateUser(
        id: UUID,
        dto: UserUpdateDTO,
        updaterID: UUID,
    ): Response<UserRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        val toEdit = User.findById(id) ?: throw UserNotFoundException()

        checkEmail(dto.email, toEdit)?.let { return@transaction it }
        checkUsername(dto.username, toEdit)?.let { return@transaction it }

        val updater = User[updaterID]

        if ((dto.role != null || dto.enabled != null) && updater.uuid == toEdit.uuid) { // can't self update role or state
            throw IllegalActionException()
        }

        if (
            (dto.role != null && (dto.role ge updater.role || toEdit.role ge updater.role)) || // can't change role to higher or equal if updater is lower or equal
            (dto.enabled != null && (toEdit.role ge updater.role)) // can't change enabled if updater is lower or equal
        ) {
            throw InsufficientPermissionsException()
        }

        Response.ok(toEdit
            .update(dto, updater)
            .toRepresentationDTO())
    }

    suspend fun updateUserPassword(
        id: UUID,
        dto: UserPasswordUpdateDTO,
    ): Response<UserRepresentationDTO, MessageKeyDTO> = Connection.transaction {
        checkPassword(dto.newPassword)?.let { return@transaction it }

        val toEdit = User.findById(id) ?: throw UserNotFoundException()

        if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
            return@transaction Response.forbidden(Errors.Users.Password.INCORRECT_PASSWORD)
        }

        Response.ok(toEdit
            .updatePassword(dto)
            .toRepresentationDTO())
    }

    suspend fun deleteUser(id: UUID): Response<UserRepresentationDTO, String> = Connection.transaction {
        val toDelete = User.findById(id) ?: throw UserNotFoundException()

        toDelete.delete()
        Response.ok(toDelete.toRepresentationDTO())
    }
}

private fun checkEmail(email: String?, toEdit: User? = null): Response<Nothing, MessageKeyDTO>? = when {
    email == null -> null
    "@" !in email -> Response.badRequest(Errors.Users.Email.INVALID_EMAIL)
    else -> User.findByEmail(email)?.let {
        if (it.uuid == toEdit?.uuid) {
            null
        } else {
            Response.forbidden(Errors.Users.Email.ALREADY_EXISTS)
        }
    }
}

private fun checkUsername(username: String?, toEdit: User? = null): Response<Nothing, MessageKeyDTO>? = when {
    username == null -> null
    !USERNAME_REGEX.matches(username) -> Response.badRequest(Errors.Users.Username.INVALID_USERNAME)
    else -> User.findByUsername(username)?.let {
        if (it.uuid == toEdit?.uuid) {
            null
        } else {
            Response.forbidden(Errors.Users.Username.ALREADY_EXISTS)
        }
    }
}

private fun checkPassword(password: String?): Response<Nothing, MessageKeyDTO>? = when {
    password == null -> null
    password.length < 8 -> Response.forbidden(Errors.Users.Password.TOO_SHORT)
    password.length > 128 -> Response.forbidden(Errors.Users.Password.TOO_LONG)
    else -> null
}
