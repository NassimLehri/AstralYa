package com.astralya.data

import com.astralya.entities.*
import com.astralya.map.QuestRegistry

enum class QuestStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

data class QuestProgress(
    val questId: String,
    var status: QuestStatus = QuestStatus.NOT_STARTED,
    var currentStep: Int = 0
)

class GameState {

    companion object {
        const val STARTING_MAP_ID = "village_depart"
        const val STARTING_X = 200f
        const val STARTING_Y = 200f
    }

    val party: MutableList<Hero>   = mutableListOf()
    var currentMapId: String       = STARTING_MAP_ID
    var playerX: Float             = STARTING_X
    var playerY: Float             = STARTING_Y
    var gold: Int                  = 100
    var playtimeSeconds: Long      = 0L
    val inventory: MutableMap<String, Int>              = mutableMapOf()
    val questProgress: MutableMap<String, QuestProgress> = mutableMapOf()
    val crystalsFound: MutableSet<String>               = mutableSetOf()
    val defeatedBosses: MutableSet<String>              = mutableSetOf()
    val openedChests: MutableSet<String>                = mutableSetOf()

    fun newGame() {
        party.clear(); party.addAll(HeroFactory.createDefaultParty())
        currentMapId = STARTING_MAP_ID; playerX = STARTING_X; playerY = STARTING_Y
        gold = 100; playtimeSeconds = 0L
        inventory.clear(); questProgress.clear()
        crystalsFound.clear(); defeatedBosses.clear(); openedChests.clear()
        ItemFactory.startingInventory().forEach { (itemId, qty) -> inventory[itemId] = qty }
        startQuest("quete_principale_1")
    }

    fun addItem(itemId: String, qty: Int = 1)  { inventory[itemId] = (inventory[itemId] ?: 0) + qty }

    fun removeItem(itemId: String, qty: Int = 1): Boolean {
        val current = inventory[itemId] ?: return false
        if (current < qty) return false
        val newQty = current - qty
        if (newQty == 0) inventory.remove(itemId) else inventory[itemId] = newQty
        return true
    }

    fun hasItem(itemId: String, qty: Int = 1) = (inventory[itemId] ?: 0) >= qty
    fun getItemCount(itemId: String)           = inventory[itemId] ?: 0

    fun startQuest(questId: String) {
        if (questProgress[questId] == null)
            questProgress[questId] = QuestProgress(questId, QuestStatus.IN_PROGRESS, 0)
    }

    /**
     * FIX REVIEW #8 — retourne LevelUpEvent pour notifier l'UI.
     * FIX PERF #6 — gainExp prend rng en paramètre.
     */
    fun advanceQuest(questId: String, rng: com.astralya.utils.GameRandom): Boolean {
        val progress = questProgress[questId] ?: return false
        val quest    = QuestRegistry.getQuest(questId) ?: return false
        progress.currentStep++
        if (progress.currentStep >= quest.steps.size) {
            progress.status = QuestStatus.COMPLETED
            gold += quest.rewardGold
            val share = quest.rewardExp / party.size.coerceAtLeast(1)
            party.forEach { it.gainExp(share, rng) }
            if (quest.rewardItemId.isNotEmpty()) addItem(quest.rewardItemId)
            return true
        }
        return false
    }

    fun isQuestCompleted(q: String) = questProgress[q]?.status == QuestStatus.COMPLETED
    fun isQuestActive(q: String)    = questProgress[q]?.status == QuestStatus.IN_PROGRESS
    fun getActiveQuests()           = questProgress.values.filter { it.status == QuestStatus.IN_PROGRESS }

    /**
     * FIX REVIEW #8 — retourne la liste des héros qui ont level-up.
     * L'UI peut ainsi afficher "NIVEAU SUPÉRIEUR !" pour chacun.
     */
    fun applyCombatRewards(
        exp: Int, goldAmount: Int, itemIds: List<String>,
        rng: com.astralya.utils.GameRandom
    ): List<Hero> {
        gold += goldAmount
        val alive    = party.filter { it.isAlive }
        val share    = exp / alive.size.coerceAtLeast(1)
        val levelUps = mutableListOf<Hero>()
        alive.forEach { hero ->
            if (hero.gainExp(share, rng)) levelUps += hero
        }
        itemIds.forEach { addItem(it) }
        return levelUps   // l'appelant peut afficher "Nassim monte au niveau X !"
    }

    fun getHero(id: HeroId)         = party.firstOrNull { it.id == id }
    fun isGameOver()                = party.none { it.isAlive }
    fun hasDefeatedBoss(id: String) = defeatedBosses.contains(id)
    fun defeatBoss(id: String)      = defeatedBosses.add(id)
    fun openChest(id: String)       = openedChests.add(id)
    fun isChestOpened(id: String)   = openedChests.contains(id)
    fun findCrystal(id: String)     = crystalsFound.add(id)
    val allCrystalsFound get()      = crystalsFound.size >= 7
}
