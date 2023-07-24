package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.MessageDTO
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

    suspend fun signup(dto: UserDTO): Response<UserRepresentationDTO, Nothing> = Connection.transaction {
        checkEmail(dto.email)?.let { return@transaction it }
        checkUsername(dto.username)?.let { return@transaction it }
        checkPassword(dto.password)?.let { return@transaction it }

        if (dto.role != UserRole.REGULAR) {
            throw IllegalActionException()
        }

        Response.created(
            Users
                .create(dto)
                .toRepresentationDTO()
        )
    }

    suspend fun getUserById(id: UUID): Response<UserRepresentationDTO, Nothing> = Connection.transaction {
        val user = Users.findById(id) ?: return@transaction Response.notFound()
        Response.ok(user.toRepresentationDTO())
    }

    suspend fun getUsers(): Response<UserPageDTO, Nothing> = Connection.transaction {
        val users = Users.getAll()
        val total = Users.countAll()

        Response.ok(
            UserPageDTO(
                PageDTO(
                    users.map { it.toRepresentationDTO() },
                    0,
                    -1,
                    1,
                    total
                )
            )
        )
    }

    suspend fun getUsers(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO
    ): Response<UserPageDTO, Nothing> = Connection.transaction {
        val (users, total) = Users.getAll(page, pageSize, definition)

        Response.ok(
            UserPageDTO(
                PageDTO(
                    users.map { it.toRepresentationDTO() },
                    page,
                    pageSize,
                    ceil(total / pageSize.toDouble()).toLong(),
                    total
                )
            )
        )
    }

    suspend fun updateUser(
        id: UUID,
        dto: UserUpdateDTO,
        updaterID: UUID,
    ): Response<UserRepresentationDTO, Nothing> = Connection.transaction {
        val toEdit = Users.findById(id) ?: throw UserNotFoundException()

        checkEmail(dto.email, toEdit)?.let { return@transaction it }
        checkUsername(dto.username, toEdit)?.let { return@transaction it }

        val updater = Users.findById(updaterID) ?: throw InsufficientPermissionsException()

        if ((dto.role != null || dto.enabled != null) && updater.uuid == toEdit.uuid) { // can't self update role or state
            throw IllegalActionException()
        }

        if (
            (dto.role != null && (dto.role ge updater.role || toEdit.role ge updater.role)) || // can't change role to higher or equal if updater is lower or equal
            (dto.enabled != null && (toEdit.role ge updater.role)) // can't change enabled if updater is lower or equal
        ) {
            throw InsufficientPermissionsException()
        }

        Response.ok(
            Users
                .update(toEdit, dto, updater)
                .toRepresentationDTO()
        )
    }

    suspend fun updateUserPassword(
        id: UUID,
        dto: UserPasswordUpdateDTO,
    ): Response<UserRepresentationDTO, Nothing> = Connection.transaction {
        val toEdit = Users.findById(id) ?: throw UserNotFoundException()

        checkPassword(dto.newPassword)?.let { return@transaction it }

        if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
            return@transaction Response.forbidden(
                MessageDTO.Simple(
                    title = Actions.Users.Update.Password.Failure.TITLE,
                    message = Errors.Users.Password.INCORRECT_PASSWORD
                )
            )
        }

        val newUserState = Users
            .updatePassword(toEdit, dto)
            .toRepresentationDTO()

        Response.ok(
            MessageDTO.Payload(
                payload = newUserState,
                title = Actions.Users.Update.Password.Success.MESSAGE
            )
        )
    }

    suspend fun deleteUser(id: UUID): Response<UserRepresentationDTO, Nothing> = Connection.transaction {
        val deletedUser = Users.delete(id) ?: throw UserNotFoundException()

        Response.ok(deletedUser.toRepresentationDTO())
    }

    // suspend fun regenerateUploadToken(id: UUID): Response<UserRepresentationDTO, String> = Connection.transaction {
    //     val user = Users.findById(id) ?: return@transaction Response.notFound()
    //     Response.created(Users
    //         .regenerateUploadToken(user)
    //         .toRepresentationDTO())
    // }
}

private fun checkEmail(email: String?, toEdit: User? = null): Response<Nothing, Nothing>? = when {
    email == null -> null
    "@" !in email -> Response.badRequest(Errors.Users.Email.INVALID_EMAIL)
    else -> Users.findByEmail(email)?.let {
        if (it.uuid == toEdit?.uuid) {
            null
        } else {
            Response.forbidden(Errors.Users.Email.ALREADY_EXISTS)
        }
    }
}

private fun checkUsername(username: String?, toEdit: User? = null): Response<Nothing, Nothing>? = when {
    username == null -> null
    !USERNAME_REGEX.matches(username) -> Response.badRequest(Errors.Users.Username.INVALID_USERNAME)
    else -> Users.findByUsername(username)?.let {
        if (it.uuid == toEdit?.uuid) {
            null
        } else {
            Response.forbidden(Errors.Users.Username.ALREADY_EXISTS)
        }
    }
}

private fun checkPassword(password: String?): Response<Nothing, Nothing>? = when {
    password == null -> null
    password.length < 8 -> Response.forbidden(Errors.Users.Password.TOO_SHORT)
    else -> null
}
