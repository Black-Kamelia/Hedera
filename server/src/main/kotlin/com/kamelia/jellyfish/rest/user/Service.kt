package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.core.ErrorDTO
import com.kamelia.jellyfish.core.Hasher
import com.kamelia.jellyfish.core.IllegalActionException
import com.kamelia.jellyfish.core.InsufficientPermissionsException
import com.kamelia.jellyfish.core.Response
import com.kamelia.jellyfish.rest.core.pageable.PageDTO
import com.kamelia.jellyfish.rest.core.pageable.PageDefinitionDTO
import com.kamelia.jellyfish.util.uuid
import java.util.UUID
import kotlin.math.ceil

object UserService {

    suspend fun signup(dto: UserDTO): Response<UserRepresentationDTO, List<ErrorDTO>> {
        checkEmail(dto.email)?.let { return it }
        checkUsername(dto.username)?.let { return it }
        checkPassword(dto.password)?.let { return it }

        if (dto.role != UserRole.REGULAR) {
            throw IllegalActionException()
        }

        return Response.ok(
            Users.create(dto)
                .toRepresentationDTO()
        )
    }

    suspend fun getUserById(id: UUID): Response<UserRepresentationDTO, List<ErrorDTO>> {
        val user = Users.findById(id) ?: return Response.notFound()
        return Response.ok(user.toRepresentationDTO())
    }

    suspend fun getUsers(): Response<UserPageDTO, List<ErrorDTO>> {
        val users = Users.getAll()
        val total = Users.countAll()
        return Response.ok(
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

    suspend fun getUsers(page: Long, pageSize: Int, definition: PageDefinitionDTO): Response<UserPageDTO, List<ErrorDTO>> {
        val (users, total) = Users.getAll(page, pageSize, definition)
        return Response.ok(
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
    ): Response<UserRepresentationDTO, List<ErrorDTO>> {
        val toEdit = Users.findById(id) ?: return Response.notFound()

        checkEmail(dto.email, toEdit)?.let { return it }
        checkUsername(dto.username, toEdit)?.let { return it }

        val updater = Users.findById(updaterID)
        if (
            updater == null ||
            (dto.role != null && (dto.role ge updater.role || toEdit.role ge updater.role))
        ) {
            throw InsufficientPermissionsException()
        }

        return Response.ok(
            Users.update(toEdit, dto, updater)
                .toRepresentationDTO()
        )
    }

    suspend fun updateUserPassword(
        id: UUID,
        dto: UserPasswordUpdateDTO,
        updaterID: UUID,
    ): Response<UserRepresentationDTO, List<ErrorDTO>> {
        checkPassword(dto.newPassword)?.let { return it }

        val toEdit = Users.findById(id) ?: return Response.notFound()

        if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
            return Response.forbidden("errors.users.password.wrong")
        }

        val updater = Users.findById(updaterID) ?: return Response.forbidden("errors.users.role.forbidden")
        return Response.ok(
            Users.updatePassword(toEdit, dto, updater)
                .toRepresentationDTO()
        )
    }

    suspend fun deleteUser(id: UUID): Response<UserRepresentationDTO, Nothing> =
        Users.delete(id)
            ?.let { Response.ok(it.toRepresentationDTO()) }
            ?: Response.notFound()

    suspend fun regenerateUploadToken(id: UUID): Response<UserRepresentationDTO, List<ErrorDTO>> {
        val user = Users.findById(id) ?: return Response.notFound()
        return Response.ok(
            Users.regenerateUploadToken(user)
                .toRepresentationDTO()
        )
    }
}

/**
 * Fetches given email to check if it is already in use and is valid.
 * If a user is provided, checks that hypothetical found user is different.
 *
 * @param email Email to lookup
 * @param toEdit Optional user to check against.
 * This parameter should represent the user that is currently logged in.
 *
 * @return Optional [Response] with [ErrorDTO] if error occurred
 */
private suspend fun checkEmail(email: String?, toEdit: User? = null) =
    if (email != null)
        Users.findByEmail(email)
            ?.let {
                if (it.uuid == toEdit?.uuid) {
                    null
                } else {
                    Response.forbidden("errors.users.email.already_exists")
                }
            } ?: if ("@" !in email) {
            Response.badRequest("errors.users.email.invalid")
        } else null
    else null

/**
 * Fetches given username to check if it is already in use.
 * If a user is provided, checks that hypothetical found user is different.
 *
 * @param username Username to lookup
 * @param toEdit Optional user to check against.
 * This parameter should represent the user that is currently logged in.
 *
 * @return Optional [Response] with [ErrorDTO] if error occurred
 */
private suspend fun checkUsername(username: String?, toEdit: User? = null) =
    if (username != null)
        Users.findByUsername(username)
            ?.let {
                if (it.uuid == toEdit?.uuid) {
                    null
                } else {
                    Response.forbidden("errors.users.username.already_exists")
                }
            }
    else null

/**
 * Checks if given password is valid.
 * The requirements are:
 * - at least 8 characters long
 * - at least one digit
 * - at least one letter
 * - at least one special character
 * - at least one uppercase letter
 * - at least one lowercase letter
 *
 * @param password Password to check
 *
 * @return Optional [Response] with [ErrorDTO] if error occurred
 */
private fun checkPassword(password: String?) =
    if (password != null) {
        val errors = mutableListOf<String>()
        if (password.length < 8) errors += "errors.users.password.too_short"
        if (password == password.lowercase()) errors += "errors.users.password.no_uppercase"
        if (password == password.uppercase()) errors += "errors.users.password.no_lowercase"
        if (password.none(Char::isDigit)) errors += "errors.users.password.no_digit"
        if (password.none(Char::isLetter)) errors += "errors.users.password.no_letter"
        if (password.all(Char::isLetterOrDigit)) errors += "errors.users.password.no_special"

        if (errors.isEmpty()) {
            null
        } else {
            Response.forbidden(*errors.toTypedArray())
        }
    } else null
