package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.ErrorDTO
import com.kamelia.jellyfish.util.Hasher
import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.uuid
import java.util.*

object UserService {

    suspend fun signup(dto: UserDTO): QueryResult<UserResponseDTO, List<ErrorDTO>> {
        checkEmail(dto.email)?.let { return it }
        checkUsername(dto.username)?.let { return it }

        if (dto.role != UserRole.REGULAR) {
            return QueryResult.forbidden("errors.users.role.forbidden")
        }

        // TODO: check if email is valid, password valid, role elevation, etc

        return QueryResult.ok(Users.create(dto).toDTO())
    }

    suspend fun updateUser(id: UUID, dto: UserUpdateDTO): QueryResult<UserResponseDTO, List<ErrorDTO>> {
        val toEdit = Users.findById(id) ?: return QueryResult.notFound()

        checkEmail(dto.email, toEdit)?.let { return it }
        checkUsername(dto.username, toEdit)?.let { return it }

        // TODO: check if email is valid, password valid, role elevation, etc
        val updater: User? = null // get from user from header Authentication
        return QueryResult.ok(Users.update(toEdit, dto, updater).toDTO())
    }

    suspend fun updateUserPassword(id: UUID, dto: UserPasswordUpdateDTO): QueryResult<UserResponseDTO, List<ErrorDTO>> {
        val toEdit = Users.findById(id) ?: return QueryResult.notFound()

        if (!Hasher.verify(dto.oldPassword, toEdit.password).verified) {
            return QueryResult.forbidden("errors.users.password.wrong")
        }

        // TODO: check if email is valid, password valid, role elevation, etc
        val updater: User? = null // get from user from header Authentication
        return QueryResult.ok(Users.updatePassword(toEdit, dto, updater).toDTO())
    }

    suspend fun deleteUser(id: UUID): QueryResult<UserResponseDTO, Nothing> {
        // TODO: check if user is admin, etc
        Users.delete(id)?.let {
            return QueryResult.ok(it.toDTO())
        } ?: return QueryResult.notFound()
    }
}

/**
 * Fetches given email to check if it is already in use.
 * If a user is provided, checks that hypothetical found user is different.
 *
 * @param email Email to lookup
 * @param toEdit Optional user to check against.
 * This parameter should represent the user that is currently logged in.
 *
 * @return Optional [QueryResult] with [ErrorDTO] if error occurred
 */
private suspend fun checkEmail(email: String?, toEdit: User? = null) =
    if (email != null)
        Users.findByEmail(email)?.let {
            if (it.uuid == toEdit?.uuid) {
                null
            } else {
                QueryResult.forbidden("errors.users.email.already_exists")
            }
        }
    else null


/**
 * Fetches given username to check if it is already in use.
 * If a user is provided, checks that hypothetical found user is different.
 *
 * @param username Username to lookup
 * @param toEdit Optional user to check against.
 * This parameter should represent the user that is currently logged in.
 *
 * @return Optional [QueryResult] with [ErrorDTO] if error occurred
 */
private suspend fun checkUsername(username: String?, toEdit: User? = null) =
    if (username != null)
        Users.findByUsername(username)?.let {
            if (it.uuid == toEdit?.uuid) {
                null
            } else {
                QueryResult.forbidden("errors.users.username.already_exists")
            }
        }
    else null
