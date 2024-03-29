@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.core.pageable

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class PageDTO<T : DTO>(
    val items: List<T>,
    val page: Long,
    val pageSize: Int,
    val totalPages: Long,
    val totalItems: Long,
) : DTO

@Serializable
data class PageDefinitionDTO(
    val filters: FilterDefinitionDTO = listOf(),
    val sorter: SorterDefinitionDTO = listOf(),
) : DTO

typealias FilterDefinitionDTO = List<FilterGroupDTO>

typealias FilterGroupDTO = List<FilterObject>

@Serializable
data class FilterObject(
    val field: String,
    val operator: String,
    val value: String,
    val type: FilterType = FilterType.POSITIVE,
) : DTO

typealias SorterDefinitionDTO = List<SortObject>

@Serializable
data class SortObject(
    val field: String,
    val direction: SortDirection = SortDirection.ASC,
) : DTO

enum class FilterType { POSITIVE, NEGATIVE }
enum class SortDirection { ASC, DESC }
