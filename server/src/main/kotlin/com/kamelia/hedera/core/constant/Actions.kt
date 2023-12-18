package com.kamelia.hedera.core.constant

object Actions : Namespace("actions") {

    object Files : Namespace("files", this) {

        object Update : Namespace("update", this) {

            object Visibility : ActionMessage("visibility", this)

        }
    }
}