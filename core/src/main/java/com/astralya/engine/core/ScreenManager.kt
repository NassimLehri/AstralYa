package com.astralya.engine.core

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen

/**
 * ScreenManager - Gère les transitions entre les écrans.
 */
class ScreenManager(private val game: Game) {

    fun setScreen(screen: Screen) {
        val currentScreen = game.screen
        game.screen = screen
        // currentScreen?.dispose() // Attention: LibGDX dispose parfois déjà, ou on veut garder l'écran
    }
}
