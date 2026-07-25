package com.astralya.engine.core

/**
 * EventBus - Communication découplée entre les modules.
 */
class EventBus {
    private val listeners = mutableMapOf<Class<*>, MutableList<(Any) -> Unit>>()

    fun <T : Any> subscribe(eventType: Class<T>, listener: (T) -> Unit) {
        listeners.getOrPut(eventType) { mutableListOf() }.add { listener(it as T) }
    }

    fun publish(event: Any) {
        listeners[event::class.java]?.forEach { it(event) }
    }
}

// --- Événements standardisés ---
data class EnemyDefeatedEvent(val enemyId: String)
data class ItemCollectedEvent(val itemId: String, val quantity: Int)
data class HeroLeveledUpEvent(val heroName: String, val newLevel: Int)
data class QuestProgressEvent(val questId: String, val stepIndex: Int)
