package com.kamelia.hedera.core

import io.ktor.http.*

class ValidationException(
    val errors: Map<String, MessageKeyDTO>
) : Exception()

/**
 * A scope that allows to raise errors during the execution of a block of code.
 */
class ValidationScope {

    private val errors: MutableMap<String, MessageKeyDTO> = mutableMapOf()

    /**
     * Raise an error for the given [fieldName] with the given [error].
     */
    fun raiseError(fieldName: String, error: MessageKeyDTO) {
        errors[fieldName] = error
    }

    /**
     * Raise an error for the given [fieldName] with the given [error].
     */
    fun raiseError(fieldName: String, error: String) {
        errors[fieldName] = error.asMessage()
    }

    /**
     * Returns `true` if there are any raised errors, `false` otherwise.
     */
    fun hasErrors() = errors.isNotEmpty()

    /**
     * Returns the errors raised during the execution of the block of code.
     */
    fun getErrors() = errors.toMap()

    /**
     * Force the validation to fail if any error was raised during the execution of the block of code.
     */
    fun catchErrors() {
        if (hasErrors()) {
            throw ValidationException(errors)
        }
    }
}

/**
 * Create a [Response] of type [R] with the given [errorTemplate], [statusCode] and [errors].
 */
private inline fun <T, reified R : Response<T>> getErrorResponse(
    errorTemplate: MessageDTO<*>,
    statusCode: HttpStatusCode,
    errors: Map<String, MessageKeyDTO>,
): R {
    val error = errorTemplate.copy(fields = errors)
    return (when (R::class) {
        ActionResponse::class -> ActionResponse<Nothing>(statusCode, error = ResultData(error))
        else -> Response<T>(statusCode, error = ResultData(error))
    }) as R
}

/**
 * Supervises the execution of a block of code and validates the errors raised by the [ValidationScope].
 * During the execution of the block, the [ValidationScope] is available to raise errors. If at the end of the
 * execution of the block the [ValidationScope] contains errors, errors are returned as a [Response] of the same type
 * as the return type of the block.
 *
 * If the block needs to ensure no errors are raised, it can call [ValidationScope.catchErrors] at the end of the
 * validation step to force the validation to fail if any error was raised.
 *
 * Examples:
 * ```kt
 * validate {
 *   // Raising an error and continue execution
 *   if (someCondition == false) {
 *      raiseError("someCondition", "Some condition is false")
 *   }
 *
 *   // This will be the response if the validation succeeds
 *   Response.ok("Everything went fine")
 * }
 * ```
 *
 * ```kt
 * validate {
 *   // Raising an error and continue execution
 *   if (username == null) {
 *      raiseError("username", "Username is required")
 *   }
 *
 *   // At this point, the validation will fail if any error was raised
 *   catchErrors()
 *
 *   // Code safe from errors
 *   val user = User.create(username)
 *   Response.created(user)
 * }
 * ```
 *
 * **Note:** The user is responsible for catching any exception raised by the block if needed.
 *
 * @param errorTemplate The template of the error to return if the validation fails.
 * @param statusCode The status code of the response to return if the validation fails.
 * @param block The block of code to execute.
 */
internal inline fun <T, reified R : Response<T>> validate(
    errorTemplate: MessageDTO<*> = MessageDTO.simple(Errors.UNKNOWN.asMessage()),
    statusCode: HttpStatusCode = HttpStatusCode.Forbidden,
    block: ValidationScope.() -> R
): R {
    val scope = ValidationScope()
    return try {
        val response = block(scope)
        if (scope.hasErrors()) {
            getErrorResponse<T, R>(errorTemplate, statusCode, scope.getErrors())
        }
        response
    } catch (e: ValidationException) {
        getErrorResponse<T, R>(errorTemplate, statusCode, scope.getErrors())
    }
}
