package com.kamelia.jellyfish.rest.core.pageable

import com.kamelia.jellyfish.rest.core.DTO
import kotlinx.serialization.Serializable

@Serializable
class PageDTO<T : DTO>(
    val items: List<T>,
    val page: Long,
    val pageSize: Int,
    val totalPages: Long,
    val totalItems: Long,
)
