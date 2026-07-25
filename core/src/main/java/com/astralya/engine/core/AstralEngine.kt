package com.astralya.engine.core

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Epic 12 — AstralEngine : Classe de base abstraite pour le moteur de jeu.
 * Gère l'infrastructure technique (DI, Rendu, Profiling) de manière agnostique.
 */
abstract class AstralEngine : Game(), KoinComponent {

    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var profiler: GLProfiler

    val resourceManager: ResourceManager by inject()
    val screenManager: ScreenManager by inject()
    val uiManager: UIManager by inject()
    val audioManager: com.astralya.audio.AudioManager by inject()

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        
        camera = OrthographicCamera()
        viewport = FitViewport(800f, 480f, camera)
        viewport.apply(true)

        profiler = GLProfiler(Gdx.graphics)
        if (isDebugMode()) {
            profiler.enable()
        }

        onEngineInit()
    }

    /** Hook pour l'initialisation spécifique au jeu */
    abstract fun onEngineInit()

    /** Hook pour déterminer si on active le mode debug/profiling */
    open fun isDebugMode(): Boolean = true

    /** Hook pour le mode éditeur (Epic 17) */
    open fun isEditorMode(): Boolean = false

    /** Permet de sauvegarder du JSON (implémentation spécifique au Desktop) */
    open fun saveJson(path: String, content: String) {
        Gdx.app.log("AstralEngine", "Sauvegarde JSON non supportée sur cette plateforme : $path")
    }

    override fun render() {
        profiler.reset()
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
        resourceManager.dispose()
        audioManager.dispose()
        super.dispose()
    }
}
