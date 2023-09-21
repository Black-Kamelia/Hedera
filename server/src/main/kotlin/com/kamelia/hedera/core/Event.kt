package com.kamelia.hedera.core

import java.util.UUID


/**
 * Represents a key that can be used to restrict the ability to emit an event.
 * When an event is created with such a key, only the owners of the key can emit the event.
 */
interface EventOwnerKey

/**
 * Creates a new event owner key.
 */
fun eventOwnerKey(): EventOwnerKey = object : EventOwnerKey {}

/**
 * Represents an event that can be emitted and subscribed to.
 *
 * @param E The type of the event. That is to say, the type of the data that is emitted.
 */
interface Event<E> {

    /**
     * Emits an event.
     *
     * @param event The event data to emit.
     * @param owner The event owner key of this event, or null. If the event was created with an event owner key, then the owner must be the same as the key.
     */
    suspend fun emit(event: E, owner: EventOwnerKey? = null, ignoredSessions: List<UUID> = emptyList())

    /**
     * Emits an event.
     *
     * @param event The event data to emit.
     * @param owner The event owner key of this event, or null. If the event was created with an event owner key, then the owner must be the same as the key.
     */
    suspend operator fun invoke(event: E, owner: EventOwnerKey? = null, ignoredSessions: List<UUID> = emptyList()) = emit(event, owner, ignoredSessions)

    /**
     * Subscribes to this event.
     *
     * @param listener The listener to subscribe.
     * @return A function that can be called to unsubscribe the listener.
     */
    fun subscribe(listener: suspend (E) -> Unit, sessionId: UUID): () -> Unit

    /**
     * Subscribes to this event, but only once. That is to say, the listener will automatically be unsubscribed after one emit.
     *
     * @param listener The listener to subscribe.
     * @return A function that can be called to unsubscribe the listener.
     */
    fun subscribeOnce(listener: suspend (E) -> Unit, sessionId: UUID): () -> Unit


/**
     * Unsubscribes a listener from this event.
     *
     * @param listener The listener to unsubscribe.
     */
    fun unsubscribe(listener: suspend (E) -> Unit)

}

/**
 * Creates a new event.
 *
 * @param key The event owner key of this event, or null. If the key is not null, then only the owners of the key can emit the event.
 * @param E The type of the event. That is to say, the type of the data that is emitted.
 */
fun <E> event(key: EventOwnerKey? = null): Event<E> = object : Event<E> {

    private val listeners = mutableMapOf<suspend (E) -> Unit, UUID>()

    override suspend fun emit(event: E, owner: EventOwnerKey?, ignoredSessions: List<UUID>) {
        if (key != owner) throw IllegalStateException("Event key mismatch")
        listeners.filter { it.value !in ignoredSessions }.forEach { it.key(event) }
    }

    override fun subscribe(listener: suspend (E) -> Unit, sessionId: UUID): () -> Unit {
        listeners[listener] = sessionId
        return { unsubscribe(listener) }
    }

    override fun subscribeOnce(listener: suspend (E) -> Unit, sessionId: UUID): () -> Unit {
        lateinit var onceListener: suspend (E) -> Unit
        onceListener = { event ->
            listener(event)
            listeners.remove(onceListener)
        }
        return subscribe(onceListener, sessionId)
    }

    override fun unsubscribe(listener: suspend (E) -> Unit) {
        listeners.remove(listener)
    }
}
