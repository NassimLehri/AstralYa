package com.astralya.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.astralya.AstralYaGame
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.repository.GameRepository
import java.io.File
import com.astralya.engine.utils.ScreenshotHelper

fun main() {
    val repository = GameRepository(
        MockHeroDao(),
        MockInventoryDao(),
        MockQuestDao(),
        MockSaveDao()
    )
    val gameState = GameStateManager()

    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("AstralYa - Desktop")
        setWindowedMode(1280, 720)
        useVsync(true)
        setForegroundFPS(60)
    }

    // Enable screenshot capture if the trigger file exists in repo root
    if (File("capture_screenshot").exists()) {
        ScreenshotHelper.enabled = true
    }
    Lwjgl3Application(AstralYaGame(repository, gameState), config)
}
