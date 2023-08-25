package com.kamelia.hedera.core

object Actions {

    private const val PREFIX = "actions"

    object Files {

        private const val PREFIX = "${Actions.PREFIX}.files"

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
                const val MESSAGE = "$PREFIX.message"
            }

        }

        object UpdatePassword {

            private const val PREFIX = "${Users.PREFIX}.update_password"

            object Success {
                private const val PREFIX = "${UpdatePassword.PREFIX}.success"
                const val TITLE = "$PREFIX.title"
            }

            object Failure {
                private const val PREFIX = "${UpdatePassword.PREFIX}.failure"
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