package com.kamelia.hedera.core.response

import com.kamelia.hedera.core.constant.BulkActionMessage
import com.kamelia.hedera.core.constant.BulkActionMessageContainer
import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*

class BulkActionResponse<E> private constructor(
    status: HttpStatusCode,
    private val success: ResultData<MessageDTO<out BulkActionSummaryDTO<out E>>>? = null,
    private val error: ResultData<MessageDTO<out DTO>>? = null,
) : ActionResponse<BulkActionSummaryDTO<out E>>(status, success, error) {

    fun withMessageParameters(vararg parameters: Pair<String, MessageKeyDTO>): BulkActionResponse<E> {
        val messageParameters = success?.data?.message?.parameters ?: return this
        val newMessageParameters = messageParameters.plus(parameters)
        val newMessage = success.data.message.copy(parameters = newMessageParameters)
        val newSuccess = success.copy(data = success.data.copy(message = newMessage))
        return BulkActionResponse(status, newSuccess, error)
    }

    companion object {

        fun <E> of(
            container: BulkActionMessage,
            success: List<E> = emptyList(),
            fail: List<E> = emptyList(),
            total: Int = 0,
        ): BulkActionResponse<E> = when {
            success.size == total -> success(container.success, success, fail, total)
            fail.size == total -> fail(container.fail, success, fail, total)
            else -> partial(container.partial, success, fail, total)
        }

        private fun <E> success(
            container: BulkActionMessageContainer,
            success: List<E> = emptyList(),
            fail: List<E> = emptyList(),
            total: Int = 0,
        ): BulkActionResponse<E> = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun <E> partial(
            container: BulkActionMessageContainer,
            success: List<E> = emptyList(),
            fail: List<E> = emptyList(),
            total: Int = 0,
        ): BulkActionResponse<E> = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun <E> fail(
            container: BulkActionMessageContainer,
            success: List<E> = emptyList(),
            fail: List<E> = emptyList(),
            total: Int = 0,
        ): BulkActionResponse<E> = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun <E> buildResponse(
            title: MessageKeyDTO,
            message: (Int, Int, Int) -> MessageKeyDTO,
            success: List<E> = emptyList(),
            fail: List<E> = emptyList(),
            total: Int = 0,
        ): BulkActionResponse<E> = BulkActionResponse(
            HttpStatusCode.OK,
            ResultData(
                MessageDTO(
                    title,
                    message(success.size, fail.size, total),
                    BulkActionSummaryDTO(success.size, success, fail.size, fail, total),
                    null
                )
            )
        )
    }
}