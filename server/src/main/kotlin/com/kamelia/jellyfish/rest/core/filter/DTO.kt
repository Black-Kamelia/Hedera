@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.core.filter

import com.kamelia.jellyfish.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class FilterSorterDefinitionDTO(
    val filter: FilterDefinitionDTO = listOf(),
    val sorter: SorterDefinitionDTO = listOf(),
)

typealias FilterDefinitionDTO = List<FilterGroupDTO>

typealias FilterGroupDTO = List<FilterObject>

@Serializable
data class FilterObject(
    val field: String,
    val operator: String,
    val value: String,
    val type: FilterType = FilterType.POS,
)

typealias SorterDefinitionDTO = List<SortObject>

@Serializable
data class SortObject(
    val field: String,
    val direction: SortDirection = SortDirection.ASC,
)

enum class FilterType { POS, NEG }
enum class SortDirection { ASC, DES }
