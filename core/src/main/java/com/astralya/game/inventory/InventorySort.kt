package com.astralya.game.inventory

import com.astralya.game.entities.Item
import com.astralya.game.entities.ItemFactory
import com.astralya.game.entities.ItemRarity

enum class SortType { NAME, VALUE, WEIGHT, RARITY }

/**
 * Epic 3 — Utilitaires de tri pour l'inventaire.
 */
object InventorySorter {

    fun sort(items: List<Pair<String, Int>>, type: SortType): List<Pair<String, Int>> {
        return when (type) {
            SortType.NAME -> items.sortedBy { ItemFactory.getById(it.first)?.name ?: "" }
            SortType.VALUE -> items.sortedByDescending { ItemFactory.getById(it.first)?.value ?: 0 }
            SortType.WEIGHT -> items.sortedByDescending { ItemFactory.getById(it.first)?.weight ?: 0f }
            SortType.RARITY -> items.sortedByDescending { ItemFactory.getById(it.first)?.rarity?.ordinal ?: 0 }
        }
    }
}
