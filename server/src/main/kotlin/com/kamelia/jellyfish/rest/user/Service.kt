package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.ErrorDTO
import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.uuid
import java.util.UUID

object UserService {

    suspend fun signup(dto: UserDTO): QueryResult<UserResponseDTO, List<ErrorDTO>> {
        Users.findByEmail(dto.email)?.let {
            return QueryResult.forbidden("errors.users.email.already_exists")
        }
        Users.findByUsername(dto.username)?.let {
            return QueryResult.forbidden("errors.users.username.already_exists")
        }
        if (dto.role != UserRole.REGULAR) {
            return QueryResult.forbidden("errors.users.role.forbidden")
        }
        // TODO: check if email is valid, password valid, role elevation, etc
        return QueryResult.ok(Users.create(dto).toDTO())
    }

    suspend fun updateUser(id: UUID, dto: UserUpdateDTO): QueryResult<UserResponseDTO, List<ErrorDTO>> {
        val self = Users.findById(id) ?: return QueryResult.notFound()

        Users.findByEmail(dto.email)?.let {
            if (it.uuid != self.uuid) return QueryResult.forbidden("errors.users.email.already_exists")
        }
        Users.findByUsername(dto.username)?.let {
            if (it.uuid != self.uuid) return QueryResult.forbidden("errors.users.username.already_exists")
        }

        // TODO: check if email is valid, password valid, role elevation, etc
        return QueryResult.ok(Users.update(id, dto)?.toDTO() ?: return QueryResult.notFound())
    }

    suspend fun deleteUser(id: UUID): QueryResult<UserResponseDTO, Nothing> {
        // TODO: check if user is admin, etc
        Users.delete(id)?.let {
            return QueryResult.ok(it.toDTO())
        } ?: return QueryResult.notFound()
    }
}
