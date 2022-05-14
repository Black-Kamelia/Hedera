package com.kamelia.jellyfish.util

import io.ktor.http.*

class QueryResult<E> private constructor(
    val status: HttpStatusCode,
    private val _result: E? = null
) {
    val result: E
        get() = _result ?: throw NoSuchElementException("QueryResult has no result (status: $status)")

    fun getOrDefault(block: () -> E) = _result ?: block()
    fun getOrElse(block: () -> Nothing) = _result ?: block()

    companion object {
        fun <E> ok(value: E) = QueryResult(HttpStatusCode.OK, value)
        fun <E> empty() = QueryResult<E>(HttpStatusCode.OK)
        fun <E> notFound() = QueryResult<E>(HttpStatusCode.NotFound)
        fun <E> unauthorized() = QueryResult<E>(HttpStatusCode.Unauthorized)
        fun <E> forbidden() = QueryResult<E>(HttpStatusCode.Forbidden)
    }
}
