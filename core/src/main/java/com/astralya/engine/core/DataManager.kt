package com.astralya.engine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.astralya.game.entities.*
import com.astralya.game.quests.Quest

/**
 * Epic 11 — DataManager centralisé pour charger les données depuis JSON.
 */
class DataManager {

    private val json = Json()
    
    private val items = mutableMapOf<String, Item>()
    private val enemies = mutableMapOf<String, Enemy>()
    private val heroSkills = mutableMapOf<String, Skill>()
    private val quests = mutableMapOf<String, Quest>()

    fun loadAll() {
        loadItems()
        loadEnemies()
        loadSkills()
        loadQuests()
    }

    private fun loadItems() {
        try {
            val file = Gdx.files.internal("data/items.json")
            if (file.exists()) {
                val list = json.fromJson(List::class.java, Item::class.java, file) as List<Item>
                list.forEach { items[it.id] = it }
            }
        } catch (e: Exception) {
            Gdx.app.error("DataManager", "Erreur chargement items.json: ${e.message}")
        }
    }

    private fun loadEnemies() {
        try {
            val file = Gdx.files.internal("data/enemies.json")
            if (file.exists()) {
                val list = json.fromJson(List::class.java, Enemy::class.java, file) as List<Enemy>
                list.forEach { enemies[it.id] = it }
            }
        } catch (e: Exception) {
            Gdx.app.error("DataManager", "Erreur chargement enemies.json: ${e.message}")
        }
    }

    private fun loadSkills() {
        try {
            val file = Gdx.files.internal("data/skills.json")
            if (file.exists()) {
                val list = json.fromJson(List::class.java, Skill::class.java, file) as List<Skill>
                list.forEach { heroSkills[it.id] = it }
            }
        } catch (e: Exception) {
            Gdx.app.error("DataManager", "Erreur chargement skills.json: ${e.message}")
        }
    }

    private fun loadQuests() {
        try {
            val file = Gdx.files.internal("data/quests.json")
            if (file.exists()) {
                val list = json.fromJson(List::class.java, Quest::class.java, file) as List<Quest>
                list.forEach { quests[it.id] = it }
                Gdx.app.log("DataManager", "${quests.size} quêtes chargées.")
            }
        } catch (e: Exception) {
            Gdx.app.error("DataManager", "Erreur chargement quests.json: ${e.message}")
        }
    }

    fun getItem(id: String): Item? = items[id]
    fun getAllItems(): Collection<Item> = items.values

    fun getEnemy(id: String): Enemy? = enemies[id]
    fun getAllEnemies(): Collection<Enemy> = enemies.values

    fun getSkill(id: String): Skill? = heroSkills[id]

    fun getQuest(id: String): Quest? = quests[id]
    fun getAllQuests(): Collection<Quest> = quests.values

    /** Pour les tests unitaires uniquement */
    fun forceLoad(
        itemsMap: Map<String, Item> = emptyMap(),
        enemiesMap: Map<String, Enemy> = emptyMap(),
        skillsMap: Map<String, Skill> = emptyMap(),
        questsMap: Map<String, Quest> = emptyMap()
    ) {
        items.putAll(itemsMap)
        enemies.putAll(enemiesMap)
        heroSkills.putAll(skillsMap)
        quests.putAll(questsMap)
    }
}
