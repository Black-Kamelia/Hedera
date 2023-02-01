package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.IllegalFilterException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidUUIDException
import com.kamelia.hedera.core.MissingHeaderException
import com.kamelia.hedera.core.MissingParameterException
import com.kamelia.hedera.core.MissingTokenException
import com.kamelia.hedera.core.MultipartParseException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UnknownFilterFieldException
import com.kamelia.hedera.core.respond
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

    is MissingTokenException,
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
