package com.kamelia.jellyfish.rest.core.pageable

import com.kamelia.jellyfish.rest.core.DTO

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PageableDTO

abstract class PageDTO<T : DTO>(
    val items: List<T>,
    val page: Long,
    val pageSize: Int,
    val totalPages: Long,
    val totalItems: Long,
)
