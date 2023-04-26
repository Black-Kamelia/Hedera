package com.kamelia.hedera.rest

object Errors {

    private const val PREFIX = "errors"

    object Auth {

        private const val PREFIX = "${Errors.PREFIX}.auth"

        const val INVALID_CREDENTIALS = "$PREFIX.invalid_credentials"
        const val ACCOUNT_DISABLED = "$PREFIX.account_disabled"

    }

}