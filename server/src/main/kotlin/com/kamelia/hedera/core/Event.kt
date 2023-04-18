package com.kamelia.hedera.core


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
    fun emit(event: E, owner: EventOwnerKey? = null)

    /**
     * Emits an event.
     *
     * @param event The event data to emit.
     * @param owner The event owner key of this event, or null. If the event was created with an event owner key, then the owner must be the same as the key.
     */
    operator fun invoke(event: E, owner: EventOwnerKey? = null) = emit(event, owner)

    /**
     * Subscribes to this event.
     *
     * @param listener The listener to subscribe.
     * @return A function that can be called to unsubscribe the listener.
     */
    fun subscribe(listener: (E) -> Unit): () -> Unit

    /**
     * Subscribes to this event.
     *
     * @param listener The listener to subscribe.
     * @return A function that can be called to unsubscribe the listener.
     */
    operator fun plusAssign(listener: (E) -> Unit) {
        subscribe(listener)
    }

    /**
     * Subscribes to this event, but only once. That is to say, the listener will automatically be unsubscribed after one emit.
     *
     * @param listener The listener to subscribe.
     * @return A function that can be called to unsubscribe the listener.
     */
    fun subscribeOnce(listener: (E) -> Unit): () -> Unit


/**
     * Unsubscribes a listener from this event.
     *
     * @param listener The listener to unsubscribe.
     */
    fun unsubscribe(listener: (E) -> Unit)

    /**
     * Unsubscribes a listener from this event.
     *
     * @param listener The listener to unsubscribe.
     */
    operator fun minusAssign(listener: (E) -> Unit) {
        unsubscribe(listener)
    }
}

/**
 * Creates a new event.
 *
 * @param key The event owner key of this event, or null. If the key is not null, then only the owners of the key can emit the event.
 * @param E The type of the event. That is to say, the type of the data that is emitted.
 */
fun <E> event(key: EventOwnerKey? = null): Event<E> = object : Event<E> {

    private val listeners = mutableSetOf<(E) -> Unit>()

    override fun emit(event: E, owner: EventOwnerKey?) {
        if (key != owner) throw IllegalStateException("Event key mismatch")
        listeners.forEach { it(event) }
    }

    override fun subscribe(listener: (E) -> Unit): () -> Unit {
        listeners.add(listener)
        return { unsubscribe(listener) }
    }

    override fun subscribeOnce(listener: (E) -> Unit): () -> Unit {
        lateinit var onceListener: (E) -> Unit
        onceListener = { event ->
            listener(event)
            listeners.remove(onceListener)
        }
        return subscribe(onceListener)
    }

    override fun unsubscribe(listener: (E) -> Unit) {
        listeners.remove(listener)
    }
}
