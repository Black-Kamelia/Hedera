package com.kamelia.hedera.core.response

import com.kamelia.hedera.core.constant.BulkActionMessage
import com.kamelia.hedera.core.constant.BulkActionMessageContainer
import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*

class BulkActionResponse private constructor(
    status: HttpStatusCode,
    private val success: ResultData<MessageDTO<out BulkActionSummaryDTO>>? = null,
    private val error: ResultData<MessageDTO<out DTO>>? = null,
) : ActionResponse<BulkActionSummaryDTO>(status, success, error) {

    fun withMessageParameters(vararg parameters: Pair<String, MessageKeyDTO>): BulkActionResponse {
        val messageParameters = success?.data?.message?.parameters ?: return this
        val newMessageParameters = messageParameters.plus(parameters)
        val newMessage = success.data.message.copy(parameters = newMessageParameters)
        val newSuccess = success.copy(data = success.data.copy(message = newMessage))
        return BulkActionResponse(status, newSuccess, error)
    }

    companion object {

        fun of(
            container: BulkActionMessage,
            success: Int = 0,
            fail: Int = 0,
            total: Int = 0,
        ): BulkActionResponse = when {
            success == total -> success(container.success, success, fail, total)
            fail == total -> fail(container.fail, success, fail, total)
            else -> partial(container.partial, success, fail, total)
        }

        private fun success(
            container: BulkActionMessageContainer,
            success: Int = 0,
            fail: Int = 0,
            total: Int = 0,
        ): BulkActionResponse = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun partial(
            container: BulkActionMessageContainer,
            success: Int = 0,
            fail: Int = 0,
            total: Int = 0,
        ): BulkActionResponse = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun fail(
            container: BulkActionMessageContainer,
            success: Int = 0,
            fail: Int = 0,
            total: Int = 0,
        ): BulkActionResponse = buildResponse(
            container.title,
            container::message,
            success,
            fail,
            total
        )

        private fun buildResponse(
            title: MessageKeyDTO,
            message: (Int, Int, Int) -> MessageKeyDTO,
            success: Int = 0,
            fail: Int = 0,
            total: Int = 0,
        ): BulkActionResponse = BulkActionResponse(
            HttpStatusCode.OK,
            ResultData(
                MessageDTO(
                    title,
                    message(success, fail, total),
                    BulkActionSummaryDTO(success, fail, total),
                    null
                )
            )
        )
    }
}