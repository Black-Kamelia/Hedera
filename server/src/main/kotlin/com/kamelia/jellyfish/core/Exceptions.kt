package com.kamelia.jellyfish.core

class InvalidUUIDException : Exception("Query param 'uuid' is malformed")

class MissingParameterException(name: String) : Exception("Query param '$name' is missing")

class ExpiredOrInvalidTokenException : Exception("errors.tokens.expired_or_invalid")

class UploadCodeGenerationException : Exception("errors.uploads.code_generation")
