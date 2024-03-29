package com.kamelia.hedera.rest.core.pageable

import com.kamelia.hedera.core.IllegalFilterException
import com.kamelia.hedera.rest.file.FileVisibility
import com.kamelia.hedera.rest.user.UserRole
import com.kamelia.hedera.util.fuzzy
import com.kamelia.hedera.util.toUUIDOrNull
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notLike
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
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
    "like" to { c, v -> c.lowerCase() like v.lowercase() },
    "fuzzy" to { c, v -> (c fuzzy v) or (c.lowerCase() like "%${v.lowercase()}%") },
    "nlike" to { c, v -> c notLike v }
)

val intFilters = comparableFilters<Int>()

val longFilters = comparableFilters<Long>()

val booleanFilters = comparableFilters<Boolean>()

val uuidFilters = mapOf<String, (Column<EntityID<UUID>>, UUID?) -> Op<Boolean>>(
    "eq" to { c, v -> c eq v },
    "ne" to { c, v -> c neq v },
    "eqn" to { c, _ -> c.isNull() }
)

val fileVisibilityFilters = comparableFilters<FileVisibility>()

val userRoleFilters = comparableFilters<UserRole>()

val instantFilters = comparableFilters<Instant>()

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any?> Column<T>.filter(filter: FilterObject): Op<Boolean> = when (T::class) {
    String::class -> stringFilters[filter.operator]?.let { it(this as Column<String>, filter.value) }
    Int::class -> intFilters[filter.operator]?.let {
        val value = filter.value.toIntOrNull()
        if (value != null) it(this as Column<Int>, value) else null
    }
    Long::class -> longFilters[filter.operator]?.let {
        val value = filter.value.toLongOrNull()
        if (value != null) it(this as Column<Long>, value) else null
    }
    Boolean::class -> booleanFilters[filter.operator]?.let {
        val value = filter.value.toBooleanStrictOrNull()
        if (value != null) it(this as Column<Boolean>, value) else null
    }
    EntityID::class -> uuidFilters[filter.operator]?.let {
        val value = filter.value.toUUIDOrNull()
        it(this as Column<EntityID<UUID>>, value)
    }
    FileVisibility::class -> fileVisibilityFilters[filter.operator]?.let {
        val value = FileVisibility.valueOfOrNull(filter.value)
        if (value != null) it(this as Column<FileVisibility>, value) else null
    }
    UserRole::class -> userRoleFilters[filter.operator]?.let {
        val value = UserRole.valueOfOrNull(filter.value)
        if (value != null) it(this as Column<UserRole>, value) else null
    }
    Instant::class -> instantFilters[filter.operator]?.let {
        it(this as Column<Instant>, Instant.parse(filter.value))
    }
    else -> null
} ?: throw IllegalFilterException(filter)

fun Query.applyFilters(filters: FilterDefinitionDTO, mapper: (FilterObject) -> Op<Boolean>): Query = apply {
    filters.forEach { group ->
        andWhere {
            group.fold(Op.nullOp()) { acc, next ->
                val op = mapper(next)
                acc or if (next.type == FilterType.NEGATIVE) not(op) else op
            }
        }
    }
}
