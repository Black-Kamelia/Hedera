package com.kamelia.hedera.core

object Errors {

    private const val PREFIX = "errors"

    const val INSUFFICIENT_PERMISSIONS = "$PREFIX.insufficient_permissions"
    const val ILLEGAL_ACTION = "$PREFIX.illegal_action"

    const val BAD_REQUEST_RAW = "$PREFIX.bad_request_raw"
    const val NOT_FOUND_RAW = "$PREFIX.not_found_raw"
    const val UNKNOWN = "$PREFIX.unknown"
    const val UNKNOWN_MESSAGE = "$PREFIX.unknown_message"

    const val MAIL_NOT_SENT = "$PREFIX.mail_not_sent"

    object Auth {

        private const val PREFIX = "${Errors.PREFIX}.auth"

        const val INVALID_CREDENTIALS = "$PREFIX.invalid_credentials"
        const val ACCOUNT_DISABLED = "$PREFIX.account_disabled"

    }

    object Configuration {

        private const val PREFIX = "${Errors.PREFIX}.configuration"

        const val GENERATION_ERROR = "$PREFIX.generation_error"
        const val PARSE_ERROR = "$PREFIX.parse_error"
        const val WRITE_ERROR = "$PREFIX.write_error"

    }

    object Files {

        private const val PREFIX = "${Errors.PREFIX}.files"

        const val NOT_FOUND = "$PREFIX.not_found"

        object Name {

            private const val PREFIX = "${Errors.Files.PREFIX}.name"

            const val MISSING_NAME = "$PREFIX.missing_name"
            const val NAME_TOO_LONG = "$PREFIX.name_too_long"

        }

        object CustomLink {

            private const val PREFIX = "${Errors.Files.PREFIX}.custom_link"

            const val ALREADY_EXISTS = "$PREFIX.already_exists"
            const val INVALID_FORMAT = "$PREFIX.invalid_format"
            const val MISSING_SLUG = "$PREFIX.missing_slug"

        }

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

    object Registrations {

        private const val PREFIX = "${Errors.PREFIX}.registrations"

        const val DISABLED = "$PREFIX.disabled"

    }

    object Sorts {

        private const val PREFIX = "${Errors.PREFIX}.sorts"

        const val UNKNOWN_FIELD = "$PREFIX.unknown_field"

    }

    object Thumbnails {

        private const val PREFIX = "${Errors.PREFIX}.thumbnails"

        const val GENERATION = "$PREFIX.generation"

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
        const val FORCE_CHANGE_PASSWORD = "$PREFIX.force_change_password"
        const val FORCE_CHANGE_PASSWORD_CONFLICT = "$PREFIX.force_change_password_conflict"
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
            const val MISSING_OLD_PASSWORD = "$PREFIX.missing_old_password"

        }

        object ResetPassword {

            private const val PREFIX = "${Errors.Users.PREFIX}.reset_password"

            const val REQUEST_NOT_SENT = "$PREFIX.request_not_sent"
            const val TOKEN_NOT_FOUND = "$PREFIX.token_not_found"
            const val TOKEN_EXPIRED = "$PREFIX.token_expired"
            const val TOO_MANY_REQUESTS = "$PREFIX.too_many_requests"

        }

        object Username {

            private const val PREFIX = "${Errors.Users.PREFIX}.username"

            const val ALREADY_EXISTS = "$PREFIX.already_exists"
            const val INVALID_USERNAME = "$PREFIX.invalid_username"

        }

        object DiskQuota {

            private const val PREFIX = "${Errors.Users.PREFIX}.disk_quota"

            const val INVALID_DISK_QUOTA = "$PREFIX.invalid_disk_quota"
        }

    }

}
