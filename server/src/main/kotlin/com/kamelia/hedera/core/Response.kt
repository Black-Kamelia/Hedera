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
    constructor(key: String, vararg parameters: Pair<String, String>) : this(key, mapOf(*parameters))
}

fun String.asMessage(parameters: Map<String, String>? = null) = MessageKeyDTO(this, parameters)
fun String.asMessage(vararg parameters: Pair<String, String>) = MessageKeyDTO(this, mapOf(*parameters))

@Serializable
data class MessageDTO<T : DTO>(
    val title: MessageKeyDTO,
    val message: MessageKeyDTO?,
    val payload: T?,
    val fields: Map<String, MessageKeyDTO>?,
) : DTO {

    companion object {
        fun simple(
            title: MessageKeyDTO,
            message: MessageKeyDTO? = null,
            fields: Map<String, MessageKeyDTO>? = null
        ): MessageDTO<Nothing> =
            MessageDTO(title, message, null, fields)

        fun <T : DTO> payload(
            title: MessageKeyDTO,
            payload: T,
            message: MessageKeyDTO? = null,
            fields: Map<String, MessageKeyDTO>? = null
        ): MessageDTO<T> =
            MessageDTO(title, message, payload, fields)
    }

    fun withFields(vararg fields: Pair<String, MessageKeyDTO>): MessageDTO<T> =
        copy(fields = mapOf(*fields))

}

open class Response<out T>(
    val status: HttpStatusCode,
    private val success: ResultData<out T>? = null,
    private val error: ResultData<MessageDTO<out DTO>>? = null,
) {
    init {
        require(!(status.isSuccess() && success == null || !status.isSuccess() && error == null)) {
            "Either success or error must be set and correspond to the status code"
        }
    }

    suspend fun ifSuccess(
        onSuccess: suspend (ResultData<out T>) -> Unit
    ) = ifSuccessOrElse(onSuccess) {}


    suspend fun ifSuccessOrElse(
        onSuccess: suspend (ResultData<out T>) -> Unit,
        onError: suspend (ResultData<MessageDTO<out DTO>>) -> Unit
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
        fun <S> success(status: HttpStatusCode, result: S? = null) =
            Response(status, success = ResultData(result))

        fun error(status: HttpStatusCode, error: MessageDTO<out DTO>? = null) =
            Response<Nothing>(status, error = ResultData(error))

        fun error(status: HttpStatusCode, error: MessageKeyDTO) =
            error(status, MessageDTO.simple(error))

        fun <S> ok(value: S) = success(HttpStatusCode.OK, value)
        fun <S> created(value: S) = success(HttpStatusCode.Created, value)
        fun noContent() = success<Nothing>(HttpStatusCode.NoContent)

        fun badRequest(error: MessageKeyDTO) = error(HttpStatusCode.BadRequest, error)
        fun badRequest(error: String) = badRequest(MessageKeyDTO(error))

        fun unauthorized(error: MessageKeyDTO) = error(HttpStatusCode.Unauthorized, error)
        fun unauthorized(error: String) = unauthorized(MessageKeyDTO(error))

        fun forbidden(error: MessageKeyDTO) = error(HttpStatusCode.Forbidden, error)
        fun forbidden(error: String) = forbidden(MessageKeyDTO(error))

        fun notFound(error: MessageKeyDTO) = error(HttpStatusCode.NotFound, error)
        fun notFound(error: String) = notFound(MessageKeyDTO(error))
        fun notFound() = error(HttpStatusCode.NotFound)
    }
}

class ActionResponse<out T : DTO>(
    status: HttpStatusCode,
    success: ResultData<MessageDTO<out T>>? = null,
    error: ResultData<MessageDTO<out DTO>>? = null,
) : Response<MessageDTO<out T>>(status, success, error) {

    companion object {
        private fun <T : DTO> success(status: HttpStatusCode, result: MessageDTO<T>? = null): ActionResponse<T> =
            ActionResponse(status, success = ResultData(result))

        private fun error(status: HttpStatusCode, result: MessageDTO<out DTO>? = null): ActionResponse<Nothing> =
            ActionResponse(status, error = ResultData(result))

        private fun <T : DTO> messageOf(
            title: MessageKeyDTO,
            message: MessageKeyDTO?,
            payload: T?,
            fields: Map<String, MessageKeyDTO>?
        ): MessageDTO<out T> =
            if (payload == null)
                MessageDTO.simple(title, message, fields)
            else
                MessageDTO.payload(title, payload, message, fields)

        fun <T : DTO> ok(
            title: MessageKeyDTO,
            message: MessageKeyDTO? = null,
            payload: T? = null,
            fields: Map<String, MessageKeyDTO>? = null,
        ): ActionResponse<T> = success(HttpStatusCode.OK, messageOf(title, message, payload, fields))

        fun <T : DTO> created(
            title: MessageKeyDTO,
            message: MessageKeyDTO? = null,
            payload: T? = null,
            fields: Map<String, MessageKeyDTO>? = null,
        ): ActionResponse<T> = success(HttpStatusCode.Created, messageOf(title, message, payload, fields))
    }
}

data class ResultData<T>(
    val data: T? = null
)

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