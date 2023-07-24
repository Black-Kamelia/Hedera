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

    object Users {

        private const val PREFIX = "${Actions.PREFIX}.users"

        object Update {

            private const val PREFIX = "${Users.PREFIX}.update"

            object Password {

                private const val PREFIX = "${Update.PREFIX}.password"

                object Success {
                    private const val PREFIX = "${Password.PREFIX}.success"
                    const val MESSAGE = "$PREFIX.message"
                }

                object Failure {
                    private const val PREFIX = "${Password.PREFIX}.failure"
                    const val TITLE = "$PREFIX.title"
                }

            }

        }

    }

}