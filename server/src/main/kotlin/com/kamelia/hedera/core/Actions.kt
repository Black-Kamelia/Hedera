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

}