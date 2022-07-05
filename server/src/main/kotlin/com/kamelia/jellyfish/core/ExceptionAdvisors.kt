package com.kamelia.jellyfish.core

import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.respond
import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.SerializationException

private suspend fun badRequestMessage(e: Throwable, call: ApplicationCall) =
    call.respond(QueryResult.badRequest(e.message!!))

val MissingParameterAdvisor = exceptionAdvisor<MissingParameterException>(::badRequestMessage)

val IllegalArgumentAdvisor = exceptionAdvisor<IllegalArgumentException>(::badRequestMessage)

val InvalidUUIDAdvisor = exceptionAdvisor<InvalidUUIDException>(::badRequestMessage)

val SerializationAdvisor = exceptionAdvisor<SerializationException>(::badRequestMessage)

val ExpiredOrInvalidTokenAdvisor = exceptionAdvisor<ExpiredOrInvalidTokenException> { e, call ->
    call.respond(QueryResult.unauthorized(e.message!!))
}

val BasicAdvisor = arrayOf(
    MissingParameterAdvisor,
    IllegalArgumentAdvisor,
    InvalidUUIDAdvisor,
    SerializationAdvisor,
    ExpiredOrInvalidTokenAdvisor
)
