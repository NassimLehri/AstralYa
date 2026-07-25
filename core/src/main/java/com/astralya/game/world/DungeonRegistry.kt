package com.astralya.game.world

/**
 * Epic 15 — Catalogue des donjons du jeu.
 */
class DungeonRegistry {

    val GROTTE_DUNGEON = Dungeon(
        id = "dungeon_grotte",
        name = "Profondeurs Cristallines",
        roomIds = listOf("grotte_cristal", "sanctuaire_secret"),
        entryMapId = "grotte_cristal"
    )

    private val allDungeons = mapOf(
        GROTTE_DUNGEON.id to GROTTE_DUNGEON
    )

    fun getDungeon(id: String): Dungeon? = allDungeons[id]
}
