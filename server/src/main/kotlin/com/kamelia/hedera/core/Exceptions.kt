package com.kamelia.hedera.core

import com.kamelia.hedera.core.response.MessageKeyDTO
import com.kamelia.hedera.core.response.asMessage
import com.kamelia.hedera.rest.core.pageable.FilterObject
import com.kamelia.hedera.rest.setting.Locale
import com.kamelia.hedera.util.I18N

open class HederaException(val error: MessageKeyDTO) : Exception(error.key){//}, null, true, false) {

    constructor(key: String) : this(MessageKeyDTO(key))

}

class InvalidUUIDException : HederaException(Errors.Parsing.INVALID_UUID)

class InvalidCredentialsException : HederaException(Errors.Auth.INVALID_CREDENTIALS)

class AccountDisabledException : HederaException(Errors.Auth.ACCOUNT_DISABLED)

class MissingParameterException(parameter: String) :
    HederaException(MessageKeyDTO(Errors.Parameters.MISSING_PARAMETER, "parameter" to parameter.asMessage()))

class MissingHeaderException(header: String) :
    HederaException(MessageKeyDTO(Errors.Headers.MISSING_HEADER, "header" to header.asMessage()))

class MissingTokenException : HederaException(Errors.Tokens.MISSING_TOKEN)

class ExpiredOrInvalidTokenException : HederaException(Errors.Tokens.EXPIRED_OR_INVALID)

class UploadCodeGenerationException : HederaException(Errors.Uploads.TOKEN_GENERATION)

class MultipartParseException : HederaException(Errors.Uploads.MULTIPART_PARSE)

class IllegalFilterException(filter: FilterObject) :
    HederaException(MessageKeyDTO(Errors.Filters.ILLEGAL_FILTER, "filter" to filter.toString().asMessage()))

class UnknownFilterFieldException(field: String) :
    HederaException(MessageKeyDTO(Errors.Filters.UNKNOWN_FIELD, "field" to field.asMessage()))

class UnknownSortFieldException(field: String) :
    HederaException(MessageKeyDTO(Errors.Sorts.UNKNOWN_FIELD, "field" to field.asMessage()))

class IllegalActionException(error: MessageKeyDTO = MessageKeyDTO(Errors.ILLEGAL_ACTION)) : HederaException(error) {

    constructor(key: String) : this(MessageKeyDTO(key))

}

class InsufficientPermissionsException : HederaException(Errors.INSUFFICIENT_PERMISSIONS)

class InsufficientDiskQuotaException(locale: Locale = Locale.en) : HederaException(
    MessageKeyDTO(
        Errors.Users.INSUFFICIENT_DISK_QUOTA,
        humanString = I18N.t("responses.file_upload.errors.quota_exceeded", locale.javaLocale)
    )
)

class FileNotFoundException : HederaException(Errors.Files.NOT_FOUND)

class UserNotFoundException : HederaException(Errors.Users.NOT_FOUND)

class PersonalTokenNotFoundException : HederaException(Errors.PersonalTokens.NOT_FOUND)

class DisabledRegistrationsException : HederaException(Errors.Registrations.DISABLED)

class ThumbnailGenerationException : HederaException(Errors.Thumbnails.GENERATION)

class MailSendingException : HederaException(Errors.MAIL_NOT_SENT)

class PasswordResetMessagingException : HederaException(Errors.Users.ResetPassword.REQUEST_NOT_SENT)

class TooManyPasswordResetRequestsException : HederaException(Errors.Users.ResetPassword.TOO_MANY_REQUESTS)

class InvalidPersonalTokenException(locale: Locale = Locale.en) : HederaException(
    MessageKeyDTO(
        Errors.PersonalTokens.INVALID_TOKEN,
        humanString = I18N.t("responses.file_upload.errors.invalid_personal_token", locale.javaLocale)
    )
)
