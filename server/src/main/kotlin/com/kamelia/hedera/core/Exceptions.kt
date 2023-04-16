package com.kamelia.hedera.core

import com.kamelia.hedera.rest.core.pageable.FilterObject

open class HederaException(val error: ErrorDTO) : Exception()

class InvalidUUIDException : HederaException(ErrorDTO.of("errors.uuid.invalid"))

class MissingParameterException(parameter: String) : HederaException(ErrorDTO.of("errors.parameters.missing", "parameter" to parameter))

class MissingHeaderException(header: String) : HederaException(ErrorDTO.of("errors.headers.missing", "header" to header))

class MissingTokenException : HederaException(ErrorDTO.of("errors.tokens.missing"))

class ExpiredOrInvalidTokenException : HederaException(ErrorDTO.of("errors.tokens.expired_or_invalid"))

class UploadCodeGenerationException : HederaException(ErrorDTO.of("errors.uploads.code_generation"))

class MultipartParseException : HederaException(ErrorDTO.of("errors.uploads.multipart_parse"))

class IllegalFilterException(filter: FilterObject) : HederaException(ErrorDTO.of("errors.filters.illegal", "filter" to filter.toString()))

class UnknownFilterFieldException(field: String) : HederaException(ErrorDTO.of("errors.filters.unknown_field", "field" to field))

class IllegalActionException : HederaException(ErrorDTO.of("errors.actions.illegal"))

class InsufficientPermissionsException : HederaException(ErrorDTO.of("errors.permissions.insufficient"))
