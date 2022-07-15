package com.kamelia.jellyfish.core

class InvalidUUIDException : Exception("errors.uuid.invalid")

class MissingParameterException(name: String) : Exception("errors.parameters.missing.`$name`")

class MissingHeaderException(header: String) : Exception("errors.headers.missing.`$header`")

class ExpiredOrInvalidTokenException : Exception("errors.tokens.expired_or_invalid")

class UploadCodeGenerationException : Exception("errors.uploads.code_generation")
