package com.kamelia.hedera.core.response

import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*

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
    ) = ifSuccessOrElse(onSuccess, onError = {})


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

        fun ok() = success<Nothing>(HttpStatusCode.OK, null)
        fun <S> ok(value: S) = success(HttpStatusCode.OK, value)
        fun <S> created(value: S) = success(HttpStatusCode.Created, value)
        fun noContent() = success<Nothing>(HttpStatusCode.NoContent)

        fun badRequest(error: MessageDTO<out DTO>) = error(HttpStatusCode.BadRequest, error)
        fun badRequest(error: MessageKeyDTO) = error(HttpStatusCode.BadRequest, error)
        fun badRequest(error: String) = badRequest(MessageKeyDTO(error))

        fun unauthorized(error: MessageKeyDTO) = error(HttpStatusCode.Unauthorized, error)
        fun unauthorized(error: String) = unauthorized(MessageKeyDTO(error))

        fun forbidden(error: MessageKeyDTO) = error(HttpStatusCode.Forbidden, error)
        fun forbidden(error: String) = forbidden(MessageKeyDTO(error))

        fun notFound(error: MessageDTO<out DTO>) = error(HttpStatusCode.NotFound, error)
        fun notFound(error: MessageKeyDTO) = error(HttpStatusCode.NotFound, error)
        fun notFound(error: String) = notFound(MessageKeyDTO(error))
        fun notFound() = error(HttpStatusCode.NotFound)
    }
}
