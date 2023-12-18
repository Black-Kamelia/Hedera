package com.kamelia.hedera.core.constant

object BulkActions : Namespace("bulk_actions") {

    object Files : Namespace("files", this) {

        object Update : Namespace("update", this) {

            object Visibility : BulkActionMessage("visibility", this)

        }

        object Delete : BulkActionMessage("delete", this)
    }
}