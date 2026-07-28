package com.astralya.lwjgl3

import com.astralya.MinimalTiledTest
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

/** Launches the minimal Tiled baseline test. */
fun main() {
    val configuration = Lwjgl3ApplicationConfiguration()
    configuration.setTitle("AstralYa - Minimal Tiled Baseline")
    configuration.setWindowedMode(1280, 720)
    configuration.useVsync(true)
    configuration.setForegroundFPS(60)
    Lwjgl3Application(MinimalTiledTest(), configuration)
}
