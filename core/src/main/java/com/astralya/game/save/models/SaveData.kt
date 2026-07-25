package com.astralya.game.save.models

import com.astralya.game.entities.*
import com.astralya.game.save.QuestProgress

/**
 * Epic 5 — Modèle de données complet pour la sérialisation JSON.
 */
data class SaveData(
    val version: Int = 1,
    val party: List<Hero> = emptyList(),
    val currentMapId: String = "",
    val playerX: Float = 0f,
    val playerY: Float = 0f,
    val gold: Int = 0,
    val reputation: Int = 0,
    val playtimeSeconds: Long = 0,
    val inventory: Map<String, Int> = emptyMap(),
    val questProgress: Map<String, QuestProgress> = emptyMap(),
    val crystalsFound: Set<String> = emptySet(),
    val defeatedBosses: Set<String> = emptySet(),
    val openedChests: Set<String> = emptySet(),
    val mapState: Map<String, Boolean> = emptyMap(),
    val maxWeight: Float = 50f
)
