package com.kamelia.jellyfish.util

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class QueryResult<out S, out E> private constructor(
    val status: HttpStatusCode,
    private val success: ResultData<S>? = null,
    private val error: ResultData<E>? = null,
) {
    init {
        if (status.isSuccess() && success == null || !status.isSuccess() && error == null) {
            throw IllegalArgumentException("Either success or error must be set and correspond to the status code")
        }
    }

    suspend fun ifSuccessOrElse(onSuccess: suspend (ResultData<out S>) -> Unit, onError: suspend (ResultData<out E>) -> Unit = {}) {
        if (status.isSuccess()) {
            onSuccess(success!!)
        } else {
            onError(error!!)
        }
    }

    companion object {
        fun <S> success(status: HttpStatusCode, result: S? = null) = QueryResult<S, Nothing>(status, success = ResultData(result))
        fun <E> error(status: HttpStatusCode, error: E? = null) = QueryResult<Nothing, E>(status, error = ResultData(error))

        fun <S> ok(value: S) = success(HttpStatusCode.OK, value)
        fun noContent() = success<Nothing>(HttpStatusCode.NoContent)

        fun notFound() = error<Nothing>(HttpStatusCode.NotFound)
        fun unauthorized() = error<Nothing>(HttpStatusCode.Unauthorized)
        fun forbidden(error: String) = error(HttpStatusCode.Forbidden, error)
    }
}

data class ResultData<T>(
    val data: T? = null
)

suspend inline fun ApplicationCall.respond(queryResult: QueryResult<*, *>) {
    val res: suspend (ResultData<*>) -> Unit = { result ->
        response.status(queryResult.status)
        result.data?.let { respond(it) }
    }
    queryResult.ifSuccessOrElse(res, res)
}
