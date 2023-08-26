package com.kamelia.hedera.core

import com.kamelia.hedera.rest.core.DTO
import io.ktor.http.*

class ValidationException : Exception()

/**
 * A scope that allows to raise errors during the execution of a block of code.
 */
class ValidationScope {

    private val errors: MutableMap<String, Pair<MessageKeyDTO, HttpStatusCode?>> = mutableMapOf()

    /**
     * Raise an error for the given [fieldName] with the given [error].
     */
    fun raiseError(fieldName: String, error: MessageKeyDTO, statusCode: HttpStatusCode? = null) {
        errors[fieldName] = error to statusCode
    }

    /**
     * Raise an error for the given [fieldName] with the given [error].
     */
    fun raiseError(fieldName: String, error: String, statusCode: HttpStatusCode? = null) =
        raiseError(fieldName, error.asMessage(), statusCode)

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
            throw ValidationException()
        }
    }
}

/**
 * Determines the status code to use for the response based on the [errors] and the [defaultStatusCode].
 *
 * If all the errors have the same status code, that status code is returned.
 * If there are no status code specified on any error, the [defaultStatusCode] is returned.
 * If there are multiple status codes specified, [HttpStatusCode.BadRequest] is returned.
 */
private fun getStatusCode(
    errors: Map<String, Pair<MessageKeyDTO, HttpStatusCode?>>,
    defaultStatusCode: HttpStatusCode,
): HttpStatusCode {
    val statusCodes = errors.values.mapNotNull { it.second }.toSet()
    if (statusCodes.isEmpty()) return defaultStatusCode
    if (statusCodes.size == 1) return statusCodes.first()

    return HttpStatusCode.BadRequest
}

/**
 * Create a [Response] of type [R] with the given [errorTemplate], [statusCode] and [errors].
 */
private inline fun <T, reified R : Response<T>> getErrorResponse(
    errorTemplate: MessageDTO<out DTO>,
    defaultStatusCode: HttpStatusCode,
    errors: Map<String, Pair<MessageKeyDTO, HttpStatusCode?>>,
): R {
    val error = errorTemplate.copy(fields = errors.mapValues { it.value.first })
    val statusCode = getStatusCode(errors, defaultStatusCode)
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
    errorTemplate: MessageDTO<out DTO> = MessageDTO.simple(Errors.UNKNOWN.asMessage()),
    defaultStatusCode: HttpStatusCode = HttpStatusCode.BadRequest,
    block: ValidationScope.() -> R
): R {
    val scope = ValidationScope()
    return try {
        val response = block(scope)
        if (scope.hasErrors()) {
            getErrorResponse<T, R>(errorTemplate, defaultStatusCode, scope.getErrors())
        }
        response
    } catch (e: ValidationException) {
        getErrorResponse<T, R>(errorTemplate, defaultStatusCode, scope.getErrors())
    }
}
