package com.kamelia.hedera.core.response

import io.ktor.server.application.*
import io.ktor.server.response.*

data class ResultData<T>(val data: T? = null)

suspend inline fun <reified S> ApplicationCall.respond(response: Response<S>) {
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

suspend inline fun ApplicationCall.respondNoSuccess(response: Response<Nothing>) {
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

suspend inline fun <reified S> ApplicationCall.respondNoError(response: Response<S>) {
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

suspend inline fun ApplicationCall.respondNothing(response: Response<Nothing>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        {
            this.response.status(response.status)
        }
    )
}
