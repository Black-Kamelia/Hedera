package com.kamelia.hedera.core

import com.kamelia.hedera.rest.core.pageable.FilterObject

open class HederaException(val error: MessageKeyDTO) : Exception() {

    constructor(key: String) : this(MessageKeyDTO.of(key))

}

class InvalidUUIDException : HederaException(Errors.Parsing.INVALID_UUID)

class MissingParameterException(parameter: String) : HederaException(MessageKeyDTO.of(Errors.Parameters.MISSING_PARAMETER, "parameter" to parameter))

class MissingHeaderException(header: String) : HederaException(MessageKeyDTO.of(Errors.Headers.MISSING_HEADER, "header" to header))

class MissingTokenException : HederaException(Errors.Tokens.MISSING_TOKEN)

class ExpiredOrInvalidTokenException : HederaException(Errors.Tokens.EXPIRED_OR_INVALID)

class UploadCodeGenerationException : HederaException(Errors.Uploads.TOKEN_GENERATION)

class MultipartParseException : HederaException(Errors.Uploads.MULTIPART_PARSE)

class IllegalFilterException(filter: FilterObject) : HederaException(MessageKeyDTO.of(Errors.Filters.ILLEGAL_FILTER, "filter" to filter.toString()))

class UnknownFilterFieldException(field: String) : HederaException(MessageKeyDTO.of(Errors.Filters.UNKNOWN_FIELD, "field" to field))

class UnknownSortFieldException(field: String) : HederaException(MessageKeyDTO.of(Errors.Sorts.UNKNOWN_FIELD, "field" to field))

class IllegalActionException : HederaException(Errors.ILLEGAL_ACTION)

class InsufficientPermissionsException : HederaException(Errors.INSUFFICIENT_PERMISSIONS)

class FileNotFoundException : HederaException(Errors.Files.NOT_FOUND)
