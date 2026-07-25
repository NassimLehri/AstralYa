package com.astralya.game.inventory

import com.astralya.engine.core.*
import com.astralya.game.entities.*
import com.astralya.game.save.GameStateManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class InventoryTest {

    private lateinit var state: GameStateManager
    private lateinit var dataManager: DataManager

    @Before
    fun setUp() {
        stopKoin()
        dataManager = DataManager()
        startKoin {
            modules(module {
                single { dataManager }
            })
        }
        
        dataManager.forceLoad(
            itemsMap = mapOf(
                "test_heavy" to Item("test_heavy", "Heavy Shield", ItemType.ARMOR, "", weight = 40f, rarity = ItemRarity.RARE),
                "test_light" to Item("test_light", "Small Potion", ItemType.CONSUMABLE, "", weight = 0.5f, rarity = ItemRarity.COMMON),
                "test_legend" to Item("test_legend", "Excalibur", ItemType.WEAPON, "", value = 9999, weight = 5f, rarity = ItemRarity.LEGENDARY)
            )
        )
        state = GameStateManager()
    }

    @Test
    fun `calcul du poids total correct`() {
        state.addItem("test_light", 10) // 10 * 0.5 = 5.0kg
        assertEquals("Poids doit être 5.0", 5.0f, state.getTotalWeight(), 0.01f)
    }

    @Test
    fun `empecher ajout si surcharge`() {
        state.maxWeight = 50f
        state.addItem("test_heavy", 1) // 40kg
        val added = state.addItem("test_heavy", 1) // 40 + 40 = 80kg (> 50)
        assertFalse("Ne doit pas pouvoir ajouter le second bouclier", added)
        assertEquals("Doit n'avoir qu'un bouclier", 1, state.getItemCount("test_heavy"))
    }

    @Test
    fun `tri par rarete fonctionne`() {
        val list = listOf("test_light" to 1, "test_legend" to 1, "test_heavy" to 1)
        val sorted = InventorySorter.sort(list, SortType.RARITY)
        assertEquals("Premier doit être Légendaire", "test_legend", sorted[0].first)
        assertEquals("Dernier doit être Commun", "test_light", sorted[2].first)
    }
}
