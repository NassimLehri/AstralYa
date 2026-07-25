package com.astralya.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.Gdx
import com.astralya.AstralYaGame
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.repository.GameRepository
import java.io.File

/**
 * Epic 17 — Lanceur Desktop pour le mode Éditeur.
 */
class EditorGame(repo: GameRepository, state: GameStateManager) : AstralYaGame(repo, state) {
    override fun isEditorMode(): Boolean = true

    override fun saveJson(path: String, content: String) {
        try {
            // Dans l'IDE, on pointe vers le dossier assets d'Android pour que les modifs soient persistantes
            val file = File("android/src/main/assets/", path)
            file.writeText(content)
            Gdx.app.log("Editor", "Données sauvegardées avec succès : ${file.absolutePath}")
        } catch (e: Exception) {
            Gdx.app.error("Editor", "Échec de la sauvegarde : ${e.message}")
        }
    }
}

fun main() {
    val repository = GameRepository(MockHeroDao(), MockInventoryDao(), MockQuestDao(), MockSaveDao())
    val gameState = GameStateManager()

    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("AstralYa — ÉDITEUR DE DONNÉES")
        setWindowedMode(1280, 720) // Fenêtre plus grande pour l'édition
        useVsync(true)
        setForegroundFPS(60)
    }

    Lwjgl3Application(EditorGame(repository, gameState), config)
}
