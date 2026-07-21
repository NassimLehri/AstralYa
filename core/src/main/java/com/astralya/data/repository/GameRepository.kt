package com.astralya.data.repository

import com.astralya.data.dao.*
import com.astralya.data.entities.*
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val heroDao: HeroDao,
    private val inventoryDao: InventoryDao,
    private val questDao: QuestDao,
    private val saveDao: SaveDao
) {
    // ── Heroes ──────────────────────────────────────────────
    val allHeroes: Flow<List<HeroEntity>> = heroDao.getAllHeroes()

    suspend fun getHero(id: String) = heroDao.getHeroById(id)
    suspend fun saveHero(hero: HeroEntity) = heroDao.insertHero(hero)
    suspend fun updateHeroHp(id: String, hp: Int) = heroDao.updateHp(id, hp)
    suspend fun updateHeroMp(id: String, mp: Int) = heroDao.updateMp(id, mp)
    suspend fun levelUpHero(id: String, level: Int, exp: Int) =
        heroDao.updateLevelExp(id, level, exp)

    // ── Inventory ────────────────────────────────────────────
    val allItems: Flow<List<InventoryItemEntity>> = inventoryDao.getAllItems()

    suspend fun addItem(item: InventoryItemEntity) = inventoryDao.insertItem(item)
    suspend fun getItem(id: String) = inventoryDao.getItemById(id)
    suspend fun updateItemQty(id: String, qty: Int) {
        if (qty <= 0) inventoryDao.deleteItem(id)
        else inventoryDao.updateQuantity(id, qty)
    }
    suspend fun getItemsByType(type: String) = inventoryDao.getItemsByType(type)

    // ── Quests ───────────────────────────────────────────────
    val allQuests: Flow<List<QuestEntity>> = questDao.getAllQuests()

    suspend fun saveQuest(quest: QuestEntity) = questDao.insertQuest(quest)
    suspend fun updateQuestProgress(id: String, status: String, step: Int) =
        questDao.updateQuestProgress(id, status, step)
    suspend fun getActiveQuests() = questDao.getQuestsByStatus("IN_PROGRESS")

    // ── Save / Load ─────────────────────────────────────────
    val allSaves: Flow<List<GameSaveEntity>> = saveDao.getAllSaves()

    suspend fun saveGame(save: GameSaveEntity) = saveDao.save(save)
    suspend fun loadGame(slot: Int) = saveDao.getSaveBySlot(slot)
    suspend fun deleteSave(slot: Int) = saveDao.deleteSave(slot)

    // ── Full reset (new game) ────────────────────────────────
    suspend fun resetAll() {
        heroDao.deleteAll()
        inventoryDao.deleteAll()
        questDao.deleteAll()
    }
}
