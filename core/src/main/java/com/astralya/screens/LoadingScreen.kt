package com.astralya.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.astralya.AstralYaGame

/**
 * FIX PERF #2 — Color en companion object
 * FIX PERF #8 — game.fonts
 */
class LoadingScreen(private val game: AstralYaGame) : Screen {

    private var elapsed = 0f

    companion object {
        private val C_BG        = Color(0f,    0f,    0.05f, 1f)
        private val C_BAR_BG    = Color(0.1f,  0.1f,  0.2f,  1f)
        private val C_BAR_FG    = Color(0.3f,  0.6f,  1f,    1f)
        private val C_BAR_EDGE  = Color(0.5f,  0.7f,  1f,    1f)
        private val C_TITLE     = Color(1f,    0.85f, 0.2f,  1f)
        private val C_PROGRESS  = Color(0.7f,  0.85f, 1f,    1f)
        private val C_WHITE     = Color(1f, 1f, 1f, 1f)
    }

    private val sb = StringBuilder(32)

    override fun show() {}

    override fun render(delta: Float) {
        elapsed += delta
        val finished = game.assetManager.update()
        val progress = game.assetManager.progress

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0f, 0f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined

        val barW = W * 0.6f; val barH = 22f
        val barX = (W - barW) / 2f; val barY = H / 2f - barH / 2f

        val shape = game.shapeRenderer
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = C_BAR_BG; shape.rect(barX, barY, barW, barH)
        shape.color = C_BAR_FG; shape.rect(barX, barY, barW * progress, barH)
        shape.end()

        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = C_BAR_EDGE; shape.rect(barX, barY, barW, barH)
        shape.end()

        game.batch.begin()
        game.fonts.title.setColor(C_TITLE)
        game.fonts.title.draw(game.batch, "Les Gardiens d'Astralya", barX, H / 2f + 80f)
        game.fonts.normal.setColor(C_PROGRESS)
        sb.clear(); sb.append("Chargement... ").append((progress * 100).toInt()).append('%')
        game.fonts.normal.draw(game.batch, sb, barX, barY - 14f)
        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()

        if (finished && elapsed > 0.5f) {
            game.assetsLoaded = true
            game.setScreen(SplashScreen(game))
            dispose()
        }
    }

    override fun resize(w: Int, h: Int) {
        game.viewport.update(w, h, true)
    }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() {}
}
