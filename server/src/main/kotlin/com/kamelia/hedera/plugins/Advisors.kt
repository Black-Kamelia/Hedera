package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.IllegalFilterException
import com.kamelia.hedera.core.InsufficientDiskQuotaException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidUUIDException
import com.kamelia.hedera.core.MessageKeyDTO
import com.kamelia.hedera.core.MissingHeaderException
import com.kamelia.hedera.core.MissingParameterException
import com.kamelia.hedera.core.MissingTokenException
import com.kamelia.hedera.core.MultipartParseException
import com.kamelia.hedera.core.PersonalTokenNotFoundException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.core.UnknownFilterFieldException
import com.kamelia.hedera.core.UnknownSortFieldException
import com.kamelia.hedera.core.UserNotFoundException
import com.kamelia.hedera.core.respondNoSuccess
import com.kamelia.hedera.util.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.SerializationException

fun Application.configureExceptionAdvisors() {
    install(StatusPages) {
        exception(::handleException)
    }
}


private suspend fun handleException(call: ApplicationCall, cause: Throwable) {
    if (Environment.isDev) {
        call.application.environment.log.info("Exception caught: ${cause.javaClass.name}", cause)
    }

    when (cause) {
        is MissingParameterException,
        is MissingHeaderException,
        is SerializationException,
        is IllegalArgumentException,
        is InvalidUUIDException,
        is MultipartParseException,
        is IllegalFilterException,
        is UnknownSortFieldException,
        is UnknownFilterFieldException -> badRequestMessage(call, cause)

        is MissingTokenException,
        is ExpiredOrInvalidTokenException -> unauthorizedMessage(call, cause)

        is IllegalActionException,
        is InsufficientDiskQuotaException,
        is InsufficientPermissionsException -> forbiddenMessage(call, cause)

        is FileNotFoundException,
        is UserNotFoundException,
        is PersonalTokenNotFoundException -> notFound(call, cause)

        else -> unhandledError(call, cause)
    }
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
    call.respondNoSuccess(Response.error(HttpStatusCode.InternalServerError, MessageKeyDTO(Errors.UNKNOWN)))
    call.application.log.error("Unexpected error", cause)
}
