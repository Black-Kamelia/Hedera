package com.kamelia.hedera.core.constant

import com.kamelia.hedera.core.response.asMessage

open class ActionMessageContainer(
    prefix: String
) {
    val title = "$prefix.title".asMessage()
    val message = "$prefix.message".asMessage()
}

open class BulkActionMessageContainer(
    private val prefix: String
) {

    val title = "$prefix.title".asMessage()

    fun message(success: Int, fail: Int, total: Int) =
        "$prefix.message".asMessage(
            "success" to success.toString(),
            "fail" to fail.toString(),
            "total" to total.toString(),
        )
}

open class Namespace(open val name: String, private val parent: Namespace? = null) {
    final override fun toString(): String = parent?.let { "$it.$name" } ?: name
}

open class ActionMessage(override val name: String, parent: Namespace) : Namespace(name, parent) {
    val success = ActionMessageContainer("${toString()}.success")
    val fail = ActionMessageContainer("${toString()}.fail")
}

open class BulkActionMessage(override val name: String, parent: Namespace) : Namespace(name, parent) {
    val success = BulkActionMessageContainer("${toString()}.success")
    val partial = BulkActionMessageContainer("${toString()}.partial")
    val fail = BulkActionMessageContainer("${toString()}.fail")
}
