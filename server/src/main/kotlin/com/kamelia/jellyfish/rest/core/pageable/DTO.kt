package com.kamelia.jellyfish.rest.core.pageable

import com.kamelia.jellyfish.rest.core.DTO
import kotlinx.serialization.Serializable

@Serializable
data class PageDTO(
    val items: List<DTO>,
    val page: Long,
    val pageSize: Int,
    val totalPages: Long,
    val totalItems: Long,
)
