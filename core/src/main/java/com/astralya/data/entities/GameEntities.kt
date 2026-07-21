package com.astralya.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heroes")
data class HeroEntity(
    @PrimaryKey val id: String,
    val name: String,
    val heroClass: String,
    val level: Int = 1,
    val experience: Int = 0,
    val maxHp: Int,
    val currentHp: Int,
    val maxMp: Int,
    val currentMp: Int,
    val attack: Int,
    val defense: Int,
    val agility: Int,
    val magic: Int,
    val weaponId: String = "",
    val armorId: String = "",
    val accessoryId: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,       // WEAPON, ARMOR, CONSUMABLE, KEY_ITEM
    val description: String,
    val quantity: Int = 1,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val magicBonus: Int = 0,
    val hpRestore: Int = 0,
    val mpRestore: Int = 0,
    val value: Int = 0,
    val equipableBy: String = ""  // comma-separated hero IDs
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val status: String = "NOT_STARTED", // NOT_STARTED, IN_PROGRESS, COMPLETED
    val currentStep: Int = 0,
    val totalSteps: Int = 1,
    val rewardGold: Int = 0,
    val rewardExp: Int = 0,
    val rewardItemId: String = ""
)

@Entity(tableName = "game_saves")
data class GameSaveEntity(
    @PrimaryKey val slot: Int,
    val saveName: String,
    val currentMapId: String,
    val playerX: Float,
    val playerY: Float,
    val gold: Int = 0,
    val playtimeSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val crystalsFound: String = "",   // comma-separated crystal IDs
    val defeatedBosses: String = ""   // comma-separated boss IDs
)
