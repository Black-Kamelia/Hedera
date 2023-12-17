package com.kamelia.hedera.plugins

import com.kamelia.hedera.core.DisabledRegistrationsException
import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.ExpiredOrInvalidTokenException
import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.IllegalActionException
import com.kamelia.hedera.core.IllegalFilterException
import com.kamelia.hedera.core.InsufficientDiskQuotaException
import com.kamelia.hedera.core.InsufficientPermissionsException
import com.kamelia.hedera.core.InvalidUUIDException
import com.kamelia.hedera.core.MessageDTO
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
import com.kamelia.hedera.core.asMessage
import com.kamelia.hedera.core.respondNoSuccess
import com.kamelia.hedera.util.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.SerializationException

fun Application.configureExceptionAdvisors() {
    install(StatusPages) {
        exception(::handleException)
    }
}


private suspend fun handleException(call: ApplicationCall, cause: Throwable) {
    if (Environment.isDev) {
        call.application.environment.log.info("[DEV] Exception caught: ${cause.javaClass.name}", cause)
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
        is UnknownFilterFieldException,
        is BadRequestException -> badRequestMessage(call, cause)

        is MissingTokenException,
        is ExpiredOrInvalidTokenException -> unauthorizedMessage(call, cause)

        is IllegalActionException,
        is DisabledRegistrationsException,
        is InsufficientDiskQuotaException,
        is InsufficientPermissionsException -> forbiddenMessage(call, cause)

        is FileNotFoundException,
        is UserNotFoundException,
        is PersonalTokenNotFoundException,
        is NotFoundException -> notFound(call, cause)

        else -> unhandledError(call, cause)
    }
}

private suspend fun badRequestMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.badRequest(cause.error))
    is BadRequestException -> call.respondNoSuccess(
        Response.badRequest(
            MessageDTO.simple(
                title = Errors.BAD_REQUEST_RAW.asMessage(),
                message = (cause.message ?: cause.javaClass.name).asMessage(),
            )
        )
    )

    else -> call.respondNoSuccess(Response.badRequest(Errors.UNKNOWN))
}

private suspend fun unauthorizedMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.unauthorized(cause.error))
    else -> call.respondNoSuccess(Response.unauthorized(Errors.UNKNOWN))
}

private suspend fun forbiddenMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.forbidden(cause.error))
    else -> call.respondNoSuccess(Response.forbidden(Errors.UNKNOWN))
}

private suspend fun notFound(call: ApplicationCall, cause: Throwable) = when (cause) {
    is HederaException -> call.respondNoSuccess(Response.notFound(cause.error))
    is NotFoundException -> call.respondNoSuccess(
        Response.notFound(
            MessageDTO.simple(
                title = Errors.BAD_REQUEST_RAW.asMessage(),
                message = (cause.message ?: cause.javaClass.name).asMessage(),
            )
        )
    )

    else -> call.respondNoSuccess(Response.notFound(Errors.UNKNOWN))
}

private suspend fun unhandledError(call: ApplicationCall, cause: Throwable) {
    call.respondNoSuccess(Response.error(
        HttpStatusCode.InternalServerError,
        MessageDTO.simple(
            title = Errors.UNKNOWN.asMessage(),
            message = cause.message?.let { Errors.UNKNOWN_MESSAGE.asMessage("hint" to it) },
        )
    ))
    call.application.log.error("Unexpected error", cause)
}
