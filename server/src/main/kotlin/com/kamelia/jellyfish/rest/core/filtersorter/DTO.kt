@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.jellyfish.rest.core.filtersorter

import com.kamelia.jellyfish.rest.core.DTO
import com.kamelia.jellyfish.util.UUIDSerializer
import kotlinx.serialization.UseSerializers


@kotlinx.serialization.Serializable
data class FilterSorterDefinitionDTO(
    val filters: FilterDefinitionDTO = listOf(),
    val sorter: SorterDefinitionDTO = listOf(),
) : DTO

typealias FilterDefinitionDTO = List<FilterGroupDTO>

typealias FilterGroupDTO = List<FilterObject>

@kotlinx.serialization.Serializable
data class FilterObject(
    val field: String,
    val operator: String,
    val value: String,
    val type: FilterType = FilterType.POS,
) : DTO

typealias SorterDefinitionDTO = List<SortObject>

@kotlinx.serialization.Serializable
data class SortObject(
    val field: String,
    val direction: SortDirection = SortDirection.ASC,
) : DTO

enum class FilterType { POS, NEG }
enum class SortDirection { ASC, DES }
