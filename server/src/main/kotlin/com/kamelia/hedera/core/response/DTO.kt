package com.kamelia.hedera.core.response

import com.kamelia.hedera.rest.core.DTO
import kotlinx.serialization.Serializable

@Serializable
data class MessageKeyDTO(
    val key: String,
    val parameters: Map<String, MessageKeyDTO>? = null,
) : DTO {
    constructor(key: String, vararg parameters: Pair<String, MessageKeyDTO>) : this(key, mapOf(*parameters))

    fun withParameters(vararg parameters: Pair<String, String>): MessageKeyDTO {
        val params = parameters.map { (k, v) -> k to MessageKeyDTO(v) }.toTypedArray()
        this.parameters?.let { return copy(parameters = it.plus(params)) }
        return copy(parameters = mapOf(*params))
    }
}

fun String.asMessage() = MessageKeyDTO(this)
fun String.asMessage(parameters: Map<String, String>? = null) =
    MessageKeyDTO(this, parameters?.mapValues { MessageKeyDTO(it.value) })
fun String.asMessage(vararg parameters: Pair<String, String>) =
    MessageKeyDTO(this, mapOf(*(parameters.map { (k, v) -> k to MessageKeyDTO(v) }.toTypedArray())))

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

@Serializable
data class BulkActionSummaryDTO(
    val successes: Int,
    val fails: Int,
    val total: Int,
) : DTO
