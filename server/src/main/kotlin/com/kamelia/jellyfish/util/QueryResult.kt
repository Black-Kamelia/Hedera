package com.kamelia.jellyfish.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class QueryResult<E> private constructor(
    val status: HttpStatusCode,
    private val _result: E? = null
) {
    val result: E
        get() = _result ?: throw NoSuchElementException("QueryResult has no result (status: $status)")

    fun getOrDefault(block: () -> E) = _result ?: block()
    fun getOrElse(block: () -> Nothing) = _result ?: block()

    suspend fun ifPresentOrElse(block: suspend (E) -> Unit, elseBlock: suspend () -> Unit) {
        if (_result != null) block(_result) else elseBlock()
    }

    companion object {
        fun <E> ok(value: E) = QueryResult(HttpStatusCode.OK, value)
        fun <E> empty() = QueryResult<E>(HttpStatusCode.OK)
        fun <E> notFound() = QueryResult<E>(HttpStatusCode.NotFound)
        fun <E> unauthorized() = QueryResult<E>(HttpStatusCode.Unauthorized)
        // TODO
        // fun status(status: HttpStatusCode, message: String? = null) = QueryResult(status, message)
        // fun forbidden(message: String?) = status(HttpStatusCode.Forbidden, message)
    }
}

suspend inline fun ApplicationCall.respond(result: QueryResult<out Any>) {
    result.ifPresentOrElse({
        respond(result.status, it)
    }, {
        response.status(result.status)
    })
}
