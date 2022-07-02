package com.kamelia.jellyfish.core

import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.respond
import kotlinx.serialization.SerializationException

val MissingParameterAdvisor = exceptionAdvisor<MissingParameterException> { e, call ->
    call.respond(QueryResult.badRequest(e.message!!))
}

val IllegalArgumentAdvisor = exceptionAdvisor<IllegalArgumentException> { e, call ->
    call.respond(QueryResult.badRequest(e.message!!))
}

val InvalidUUIDAdvisor = exceptionAdvisor<InvalidUUIDException> { e, call ->
    call.respond(QueryResult.badRequest(e.message!!))
}

val SerializationAdvisor = exceptionAdvisor<SerializationException> { e, call ->
    call.respond(QueryResult.badRequest(e.message!!))
}

val BasicAdvisor = arrayOf(
    MissingParameterAdvisor,
    IllegalArgumentAdvisor,
    InvalidUUIDAdvisor,
    SerializationAdvisor,
)
