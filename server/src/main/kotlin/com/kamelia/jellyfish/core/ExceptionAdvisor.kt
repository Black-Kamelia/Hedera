package com.kamelia.jellyfish.core

import io.ktor.server.application.ApplicationCall
import kotlin.reflect.KClass

class ExceptionAdvisor<out T : Throwable>(
    val throwableClass: KClass<@UnsafeVariance T>,
    val handle: suspend (@UnsafeVariance T, ApplicationCall) -> Unit
)

inline fun <reified T : Throwable> exceptionAdvisor(noinline handle: suspend (T, ApplicationCall) -> Unit) =
    ExceptionAdvisor(T::class, handle)
