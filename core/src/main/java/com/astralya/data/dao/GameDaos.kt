package com.astralya.data.dao

import androidx.room.*
import com.astralya.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes")
    fun getAllHeroes(): Flow<List<HeroEntity>>

    @Query("SELECT * FROM heroes WHERE id = :heroId")
    suspend fun getHeroById(heroId: String): HeroEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHero(hero: HeroEntity)

    @Update
    suspend fun updateHero(hero: HeroEntity)

    @Query("UPDATE heroes SET currentHp = :hp WHERE id = :heroId")
    suspend fun updateHp(heroId: String, hp: Int)

    @Query("UPDATE heroes SET currentMp = :mp WHERE id = :heroId")
    suspend fun updateMp(heroId: String, mp: Int)

    @Query("UPDATE heroes SET level = :level, experience = :exp WHERE id = :heroId")
    suspend fun updateLevelExp(heroId: String, level: Int, exp: Int)

    @Query("DELETE FROM heroes")
    suspend fun deleteAll()
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE type = :type")
    suspend fun getItemsByType(type: String): List<InventoryItemEntity>

    @Query("SELECT * FROM inventory_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET quantity = :qty WHERE id = :itemId")
    suspend fun updateQuantity(itemId: String, qty: Int)

    @Query("DELETE FROM inventory_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAll()
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE status = :status")
    suspend fun getQuestsByStatus(status: String): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Query("UPDATE quests SET status = :status, currentStep = :step WHERE id = :questId")
    suspend fun updateQuestProgress(questId: String, status: String, step: Int)

    @Query("DELETE FROM quests")
    suspend fun deleteAll()
}

@Dao
interface SaveDao {
    @Query("SELECT * FROM game_saves ORDER BY slot ASC")
    fun getAllSaves(): Flow<List<GameSaveEntity>>

    @Query("SELECT * FROM game_saves WHERE slot = :slot")
    suspend fun getSaveBySlot(slot: Int): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(gameSave: GameSaveEntity)

    @Query("DELETE FROM game_saves WHERE slot = :slot")
    suspend fun deleteSave(slot: Int)
}
