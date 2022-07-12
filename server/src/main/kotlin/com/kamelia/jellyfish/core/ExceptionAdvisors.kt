package com.kamelia.jellyfish.core

import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.respond
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import kotlinx.serialization.SerializationException

private suspend fun badRequestMessage(e: Throwable, call: ApplicationCall) =
    call.respond(QueryResult.badRequest(e.message ?: e.javaClass.name))

val MissingParameterAdvisor = exceptionAdvisor<MissingParameterException>(::badRequestMessage)

val IllegalArgumentAdvisor = exceptionAdvisor<IllegalArgumentException>(::badRequestMessage)

val InvalidUUIDAdvisor = exceptionAdvisor<InvalidUUIDException>(::badRequestMessage)

val SerializationAdvisor = exceptionAdvisor<SerializationException>(::badRequestMessage)

val ExpiredOrInvalidTokenAdvisor = exceptionAdvisor<ExpiredOrInvalidTokenException> { e, call ->
    call.respond(QueryResult.unauthorized(e.message!!))
}

val GeneralAdvisor = exceptionAdvisor<Exception> { e, call ->
    call.respond(QueryResult.error(HttpStatusCode.InternalServerError, listOf("errors.unknown")))
    call.application.log.error("Unexpected error", e)
}

val BasicAdvisor = arrayOf(
    MissingParameterAdvisor,
    IllegalArgumentAdvisor,
    InvalidUUIDAdvisor,
    SerializationAdvisor,
    ExpiredOrInvalidTokenAdvisor,
    GeneralAdvisor
)
