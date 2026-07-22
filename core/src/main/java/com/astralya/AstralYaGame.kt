package com.astralya

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.assets.AssetManager
import com.astralya.data.GameState
import com.astralya.data.repository.GameRepository
import com.astralya.screens.LoadingScreen
import com.astralya.utils.AssetLoader
import com.astralya.utils.FontManager
import com.astralya.utils.GameRandom
import com.astralya.audio.AudioManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class AstralYaGame(
    val repository: GameRepository,
    val gameState: GameState
) : Game() {

    lateinit var batch: SpriteBatch
    lateinit var assetManager: AssetManager
    lateinit var assetLoader: AssetLoader
    lateinit var audioManager: AudioManager
    lateinit var shapeRenderer: ShapeRenderer

    // Gestion de la résolution virtuelle (800x480)
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport

    // FIX PERF #8 — FontManager remplace le BitmapFont unique + setScale()
    lateinit var fonts: FontManager

    // FIX PERF #6 — GameRandom central, injectable dans CombatSystem etc.
    val random: GameRandom = GameRandom()

    var assetsLoaded = false

    override fun create() {
        batch         = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        assetManager  = AssetManager()
        assetLoader   = AssetLoader(assetManager)
        audioManager  = AudioManager()
        fonts         = FontManager()

        camera   = OrthographicCamera()
        viewport = FitViewport(800f, 480f, camera)
        viewport.apply(true)

        Gdx.graphics.isContinuousRendering = true
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        assetLoader.loadAll()
        setScreen(LoadingScreen(this))
    }

    override fun render() {
        // Nécessaire pour les fades audio
        audioManager.update(Gdx.graphics.deltaTime)
        super.render()
    }
    override fun resize(w: Int, h: Int) {
        viewport.update(w, h, true)
        super.resize(w, h)
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        assetManager.dispose()
        audioManager.dispose()
        fonts.dispose()
        screen?.dispose()
    }
}
