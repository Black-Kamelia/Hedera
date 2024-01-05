package com.kamelia.hedera.core.constant

object Actions : Namespace("actions") {

    object Files : Namespace("files", this) {

        object Update : Namespace("update", this) {

            object Visibility : ActionMessage("visibility", this)
            object Name : ActionMessage("name", this)
            object CustomLink : ActionMessage("custom_link", this)
            object RemoveCustomLink : ActionMessage("remove_custom_link", this)

        }

        object Upload : ActionMessage("upload", this)
        object Delete : ActionMessage("delete", this)

    }

    object Maintenance : Namespace("maintenance", this) {

        object ClearThumbnailCache : ActionMessage("clear_thumbnail_cache", this)

    }

    object Tokens : Namespace("tokens", this) {

        object Create : ActionMessage("create", this)
        object Delete : ActionMessage("delete", this)

    }

    object Users : Namespace("users", this) {

        object Create : ActionMessage("create", this)
        object Activate : ActionMessage("activate", this)
        object Deactivate : ActionMessage("deactivate", this)
        object SelfUpdate : ActionMessage("self_update", this)
        object Update : ActionMessage("update", this)
        object UpdatePassword : ActionMessage("update_password", this)
        object Delete : ActionMessage("delete", this)

    }
}
