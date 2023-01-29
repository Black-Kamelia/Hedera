package com.kamelia.jellyfish.plugins

import com.kamelia.jellyfish.core.ExpiredOrInvalidTokenException
import com.kamelia.jellyfish.core.IllegalActionException
import com.kamelia.jellyfish.core.IllegalFilterException
import com.kamelia.jellyfish.core.InsufficientPermissionsException
import com.kamelia.jellyfish.core.InvalidUUIDException
import com.kamelia.jellyfish.core.MissingHeaderException
import com.kamelia.jellyfish.core.MissingParameterException
import com.kamelia.jellyfish.core.MultipartParseException
import com.kamelia.jellyfish.core.Response
import com.kamelia.jellyfish.core.UnknownFilterFieldException
import com.kamelia.jellyfish.core.respond
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
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

    is ExpiredOrInvalidTokenException -> unauthorizedMessage(call, cause)

    is IllegalActionException,
    is InsufficientPermissionsException -> forbiddenMessage(call, cause)

    else -> unhandledError(call, cause)
}

private suspend fun badRequestMessage(call: ApplicationCall, cause: Throwable) =
    call.respond(Response.badRequest(cause.message ?: cause.javaClass.name))

private suspend fun unauthorizedMessage(call: ApplicationCall, cause: Throwable) =
    call.respond(Response.unauthorized(cause.message ?: cause.javaClass.name))

private suspend fun forbiddenMessage(call: ApplicationCall, cause: Throwable) =
    call.respond(Response.forbidden(cause.message ?: cause.javaClass.name))

private suspend fun unhandledError(call: ApplicationCall, cause: Throwable) {
    call.respond(Response.error(HttpStatusCode.InternalServerError, listOf("errors.unknown")))
    call.application.log.error("Unexpected error", cause)
}
