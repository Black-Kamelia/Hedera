package com.kamelia.jellyfish.core

class InvalidUUIDException : Exception("Query param 'uuid' is malformed")

class MissingParameterException(name: String) : Exception("Query param '$name' is missing")

class ExpiredOrInvalidTokenException : Exception("Token is expired or invalid")