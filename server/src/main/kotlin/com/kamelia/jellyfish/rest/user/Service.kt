package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.QueryResult
import java.util.UUID

object UserService {

    suspend fun signup(dto: UserDTO): QueryResult<UserResponseDTO, String> {
        Users.findByEmail(dto.email)?.let {
            return QueryResult.forbidden("Email already exists")
        }
        Users.findByUsername(dto.username)?.let {
            return QueryResult.forbidden("Username already exists")
        }
        // TODO: check if email is valid, password valid, role elevation, etc
        return QueryResult.ok(Users.create(dto).toDTO())
    }

    suspend fun delete(id: UUID): QueryResult<UserResponseDTO, Nothing> {
        // TODO: check if user is admin, etc
        Users.delete(id)?.let {
            return QueryResult.ok(it.toDTO())
        } ?: return QueryResult.notFound()
    }
}
