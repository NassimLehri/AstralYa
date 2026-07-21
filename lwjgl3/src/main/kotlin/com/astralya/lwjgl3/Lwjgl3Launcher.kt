package com.astralya.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.astralya.AstralYaGame
import com.astralya.data.GameState
import com.astralya.data.repository.GameRepository

fun main() {
    val repository = GameRepository(
        MockHeroDao(),
        MockInventoryDao(),
        MockQuestDao(),
        MockSaveDao()
    )
    val gameState = GameState()

    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("AstralYa - Desktop")
        setWindowedMode(1280, 720)
        useVsync(true)
        setForegroundFPS(60)
    }

    Lwjgl3Application(AstralYaGame(repository, gameState), config)
}
