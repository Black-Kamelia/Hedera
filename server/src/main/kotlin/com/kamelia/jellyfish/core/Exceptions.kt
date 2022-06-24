package com.kamelia.jellyfish.core

class InvalidUUIDException : Exception("Query param 'uuid' is malformed")

class MissingParameterException(name: String) : Exception("Query param '$name' is missing")