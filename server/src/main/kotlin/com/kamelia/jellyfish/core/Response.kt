package com.kamelia.jellyfish.core

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

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

suspend inline fun ApplicationCall.respond(response: Response<*, *>) {
    val res: suspend (ResultData<*>) -> Unit = { result ->
        this.response.status(response.status)
        result.data?.let { respond(it) }
    }
    response.ifSuccessOrElse(res, res)
}
