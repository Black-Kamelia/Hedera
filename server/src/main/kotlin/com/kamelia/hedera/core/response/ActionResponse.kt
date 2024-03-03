package com.kamelia.hedera.core.response

import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*

open class ActionResponse<out T : DTO>(
    status: HttpStatusCode,
    success: ResultData<MessageDTO<out T>>? = null,
    error: ResultData<MessageDTO<out DTO>>? = null,
) : Response<MessageDTO<out T>>(status, success, error) {

    companion object {
        fun <T : DTO> success(status: HttpStatusCode, result: MessageDTO<T>? = null): ActionResponse<T> =
            ActionResponse(status, success = ResultData(result))

        fun error(status: HttpStatusCode, result: MessageDTO<out DTO>? = null): ActionResponse<Nothing> =
            ActionResponse(status, error = ResultData(result))

        fun <T : DTO> messageOf(
            title: MessageKeyDTO,
            message: MessageKeyDTO?,
            payload: T?,
            fields: Map<String, MessageKeyDTO>? = null,
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
