package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.astralya.AstralYaGame

// ════════════════════════════════════════════════════════════════
// SPLASH SCREEN — FIX PERF #2 #8 + Scaling
// ════════════════════════════════════════════════════════════════

class SplashScreen(private val game: AstralYaGame) : Screen {

    private var elapsed = 0f

    companion object {
        private val C_TITLE  = Color(1f, 0.85f, 0.2f, 1f)
        private val C_SUB    = Color(0.6f, 0.7f, 1f, 1f)
        private val C_WHITE  = Color(1f, 1f, 1f, 1f)
    }

    override fun show() {}

    override fun render(delta: Float) {
        elapsed += delta
        val alpha = when {
            elapsed < 1f -> elapsed
            elapsed < 2f -> 1f
            else         -> 1f - (elapsed - 2f)
        }.coerceIn(0f, 1f)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        game.batch.begin()
        game.fonts.title.setColor(1f, 0.85f, 0.2f, alpha)
        game.fonts.title.draw(game.batch, "ASTRALYA STUDIOS", W * 0.22f, H * 0.54f)
        game.fonts.normal.setColor(0.6f, 0.7f, 1f, alpha * 0.8f)
        game.fonts.normal.draw(game.batch, "Les Gardiens d'Astralya", W * 0.30f, H * 0.43f)
        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()

        if (elapsed >= 3f || Gdx.input.isTouched) {
            game.setScreen(MainMenuScreen(game)); dispose()
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

// ════════════════════════════════════════════════════════════════
// MAIN MENU SCREEN — FIX PERF #2 #3 #4 #8 + Scaling + Touch
// ════════════════════════════════════════════════════════════════

open class MainMenuScreen(private val game: AstralYaGame) : Screen {

    private val menuItems = listOf("Nouvelle Partie", "Continuer", "Options", "Quitter")
    private var selectedIndex = 0
    private var elapsed       = 0f
    private val sb = StringBuilder(32)
    private val touchPos = Vector3()

    companion object {
        private val C_TITLE  = Color(1f, 0.85f, 0.2f, 1f)
        private val C_SUB    = Color(0.65f, 0.80f, 1f, 1f)
        private val C_WHITE  = Color(1f, 1f, 1f, 1f)
        private val C_HINT   = Color(0.45f, 0.45f, 0.5f, 1f)
        // Étoiles pré-calculées ajustées pour 800x480
        private val STAR_POSITIONS = listOf(
            50f to 400f, 150f to 250f, 400f to 350f,
            600f to 420f, 700f to 150f, 750f to 300f, 780f to 80f,
            200f to 440f, 500f to 100f, 720f to 380f, 120f to 320f
        )
    }

    override fun show() {}

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined
        game.batch.projectionMatrix = game.viewport.camera.combined

        // Fond étoilé — shape avant batch
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color(0.02f, 0.02f, 0.1f, 1f)
        game.shapeRenderer.rect(0f, 0f, W, H)
        game.shapeRenderer.color = Color(1f, 1f, 1f, 0.6f)
        // FIX PERF #4 — for classique, positions pré-calculées
        for (i in STAR_POSITIONS.indices) {
            val (sx, sy) = STAR_POSITIONS[i]
            game.shapeRenderer.circle(sx, sy, 2f)
        }
        game.shapeRenderer.end()

        game.batch.begin()

        game.fonts.title.setColor(C_TITLE)
        game.fonts.title.draw(game.batch, "Les Gardiens d'Astralya", W * 0.10f, H * 0.82f)

        game.fonts.normal.setColor(C_SUB)
        game.fonts.normal.draw(game.batch,
            "Un monde menacé par le Seigneur du Néant...", W * 0.26f, H * 0.68f)

        // FIX PERF #4 — for, FIX PERF #3 — StringBuilder
        for (i in menuItems.indices) {
            val sel   = i == selectedIndex
            val pulse = if (sel) 0.75f + MathUtils.sin(elapsed * 4f) * 0.25f else 0.55f
            val fnt   = if (sel) game.fonts.medium else game.fonts.normal
            fnt.setColor(pulse, if (sel) pulse * 0.9f else 0.65f, if (sel) 0.25f else 0.65f, 1f)
            sb.clear(); sb.append(if (sel) "► " else "  ").append(menuItems[i])
            fnt.draw(game.batch, sb, W * 0.36f, H * 0.52f - i * 54f)
        }

        game.fonts.tiny.setColor(C_HINT)
        game.fonts.tiny.draw(game.batch, "↑↓ Naviguer  |  ENTRÉE Confirmer", W * 0.36f, 28f)

        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            selectedIndex = (selectedIndex + 1) % menuItems.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            selectedIndex = (selectedIndex - 1 + menuItems.size) % menuItems.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.Z)) onSelect()

        // GESTION DU TOUCHER
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchPos)

            val W = game.viewport.worldWidth
            val H = game.viewport.worldHeight

            for (i in menuItems.indices) {
                val itemX = W * 0.36f
                val itemY = H * 0.52f - i * 54f
                // Bounding box approximative (300px large, 40px haut)
                if (touchPos.x >= itemX && touchPos.x <= itemX + 300f &&
                    touchPos.y <= itemY && touchPos.y >= itemY - 40f) {
                    if (selectedIndex == i) onSelect()
                    else selectedIndex = i
                    break
                }
            }
        }
    }

    private fun onSelect() {
        when (selectedIndex) {
            0 -> {
                game.gameState.newGame()
                game.setScreen(ExplorationScreen(game, game.gameState))
                dispose()
            }
            1 -> {
                game.setScreen(SaveScreen(game, game.gameState, SaveScreen.Mode.LOAD))
                dispose()
            }
            2 -> game.setScreen(OptionsScreen(game))
            3 -> Gdx.app.exit()
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
