package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.ActionResponse
import com.kamelia.hedera.core.Actions
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.Hasher
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.core.ValidationScope
import com.kamelia.hedera.core.asMessage
import com.kamelia.hedera.core.validate
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.uuid
import io.ktor.http.*
import java.util.*
import kotlin.math.ceil

private val USERNAME_REGEX = Regex("""^[a-z0-9_\-.]+$""")

object UserService {

    suspend fun signup(dto: UserCreationDTO): Response<UserRepresentationDTO> = Connection.transaction {
        validate {
            checkEmail(dto.email)
            checkUsername(dto.username)
            checkPassword(dto.password)

            if (dto.role != UserRole.REGULAR) {
                throw IllegalActionException()
            }

            catchErrors()

            val user = User.create(dto)
            Response.created(user.toRepresentationDTO())
        }
    }

    suspend fun createUser(
        dto: UserCreationDTO
    ): ActionResponse<UserRepresentationDTO> = Connection.transaction {
        validate {
            checkEmail(dto.email)
            checkUsername(dto.username)
            checkPassword(dto.password)

            if (dto.role == UserRole.OWNER) {
                throw IllegalActionException()
            }

            catchErrors()

            val user = User.create(dto)
            ActionResponse.created(
                title = Actions.Users.Create.Success.TITLE.asMessage(),
                message = Actions.Users.Create.Success.MESSAGE.asMessage("username" to user.username),
                payload = user.toRepresentationDTO(),
            )
        }
    }

    suspend fun getUserById(id: UUID): Response<UserRepresentationDTO> = Connection.transaction {
        val user = User.findById(id) ?: throw UserNotFoundException()
        Response.ok(user.toRepresentationDTO())
    }

    suspend fun getUsers(
        page: Long,
        pageSize: Int,
        definition: PageDefinitionDTO
    ): Response<UserPageDTO> = Connection.transaction {
        val (users, total) = User.search(page, pageSize, definition)
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
    ): ActionResponse<UserRepresentationDTO> = Connection.transaction {
        validate {
            val toEdit = User.findById(id) ?: throw UserNotFoundException()

            checkEmail(dto.email, toEdit)
            checkUsername(dto.username, toEdit)

            val updater = User[updaterID]

            // Prevent updating self state
            if ((dto.enabled != null) && updater.id == toEdit.id) {
                throw IllegalActionException()
            }

            // Prevent changing self role
            if (dto.role != null && toEdit.id == updater.id && dto.role != toEdit.role) {
                throw IllegalActionException()
            }

            // Prevent changing the role of someone else if not strictly superior
            if (dto.role != null && toEdit.id != updater.id && (dto.role ge updater.role || toEdit.role ge updater.role)) {
                throw InsufficientPermissionsException()
            }

            catchErrors()

            toEdit.update(dto, updater)
            ActionResponse.ok(
                title = Actions.Users.Update.Success.TITLE.asMessage(),
                message = Actions.Users.Update.Success.MESSAGE.asMessage("username" to toEdit.username),
                payload = toEdit.toRepresentationDTO(),
            )
        }
    }

    suspend fun updateUserStatus(
        id: UUID,
        enable: Boolean,
        updaterID: UUID,
    ): ActionResponse<Nothing> = Connection.transaction {
        val toEdit = User.findById(id) ?: throw UserNotFoundException()
        val updater = User[updaterID]

        if (toEdit.role ge updater.role) {
            throw InsufficientPermissionsException()
        }

        toEdit.updateStatus(enable, updater)

        if (enable) {
            ActionResponse.ok(
                title = MessageKeyDTO(Actions.Users.Activate.Success.TITLE),
                message = Actions.Users.Activate.Success.MESSAGE.asMessage("username" to toEdit.username),
            )
        } else {
            ActionResponse.ok(
                title = MessageKeyDTO(Actions.Users.Deactivate.Success.TITLE),
                message = Actions.Users.Deactivate.Success.MESSAGE.asMessage("username" to toEdit.username),
            )
        }
    }

    suspend fun updateUserPassword(
        id: UUID,
        dto: UserPasswordUpdateDTO,
    ): ActionResponse<UserRepresentationDTO> = Connection.transaction {
        validate(statusCode = HttpStatusCode.Forbidden) {
            checkPassword(dto.newPassword)

            val toEdit = User.findById(id) ?: throw UserNotFoundException()

            if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
                raiseError("oldPassword", Errors.Users.Password.INCORRECT_PASSWORD)
            }

            catchErrors()

            toEdit.updatePassword(dto)
            ActionResponse.ok(
                title = Actions.Users.UpdatePassword.Success.TITLE.asMessage(),
                payload = toEdit.toRepresentationDTO()
            )
        }
    }

    suspend fun deleteUser(
        id: UUID
    ): ActionResponse<UserRepresentationDTO> = Connection.transaction {
        val toDelete = User.findById(id) ?: throw UserNotFoundException()

        toDelete.delete()
        ActionResponse.ok(
            title = Actions.Users.Delete.Success.TITLE.asMessage(),
            message = Actions.Users.Delete.Success.MESSAGE.asMessage("username" to toDelete.username),
            payload = toDelete.toRepresentationDTO()
        )
    }
}

private fun ValidationScope.checkEmail(email: String?, toEdit: User? = null): Unit? = when {
    email == null -> null
    "@" !in email -> raiseError("email", Errors.Users.Email.INVALID_EMAIL)
    else -> User.findByEmail(email)?.let {
        if (it.uuid != toEdit?.uuid) {
            raiseError("email", Errors.Users.Email.ALREADY_EXISTS)
        }
    }
}

private fun ValidationScope.checkUsername(username: String?, toEdit: User? = null): Unit? = when {
    username == null -> null
    !USERNAME_REGEX.matches(username) -> raiseError("username", Errors.Users.Username.INVALID_USERNAME)
    else -> User.findByUsername(username)?.let {
        if (it.uuid != toEdit?.uuid) {
            raiseError("username", Errors.Users.Username.ALREADY_EXISTS)
        }
    }
}

private fun ValidationScope.checkPassword(password: String?): Unit? = when {
    password == null -> null
    password.length < 8 -> raiseError("password", Errors.Users.Password.TOO_SHORT)
    password.length > 128 -> raiseError("password", Errors.Users.Password.TOO_LONG)
    else -> null
}
