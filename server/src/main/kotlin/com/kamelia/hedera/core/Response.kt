package com.kamelia.hedera.core

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

typealias ErrorDTO = String

class Response<out S, out E> private constructor(
    val status: HttpStatusCode,
    private val success: ResultData<S>? = null,
    private val error: ResultData<E>? = null,
) {
    init {
        require(!(status.isSuccess() && success == null || !status.isSuccess() && error == null)) {
            "Either success or error must be set and correspond to the status code"
        }
    }

    suspend fun ifSuccessOrElse(
        onSuccess: suspend (ResultData<out S>) -> Unit,
        onError: suspend (ResultData<out E>) -> Unit = {}
    ) {
        if (status.isSuccess()) {
            checkNotNull(success) { "Success result is null but it should not be possible" }
            onSuccess(success)
        } else {
            checkNotNull(error) { "Error result is null but it should not be possible" }
            onError(error)
        }
    }

    companion object {
        fun <S> success(status: HttpStatusCode, result: S? = null) =
            Response<S, Nothing>(status, success = ResultData(result))

        fun <E> error(status: HttpStatusCode, error: E? = null) =
            Response<Nothing, E>(status, error = ResultData(error))

        fun <S> ok(value: S) = success(HttpStatusCode.OK, value)
        fun ok() = success<Nothing>(HttpStatusCode.OK)
        fun noContent() = success<Nothing>(HttpStatusCode.NoContent)

        fun badRequest(vararg error: ErrorDTO) = error(HttpStatusCode.BadRequest, error.toList())
        fun unauthorized(vararg error: ErrorDTO) = error(HttpStatusCode.Unauthorized, error.toList())
        fun forbidden(vararg error: ErrorDTO) = error(HttpStatusCode.Forbidden, error.toList())
        fun notFound() = error<Nothing>(HttpStatusCode.NotFound)
    }
}

data class ResultData<T>(
    val data: T? = null
)

suspend inline fun <reified S, reified E> ApplicationCall.respond(response: Response<S, E>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            result.data?.let { respond(it) }
        },
        { result ->
            this.response.status(response.status)
            result.data?.let { respond(it) }
        }
    )
}

suspend inline fun <reified E> ApplicationCall.respondNoSuccess(response: Response<Nothing, E>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        { result ->
            this.response.status(response.status)
            result.data?.let { respond(it) }
        }
    )
}

suspend inline fun <reified S> ApplicationCall.respondNoError(response: Response<S, Nothing>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            result.data?.let { respond(it) }
        },
        {
            this.response.status(response.status)
        }
    )
}

suspend inline fun ApplicationCall.respondNothing(response: Response<Nothing, Nothing>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        {
            this.response.status(response.status)
        }
    )
}