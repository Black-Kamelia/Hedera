package com.kamelia.hedera.core

object Actions {

    private const val PREFIX = "actions"

    object Files {

        private const val PREFIX = "${Actions.PREFIX}.files"

        object BulkUpdate {

            private const val PREFIX = "${Files.PREFIX}.bulk_update"

            object Visibility {

                private const val PREFIX = "${BulkUpdate.PREFIX}.visibility"

                object Success {
                    private const val PREFIX = "${Visibility.PREFIX}.success"
                    const val TITLE = "$PREFIX.title"
                    const val MESSAGE = "$PREFIX.message"
                }

            }

        }

        object Update {

            private const val PREFIX = "${Files.PREFIX}.update"

            object Visibility {

                private const val PREFIX = "${Update.PREFIX}.visibility"

                object Success {
                    private const val PREFIX = "${Visibility.PREFIX}.success"
                    const val TITLE = "$PREFIX.title"
                    const val MESSAGE = "$PREFIX.message"
                }

            }

            object Name {

                private const val PREFIX = "${Update.PREFIX}.name"

                object Success {
                    private const val PREFIX = "${Name.PREFIX}.success"
                    const val TITLE = "$PREFIX.title"
                    const val MESSAGE = "$PREFIX.message"
                }

            }

            object CustomLink {

                private const val PREFIX = "${Update.PREFIX}.custom_link"

                object Success {
                    private const val PREFIX = "${CustomLink.PREFIX}.success"
                    const val TITLE = "$PREFIX.title"
                    const val MESSAGE = "$PREFIX.message"
                }

            }

            object RemoveCustomLink {

                private const val PREFIX = "${Update.PREFIX}.remove_custom_link"

                object Success {
                    private const val PREFIX = "${RemoveCustomLink.PREFIX}.success"
                    const val TITLE = "$PREFIX.title"
                    const val MESSAGE = "$PREFIX.message"
                }

            }

        }

        object Upload {

            private const val PREFIX = "${Files.PREFIX}.upload"

             object Success {
                 private const val PREFIX = "${Upload.PREFIX}.success"
                 const val TITLE = "$PREFIX.title"
                 const val MESSAGE = "$PREFIX.message"
             }

        }

        object Delete {

            private const val PREFIX = "${Files.PREFIX}.delete"

            object Success {
                private const val PREFIX = "${Delete.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

        }

    }

    object Tokens {

        private const val PREFIX = "${Actions.PREFIX}.tokens"

        object Create {

            private const val PREFIX = "${Tokens.PREFIX}.create"

            object Success {
                private const val PREFIX = "${Create.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

        }

        object Delete {

            private const val PREFIX = "${Tokens.PREFIX}.delete"

            object Success {
                private const val PREFIX = "${Delete.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

        }

    }

    object Users {

        private const val PREFIX = "${Actions.PREFIX}.users"

        object Create {

            private const val PREFIX = "${Users.PREFIX}.create"

            object Success {
                private const val PREFIX = "${Create.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

            object Error {
                private const val PREFIX = "${Create.PREFIX}.error"
                const val TITLE = "$PREFIX.title"
            }

        }

        object Activate {

            private const val PREFIX = "${Users.PREFIX}.activate"

            object Success {
                private const val PREFIX = "${Activate.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

        }

        object Deactivate {

            private const val PREFIX = "${Users.PREFIX}.deactivate"

            object Success {
                private const val PREFIX = "${Deactivate.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }

        }

        object Update {

            private const val PREFIX = "${Users.PREFIX}.update"

            object Success {
                private const val PREFIX = "${Update.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val TITLE_SELF = "$PREFIX.title_self"
                const val MESSAGE = "$PREFIX.message"
            }

            object Error {
                private const val PREFIX = "${Update.PREFIX}.error"
                const val TITLE = "$PREFIX.title"
            }

        }

        object UpdatePassword {

            private const val PREFIX = "${Users.PREFIX}.update_password"

            object Success {
                private const val PREFIX = "${UpdatePassword.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
            }

            object Error {
                private const val PREFIX = "${UpdatePassword.PREFIX}.error"
                const val TITLE = "$PREFIX.title"
            }

        }

        object Delete {

            private const val PREFIX = "${Users.PREFIX}.delete"

            object Success {
                private const val PREFIX = "${Delete.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
                const val MESSAGE = "$PREFIX.message"
            }
        }
    }
}
