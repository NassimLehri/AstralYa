package com.astralya.game.save

import com.badlogic.gdx.utils.Json
import com.astralya.game.save.models.SaveData
import com.astralya.game.save.entities.GameSaveEntity
import com.astralya.game.save.repository.GameRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * Epic 5 — Orchestrateur pour la sauvegarde et le chargement.
 */
class SaveManager(
    private val repository: GameRepository,
    private val stateManager: GameStateManager
) {
    private val json = Json()
    private val CURRENT_VERSION = 1

    suspend fun save(slot: Int, saveName: String) = withContext(Dispatchers.IO) {
        val data = SaveData(
            version = CURRENT_VERSION,
            party = stateManager.party.toList(),
            currentMapId = stateManager.currentMapId,
            playerX = stateManager.playerX,
            playerY = stateManager.playerY,
            gold = stateManager.gold,
            reputation = stateManager.reputation,
            playtimeSeconds = stateManager.playtimeSeconds,
            inventory = stateManager.inventory.toMap(),
            questProgress = stateManager.questProgress.toMap(),
            crystalsFound = stateManager.crystalsFound.toSet(),
            defeatedBosses = stateManager.defeatedBosses.toSet(),
            openedChests = stateManager.openedChests.toSet(),
            mapState = stateManager.mapState.toMap(),
            maxWeight = stateManager.maxWeight
        )

        val blob = json.toJson(data)
        
        val entity = GameSaveEntity(
            slot = slot,
            saveName = saveName,
            currentMapId = data.currentMapId,
            playerX = data.playerX,
            playerY = data.playerY,
            gold = data.gold,
            playtimeSeconds = data.playtimeSeconds,
            version = data.version,
            jsonBlob = blob
        )
        
        repository.saveGame(entity)
    }

    suspend fun load(slot: Int): Boolean = withContext(Dispatchers.IO) {
        val entity = repository.loadGame(slot) ?: return@withContext false
        
        var jsonBlob = entity.jsonBlob
        if (entity.version < CURRENT_VERSION) {
            jsonBlob = SaveMigrationManager.migrate(jsonBlob, entity.version, CURRENT_VERSION)
        }
        
        val data = try {
            json.fromJson(SaveData::class.java, jsonBlob)
        } catch (e: Exception) {
            return@withContext false
        }

        // Restore State
        withContext(Dispatchers.Main) {
            stateManager.apply {
                party.clear(); party.addAll(data.party)
                currentMapId = data.currentMapId
                playerX = data.playerX
                playerY = data.playerY
                gold = data.gold
                reputation = data.reputation
                playtimeSeconds = data.playtimeSeconds
                inventory.clear(); inventory.putAll(data.inventory)
                questProgress.clear(); questProgress.putAll(data.questProgress)
                crystalsFound.clear(); crystalsFound.addAll(data.crystalsFound)
                defeatedBosses.clear(); defeatedBosses.addAll(data.defeatedBosses)
                openedChests.clear(); openedChests.addAll(data.openedChests)
                mapState.clear(); mapState.putAll(data.mapState)
                maxWeight = data.maxWeight
            }
        }
        true
    }
}
