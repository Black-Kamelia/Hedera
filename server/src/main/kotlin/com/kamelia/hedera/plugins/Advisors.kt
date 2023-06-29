package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.SerializationException

fun Application.configureExceptionAdvisors() {
    install(StatusPages) {
        exception(::handleException)
    }
}


private suspend fun handleException(call: ApplicationCall, cause: Throwable) = when (cause) {
    is MissingParameterException,
    is MissingHeaderException,
    is SerializationException,
    is IllegalArgumentException,
    is InvalidUUIDException,
    is MultipartParseException,
    is IllegalFilterException,
    is UnknownFilterFieldException -> badRequestMessage(call, cause)

    is MissingTokenException,
    is ExpiredOrInvalidTokenException -> unauthorizedMessage(call, cause)

    is IllegalActionException,
    is InsufficientPermissionsException -> forbiddenMessage(call, cause)

    is FileNotFoundException -> notFound(call, cause)

    else -> unhandledError(call, cause)
}

private suspend fun badRequestMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.badRequest(cause.error))
    else -> call.respondNoSuccess(Response.badRequest(cause.message ?: cause.javaClass.name))
}

private suspend fun unauthorizedMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.unauthorized(cause.error))
    else -> call.respondNoSuccess(Response.unauthorized(cause.message ?: cause.javaClass.name))
}

private suspend fun forbiddenMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.forbidden(cause.error))
    else -> call.respondNoSuccess(Response.forbidden(cause.message ?: cause.javaClass.name))
}

private suspend fun notFound(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.notFound(cause.error))
    else -> call.respondNoSuccess(Response.notFound(cause.message ?: cause.javaClass.name))
}

private suspend fun unhandledError(call: ApplicationCall, cause: Throwable) {
    call.respondNoSuccess(Response.error(HttpStatusCode.InternalServerError, MessageDTO.of(Errors.UNKNOWN)))
    call.application.log.error("Unexpected error", cause)
}
