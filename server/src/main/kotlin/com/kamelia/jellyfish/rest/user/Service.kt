package com.kamelia.jellyfish.rest.user

import com.kamelia.jellyfish.util.QueryResult

object UserService {

    suspend fun signup(dto: UserDTO): QueryResult<UserResponseDTO, String> {
        Users.findByEmail(dto.email)?.let {
            return QueryResult.forbidden("Email already exists")
        }
        Users.findByUsername(dto.username)?.let {
            return QueryResult.forbidden("Username already exists")
        }
        return QueryResult.ok(Users.create(dto).toDTO())
    }
}
