package com.kamelia.jellyfish.rest.core.pageable

import com.kamelia.jellyfish.core.IllegalFilterException
import com.kamelia.jellyfish.rest.file.FileVisibility
import com.kamelia.jellyfish.util.toUUIDOrNull
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notLike
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or


inline fun <reified T : Comparable<T>> comparableFilters() = mapOf<String, (Column<T>, T) -> Op<Boolean>>(
    "eq" to { c, v -> c eq v },
    "ne" to { c, v -> c neq v },
    "gt" to { c, v -> c greater v },
    "lt" to { c, v -> c less v },
    "ge" to { c, v -> c greaterEq v },
    "le" to { c, v -> c lessEq v },
)

val stringFilters = comparableFilters<String>() + mapOf(
    "like" to { c, v -> c like v },
    "nlike" to { c, v -> c notLike v }
)

val intFilters = comparableFilters<Int>()

val longFilters = comparableFilters<Long>()

val uuidFilters = mapOf<String, (Column<EntityID<UUID>>, UUID) -> Op<Boolean>>(
    "eq" to { c, v -> c eq v },
    "ne" to { c, v -> c neq v },
)

val fileVisibilityFilters = comparableFilters<FileVisibility>()

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> Column<T>.filter(filter: FilterObject): Op<Boolean> = when (T::class) {
    String::class -> stringFilters[filter.operator]?.let { it(this as Column<String>, filter.value) }
    Int::class -> intFilters[filter.operator]?.let { it(this as Column<Int>, filter.value.toInt()) }
    Long::class -> longFilters[filter.operator]?.let { it(this as Column<Long>, filter.value.toLong()) }
    EntityID::class -> uuidFilters[filter.operator]?.let {
        val value = filter.value.toUUIDOrNull()
        if (value != null) it(this as Column<EntityID<UUID>>, value) else null
    }
    FileVisibility::class -> fileVisibilityFilters[filter.operator]?.let {
        val value = FileVisibility.valueOf(filter.value)
        it(this as Column<FileVisibility>, value)
    }
    else -> null
} ?: throw IllegalFilterException(filter)

fun Query.applyFilters(filters: FilterDefinitionDTO, mapper: (FilterObject) -> Op<Boolean>): Query = apply {
    filters.forEach { group ->
        andWhere {
            group.fold(Op.nullOp()) { acc, next ->
                val op = mapper(next)
                acc or if (next.type == FilterType.NEG) not(op) else op
            }
        }
    }
}
