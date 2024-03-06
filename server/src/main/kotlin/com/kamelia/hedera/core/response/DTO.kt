@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.core.response

import com.kamelia.hedera.rest.core.DTO
import com.kamelia.hedera.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class MessageKeyDTO(
    val key: String,
    val parameters: Map<String, MessageKeyDTO>? = null,
    val humanString: String? = null,
) : DTO {
    constructor(key: String, vararg parameters: Pair<String, MessageKeyDTO>) : this(key, mapOf(*parameters))

    fun withParameters(vararg parameters: Pair<String, String>): MessageKeyDTO {
        val params = parameters.map { (k, v) -> k to MessageKeyDTO(v) }.toTypedArray()
        if (this.parameters != null) {
            return copy(parameters = this.parameters.plus(params))
        }
        return copy(parameters = params.toMap())
    }
}

fun String.asMessage() = MessageKeyDTO(this)
fun String.asMessage(vararg parameters: Pair<String, String>) =
    MessageKeyDTO(this, parameters.associate { (k, v) -> k to MessageKeyDTO(v) })

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
data class BulkActionSummaryDTO<E>(
    val success: Int,
    val successItems: List<E>,
    val fail: Int,
    val failItems: List<E>,
    val total: Int,
) : DTO
