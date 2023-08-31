package com.kamelia.hedera.core

object Errors {

    private const val PREFIX = "errors"

    const val INSUFFICIENT_PERMISSIONS = "$PREFIX.insufficient_permissions"
    const val ILLEGAL_ACTION = "$PREFIX.illegal_action"

    const val UNKNOWN = "$PREFIX.unknown"

    object Auth {

        private const val PREFIX = "${Errors.PREFIX}.auth"

        const val INVALID_CREDENTIALS = "$PREFIX.invalid_credentials"
        const val ACCOUNT_DISABLED = "$PREFIX.account_disabled"

    }

    object Files {

        private const val PREFIX = "${Errors.PREFIX}.files"

        const val NOT_FOUND = "$PREFIX.not_found"

    }

    object Filters {

        private const val PREFIX = "${Errors.PREFIX}.filters"

        const val UNKNOWN_FIELD = "$PREFIX.unknown_field"
        const val ILLEGAL_FILTER = "$PREFIX.illegal_filter"

    }

    object Headers {

        private const val PREFIX = "${Errors.PREFIX}.headers"

        const val MISSING_HEADER = "$PREFIX.missing_header"

    }

    object Pagination {

        private const val PREFIX = "${Errors.PREFIX}.pagination"

        const val INVALID_PAGE_NUMBER = "$PREFIX.invalid_page_number"
        const val INVALID_PAGE_SIZE = "$PREFIX.invalid_page_size"

    }

    object Parameters {

        private const val PREFIX = "${Errors.PREFIX}.parameters"

        const val MISSING_PARAMETER = "$PREFIX.missing_parameter"

    }

    object Parsing {

        private const val PREFIX = "${Errors.PREFIX}.parsing"

        const val INVALID_UUID = "$PREFIX.invalid_uuid"

    }

    object PersonalTokens {

        private const val PREFIX = "${Errors.PREFIX}.personal_tokens"

        const val NOT_FOUND = "$PREFIX.not_found"

    }

    object Sorts {

        private const val PREFIX = "${Errors.PREFIX}.sorts"

        const val UNKNOWN_FIELD = "$PREFIX.unknown_field"

    }

    object Tokens {

        private const val PREFIX = "${Errors.PREFIX}.tokens"

        const val EXPIRED_OR_INVALID = "$PREFIX.expired_or_invalid"
        const val MISSING_TOKEN = "$PREFIX.missing_token"

    }

    object Uploads {

        private const val PREFIX = "${Errors.PREFIX}.uploads"

        const val TOKEN_GENERATION = "$PREFIX.token_generation"
        const val MULTIPART_PARSE = "$PREFIX.multipart_parse"
        const val MISSING_FILE = "$PREFIX.missing_file"
        const val EMPTY_FILE_NAME = "$PREFIX.empty_file_name"

    }

    object Users {

        private const val PREFIX = "${Errors.PREFIX}.users"

        const val NOT_FOUND = "$PREFIX.not_found"
        const val INSUFFICIENT_DISK_QUOTA = "$PREFIX.insufficient_disk_quota"

        object Email {

            private const val PREFIX = "${Errors.Users.PREFIX}.email"

            const val ALREADY_EXISTS = "$PREFIX.already_exists"
            const val INVALID_EMAIL = "$PREFIX.invalid_email"

        }

        object Password {

            private const val PREFIX = "${Errors.Users.PREFIX}.password"

            const val TOO_SHORT = "$PREFIX.too_short"
            const val TOO_LONG = "$PREFIX.too_long"
            const val INCORRECT_PASSWORD = "$PREFIX.incorrect_password"

        }

        object Username {

            private const val PREFIX = "${Errors.Users.PREFIX}.username"

            const val ALREADY_EXISTS = "$PREFIX.already_exists"
            const val INVALID_USERNAME = "$PREFIX.invalid_username"

        }

    }

}
