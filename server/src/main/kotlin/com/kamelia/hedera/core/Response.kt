package com.kamelia.hedera.core

import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class MessageKeyDTO(
    val key: String,
    val parameters: Map<String, String>? = null,
) : DTO {
    companion object {
        fun of(key: String, template: Map<String, String>? = null) = MessageKeyDTO(key, template)
        fun of(key: String, vararg template: Pair<String, String>) = MessageKeyDTO(key, mapOf(*template))
    }
}

@Serializable
sealed interface MessageDTO<T : DTO> : DTO {

    @Serializable
    data class Simple(
        val title: MessageKeyDTO,
        val message: MessageKeyDTO? = null,
    ) : MessageDTO<Nothing> {

        constructor(title: String, message: String? = null) : this(
            MessageKeyDTO.of(title),
            message?.let { MessageKeyDTO.of(it) }
        )

    }

    @Serializable
    data class Payload<T : DTO>(
        val payload: T,
        val title: MessageKeyDTO? = null,
        val message: MessageKeyDTO? = null,
    ) : MessageDTO<T> {

        constructor(payload: T, title: String, message: String? = null) : this(
            payload,
            MessageKeyDTO.of(title),
            message?.let { MessageKeyDTO.of(it) }
        )

    }

}

class Response<out S : DTO, out E : DTO> private constructor(
    val status: HttpStatusCode,
    private val success: MessageDTO<S>? = null,
    private val error: MessageDTO<E>? = null,
) {
    init {
        require(!(status.isSuccess() && success == null || !status.isSuccess() && error == null)) {
            "Either success or error must be set and correspond to the status code"
        }
    }

    suspend fun ifSuccessOrElse(
        onSuccess: suspend (MessageDTO<out S>) -> Unit,
        onError: suspend (MessageDTO<out E>) -> Unit = {}
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
        fun <S : DTO> success(status: HttpStatusCode, result: MessageDTO<S>? = null) =
            Response<S, Nothing>(status, success = result)

        fun <E : DTO> error(status: HttpStatusCode, error: MessageDTO<E>? = null) =
            Response<Nothing, E>(status, error = error)

        fun error(status: HttpStatusCode, error: String) =
            error(status, MessageDTO.Simple(error))


        fun <T : DTO> ok(value: MessageDTO<T>) = success(HttpStatusCode.OK, value)
        fun <T : DTO> ok(payload: T) = ok(MessageDTO.Payload(payload))
        fun ok() = success<Nothing>(HttpStatusCode.OK)

        fun <T : DTO> created(value: MessageDTO<T>) = success(HttpStatusCode.Created, value)
        fun <T : DTO> created(payload: T) = created(MessageDTO.Payload(payload))

        fun noContent() = success<Nothing>(HttpStatusCode.NoContent)

        fun <T : DTO> badRequest(error: MessageDTO<T>) = error(HttpStatusCode.BadRequest, error)
        fun <T : DTO> badRequest(payload: T) = badRequest(MessageDTO.Payload(payload))
        fun badRequest(error: MessageKeyDTO) = badRequest(MessageDTO.Simple(error))
        fun badRequest(error: String) = badRequest(MessageKeyDTO.of(error))

        fun <T : DTO> unauthorized(error: MessageDTO<T>) = error(HttpStatusCode.Unauthorized, error)
        fun <T : DTO> unauthorized(payload: T) = unauthorized(MessageDTO.Payload(payload))
        fun unauthorized(error: MessageKeyDTO) = unauthorized(MessageDTO.Simple(error))
        fun unauthorized(error: String) = unauthorized(MessageKeyDTO.of(error))

        fun <T : DTO> forbidden(error: MessageDTO<T>) = error(HttpStatusCode.Forbidden, error)
        fun <T : DTO> forbidden(payload: T) = forbidden(MessageDTO.Payload(payload))
        fun forbidden(error: MessageKeyDTO) = forbidden(MessageDTO.Simple(error))
        fun forbidden(error: String) = forbidden(MessageKeyDTO.of(error))

        fun <T : DTO> notFound(error: MessageDTO<T>) = error(HttpStatusCode.NotFound, error)
        fun <T : DTO> notFound(payload: T) = notFound(MessageDTO.Payload(payload))
        fun notFound(error: MessageKeyDTO) = notFound(MessageDTO.Simple(error))
        fun notFound(error: String) = notFound(MessageKeyDTO.of(error))
        fun notFound() = error<Nothing>(HttpStatusCode.NotFound)
    }
}

data class ResultData<T>(
    val data: T? = null
)

suspend inline fun <reified S : DTO, reified E : DTO> ApplicationCall.respond(response: Response<S, E>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            respond(result)
        },
        { result ->
            this.response.status(response.status)
            respond(result)
        }
    )
}

suspend inline fun <reified E : DTO> ApplicationCall.respondNoSuccess(response: Response<Nothing, E>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        { result ->
            this.response.status(response.status)
            respond(result)
        }
    )
}

suspend inline fun <reified S : DTO> ApplicationCall.respondNoError(response: Response<S, Nothing>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            respond(result)
        },
        {
            this.response.status(response.status)
        }
    )
}

suspend inline fun ApplicationCall.respondNothing(response: Response<Nothing, Nothing>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        {
            this.response.status(response.status)
        }
    )
}
