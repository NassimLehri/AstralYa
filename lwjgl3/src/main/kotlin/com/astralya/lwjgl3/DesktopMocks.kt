package com.astralya.lwjgl3

import com.astralya.data.dao.*
import com.astralya.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockHeroDao : HeroDao {
    override fun getAllHeroes(): Flow<List<HeroEntity>> = flowOf(emptyList())
    override suspend fun getHeroById(heroId: String): HeroEntity? = null
    override suspend fun insertHero(hero: HeroEntity) {}
    override suspend fun updateHero(hero: HeroEntity) {}
    override suspend fun updateHp(heroId: String, hp: Int) {}
    override suspend fun updateMp(heroId: String, mp: Int) {}
    override suspend fun updateLevelExp(heroId: String, level: Int, exp: Int) {}
    override suspend fun deleteAll() {}
}

class MockInventoryDao : InventoryDao {
    override fun getAllItems(): Flow<List<InventoryItemEntity>> = flowOf(emptyList())
    override suspend fun getItemsByType(type: String): List<InventoryItemEntity> = emptyList()
    override suspend fun getItemById(itemId: String): InventoryItemEntity? = null
    override suspend fun insertItem(item: InventoryItemEntity) {}
    override suspend fun updateQuantity(itemId: String, qty: Int) {}
    override suspend fun deleteItem(itemId: String) {}
    override suspend fun deleteAll() {}
}

class MockQuestDao : QuestDao {
    override fun getAllQuests(): Flow<List<QuestEntity>> = flowOf(emptyList())
    override suspend fun getQuestsByStatus(status: String): List<QuestEntity> = emptyList()
    override suspend fun insertQuest(quest: QuestEntity) {}
    override suspend fun updateQuestProgress(questId: String, status: String, step: Int) {}
    override suspend fun deleteAll() {}
}

class MockSaveDao : SaveDao {
    override fun getAllSaves(): Flow<List<GameSaveEntity>> = flowOf(emptyList())
    override suspend fun getSaveBySlot(slot: Int): GameSaveEntity? = null
    override suspend fun save(gameSave: GameSaveEntity) {}
    override suspend fun deleteSave(slot: Int) {}
}
