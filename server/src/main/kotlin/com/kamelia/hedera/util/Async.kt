package com.kamelia.hedera.util

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private class ReentrantMutexContextElement(
    override val key: ReentrantMutexContextKey
) : CoroutineContext.Element

private data class ReentrantMutexContextKey(
    val mutex: Mutex
) : CoroutineContext.Key<ReentrantMutexContextElement>

/**
 * Executes the given [action] under the context of this [Mutex].
 * It is the reentrant counterpart to [Mutex.withLock].
 * That is to say, if the current coroutine is already holding the lock,
 * the [action] is executed immediately without suspending.
 * Otherwise, the [action] is executed after acquiring the lock.
 * In both cases, the lock is released when the [action] completes.
 *
 * In short, this avoids a deadlock that would happen in this situation:
 * ```
 * val mutex = Mutex()
 *
 * suspend fun foo() = mutex.withLock {
 *     bar()
 * }
 *
 * suspend fun bar() = mutex.withLock {
 *    // If called from `foo`,
 *    // this suspends forever because the coroutine already holds the lock.
 * }
 * ```
 *
 * @param action the action to execute under the context of this [Mutex].
 * @param T the type of the result of [action].
 * @return the result of [action].
 */
suspend fun <T> Mutex.withReentrantLock(action: suspend () -> T): T {
    val key = ReentrantMutexContextKey(this)
    if (coroutineContext[key] != null) return action()
    return withContext(coroutineContext + ReentrantMutexContextElement(key)) {
        withLock { action() }
    }
}

fun CoroutineScope.launchPeriodic(
    delay: Duration,
    immediate: Boolean = false,
    action: suspend () -> Unit
) = launch {
    if (immediate) action()
    while (isActive) {
        delay(delay)
        action()
    }
}
