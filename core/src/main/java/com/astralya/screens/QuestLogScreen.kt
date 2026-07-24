package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.astralya.AstralYaGame
import com.astralya.data.GameState
import com.astralya.map.QuestRegistry

class QuestLogScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    private val parentScreen: Screen
) : Screen {

    private var pixelRegion: TextureRegion? = null
    private val activeQuests = state.getActiveQuests()
    private var elapsed = 0f
    private val touchVec = com.badlogic.gdx.math.Vector3()

    override fun show() {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelRegion = TextureRegion(Texture(pixmap))
        pixmap.dispose()
        elapsed = 0f
    }

    override fun render(delta: Float) {
        elapsed += delta
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(parentScreen)
            return
        }
        
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchVec)
            val touchX = touchVec.x
            val touchY = touchVec.y
            if (touchX > game.viewport.worldWidth - 160f && touchY < 60f) {
                game.setScreen(parentScreen)
                return
            }
        }

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.batch.begin()

        // UI Frame
        game.batch.draw(game.assetLoader.getTexture("sprites/ui_frame.png"), 10f, 10f, W - 20f, H - 20f)

        game.fonts.large.setColor(Color.GOLD)
        game.fonts.large.draw(game.batch, "Journal des Quêtes", 35f, H - 35f)

        if (activeQuests.isEmpty()) {
            game.fonts.normal.setColor(Color.LIGHT_GRAY)
            game.fonts.normal.draw(game.batch, "Aucune quête active.", 100f, H - 150f)
        } else {
            activeQuests.forEachIndexed { index, progress ->
                val quest = QuestRegistry.getQuest(progress.questId)
                if (quest != null) {
                    val y = H - 120f - index * 100f
                    
                    val pulse = 0.9f + com.badlogic.gdx.math.MathUtils.sin(elapsed * 4f + index) * 0.1f
                    game.fonts.medium.setColor(pulse, pulse, 1f, 1f)
                    game.fonts.medium.draw(game.batch, "${quest.title}", 60f, y)
                    
                    game.fonts.small.setColor(Color.CYAN)
                    val step = quest.steps.getOrNull(progress.currentStep)
                    game.fonts.small.draw(game.batch, "Objectif : ${step?.description ?: "Terminé"}", 80f, y - 30f)
                }
            }
        }

        game.fonts.normal.setColor(Color.WHITE)
        game.fonts.normal.draw(game.batch, "[ RETOUR ]", W - 160f, 45f)

        game.batch.end()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() { pixelRegion?.texture?.dispose() }
}
