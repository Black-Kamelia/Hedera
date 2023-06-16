package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.*
import com.kamelia.hedera.rest.core.pageable.PageDTO
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.util.uuid
import java.util.*
import kotlin.math.ceil

object UserService {

    suspend fun signup(dto: UserDTO): Response<UserRepresentationDTO, ErrorDTO> {
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

    suspend fun getUserById(id: UUID): Response<UserRepresentationDTO, ErrorDTO> {
        val user = Users.findById(id) ?: return Response.notFound()
        return Response.ok(user.toRepresentationDTO())
    }

    suspend fun getUsers(): Response<UserPageDTO, ErrorDTO> {
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

    suspend fun getUsers(page: Long, pageSize: Int, definition: PageDefinitionDTO): Response<UserPageDTO, String> {
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
    ): Response<UserRepresentationDTO, ErrorDTO> {
        val toEdit = Users.findById(id) ?: return Response.notFound()

        checkEmail(dto.email, toEdit)?.let { return it }
        checkUsername(dto.username, toEdit)?.let { return it }

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

        return Response.ok(Users
            .update(toEdit, dto, updater)
            .toRepresentationDTO())
    }

    suspend fun updateUserPassword(
        id: UUID,
        dto: UserPasswordUpdateDTO,
    ): Response<UserRepresentationDTO, ErrorDTO> {
        checkPassword(dto.newPassword)?.let { return it }

        val toEdit = Users.findById(id) ?: return Response.notFound()

        if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
            return Response.forbidden(Errors.Users.Password.INCORRECT_PASSWORD)
        }

        return Response.ok(
            Users.updatePassword(toEdit, dto)
                .toRepresentationDTO()
        )
    }

    suspend fun deleteUser(id: UUID): Response<UserRepresentationDTO, String> =
        Users.delete(id)
            ?.let { Response.ok(it.toRepresentationDTO()) }
            ?: Response.notFound()

    suspend fun regenerateUploadToken(id: UUID): Response<UserRepresentationDTO, String> {
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
                    Response.forbidden(Errors.Users.Email.ALREADY_EXISTS)
                }
            } ?: if ("@" !in email) {
            Response.badRequest(Errors.Users.Email.INVALID_EMAIL)
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
                    Response.forbidden(Errors.Users.Username.ALREADY_EXISTS)
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
        if (password.length < 8) Response.forbidden(Errors.Users.Password.TOO_SHORT)
        else null
    } else null
