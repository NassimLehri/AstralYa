package com.astralya.ui.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.astralya.AstralYaGame
import com.astralya.ui.components.GameWindow
import com.badlogic.gdx.utils.Json
import com.astralya.game.entities.*
import com.astralya.game.quests.*
import java.util.Locale

/**
 * Epic 17 — Écran d'édition des données (PC uniquement).
 */
class DataEditorScreen(private val game: AstralYaGame) : Screen {

    private val window = GameWindow(10f, 10f, 780f, 460f, "ÉDITEUR DE DONNÉES")
    private var mode = "ITEMS" // "ITEMS", "ENEMIES", "QUESTS"
    
    private var selectedIndex = 0
    private val json = Json()

    override fun show() {
        game.uiManager.clear()
        game.uiManager.addHUD(window)
        window.isFocused = true
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        handleInput()
        
        game.uiManager.update(delta)
        game.uiManager.draw(game.batch, game.shapeRenderer)

        game.batch.begin()
        val dataList = when(mode) {
            "ITEMS" -> game.dataManager.getAllItems().toList()
            "ENEMIES" -> game.dataManager.getAllEnemies().toList()
            else -> game.dataManager.getAllQuests().toList()
        }
        
        game.fonts.normal.setColor(Color.YELLOW)
        game.fonts.normal.draw(game.batch, "Mode: $mode (Tab pour changer)", 30f, 440f)

        // Panel de gauche : Liste (limitée à 12 pour visibilité)
        val visibleCount = 12
        for (i in 0 until dataList.size.coerceAtMost(visibleCount)) {
            val sel = i == selectedIndex
            game.fonts.small.setColor(if (sel) Color.GOLD else Color.WHITE)
            val name = when(mode) {
                "ITEMS" -> (dataList[i] as Item).name
                "ENEMIES" -> (dataList[i] as Enemy).name
                else -> (dataList[i] as Quest).title
            }
            game.fonts.small.draw(game.batch, "${if (sel) "> " else "  "} $name", 30f, 400f - i * 25f)
        }
        
        // Panel de droite : Détails
        val selected = dataList.getOrNull(selectedIndex)
        if (selected != null) {
            game.fonts.normal.setColor(Color.CYAN)
            val titleText = when(mode) {
                "ITEMS" -> "OBJET : ${(selected as Item).name}"
                "ENEMIES" -> "ENNEMI : ${(selected as Enemy).name}"
                else -> "QUÊTE : ${(selected as Quest).title}"
            }
            game.fonts.normal.draw(game.batch, titleText, 350f, 400f)
            
            game.fonts.small.setColor(Color.WHITE)
            when(mode) {
                "ITEMS" -> {
                    val item = selected as Item
                    game.fonts.small.draw(game.batch, "Valeur: ${item.value} (<- / ->)", 350f, 350f)
                    game.fonts.small.draw(game.batch, "Poids: ${String.format(Locale.US, "%.1f", item.weight)} kg (Q / D)", 350f, 320f)
                    game.fonts.small.draw(game.batch, "Rareté: ${item.rarity} (W / X)", 350f, 290f)
                }
                "ENEMIES" -> {
                    val enemy = selected as Enemy
                    game.fonts.small.draw(game.batch, "PV Max: ${enemy.maxHp} (<- / ->)", 350f, 350f)
                    game.fonts.small.draw(game.batch, "Attaque: ${enemy.attack} (Q / D)", 350f, 320f)
                    game.fonts.small.draw(game.batch, "IA: ${enemy.aiType} (N pour changer)", 350f, 290f)
                }
                "QUESTS" -> {
                    val q = selected as Quest
                    game.fonts.small.draw(game.batch, "ID: ${q.id}", 350f, 350f)
                    game.fonts.small.draw(game.batch, "Étapes: ${q.steps.size}", 350f, 320f)
                    game.fonts.small.draw(game.batch, "Récompenses: ${q.rewards.size}", 350f, 290f)
                }
            }
        }
        
        game.fonts.tiny.setColor(Color.GRAY)
        game.fonts.tiny.draw(game.batch, "S: Sauvegarder dans les fichiers du projet | Echap: Retour", 30f, 30f)
        
        game.batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screenManager.setScreen(MainMenuScreen(game))
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            mode = when(mode) {
                "ITEMS" -> "ENEMIES"
                "ENEMIES" -> "QUESTS"
                else -> "ITEMS"
            }
            selectedIndex = 0
        }
        
        val dataList = when(mode) {
            "ITEMS" -> game.dataManager.getAllItems().toList()
            "ENEMIES" -> game.dataManager.getAllEnemies().toList()
            else -> game.dataManager.getAllQuests().toList()
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) selectedIndex = (selectedIndex + 1) % dataList.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) selectedIndex = (selectedIndex - 1 + dataList.size) % dataList.size
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            saveAll()
        }
        
        // Edition des valeurs
        val selected = dataList.getOrNull(selectedIndex) ?: return
        
        if (mode == "ITEMS") {
            val item = selected as Item
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) updateItem(item.copy(value = item.value + 10))
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) updateItem(item.copy(value = (item.value - 10).coerceAtLeast(0)))
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) updateItem(item.copy(weight = item.weight + 0.1f))
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) updateItem(item.copy(weight = (item.weight - 0.1f).coerceAtLeast(0f)))
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) cycleRarity(item, 1)
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) cycleRarity(item, -1)
        }
        
        if (mode == "ENEMIES") {
            val enemy = selected as Enemy
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) updateEnemy(enemy.copy(maxHp = enemy.maxHp + 50))
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) updateEnemy(enemy.copy(maxHp = (enemy.maxHp - 50).coerceAtLeast(1)))
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) updateEnemy(enemy.copy(attack = enemy.attack + 5))
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) updateEnemy(enemy.copy(attack = (enemy.attack - 5).coerceAtLeast(1)))
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) cycleAiType(enemy)
        }
    }

    private fun cycleRarity(item: Item, direction: Int) {
        val values = ItemRarity.values()
        val nextIdx = (item.rarity.ordinal + direction + values.size) % values.size
        updateItem(item.copy(rarity = values[nextIdx]))
    }

    private fun cycleAiType(enemy: Enemy) {
        val types = listOf("AGGRESSIVE", "SUPPORT", "TACTICAL", "BOSS", "RANDOM")
        val currentIdx = types.indexOf(enemy.aiType).coerceAtLeast(0)
        val nextIdx = (currentIdx + 1) % types.size
        updateEnemy(enemy.copy(aiType = types[nextIdx]))
    }

    private fun updateItem(newItem: Item) {
        game.dataManager.forceLoad(itemsMap = mapOf(newItem.id to newItem))
    }

    private fun updateEnemy(newEnemy: Enemy) {
        game.dataManager.forceLoad(enemiesMap = mapOf(newEnemy.id to newEnemy))
    }

    private fun saveAll() {
        // Objets
        game.saveJson("data/items.json", json.prettyPrint(game.dataManager.getAllItems().toList()))
        // Ennemis
        game.saveJson("data/enemies.json", json.prettyPrint(game.dataManager.getAllEnemies().toList()))
        // Quêtes
        game.saveJson("data/quests.json", json.prettyPrint(game.dataManager.getAllQuests().toList()))
    }

    override fun resize(w: Int, h: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {}
}
