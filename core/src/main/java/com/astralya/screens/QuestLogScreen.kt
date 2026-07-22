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

    override fun show() {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelRegion = TextureRegion(Texture(pixmap))
        pixmap.dispose()
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.justTouched()) {
            game.setScreen(parentScreen)
        }

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.batch.begin()

        // Background frame
        game.batch.setColor(0f, 0f, 0.2f, 0.8f)
        game.batch.draw(pixelRegion!!, 50f, 50f, W - 100f, H - 100f)

        game.fonts.large.setColor(Color.GOLD)
        game.fonts.large.draw(game.batch, "Journal des Quêtes", 80f, H - 80f)

        if (activeQuests.isEmpty()) {
            game.fonts.normal.setColor(Color.LIGHT_GRAY)
            game.fonts.normal.draw(game.batch, "Aucune quête active.", 100f, H - 150f)
        } else {
            activeQuests.forEachIndexed { index, progress ->
                val quest = QuestRegistry.getQuest(progress.questId)
                if (quest != null) {
                    val y = H - 150f - index * 100f
                    
                    game.fonts.medium.setColor(Color.WHITE)
                    game.fonts.medium.draw(game.batch, "${quest.title}", 100f, y)
                    
                    game.fonts.small.setColor(Color.CYAN)
                    val step = quest.steps.getOrNull(progress.currentStep)
                    game.fonts.small.draw(game.batch, "Objectif : ${step?.description ?: "Terminé"}", 120f, y - 30f)
                }
            }
        }

        game.fonts.normal.setColor(Color.GRAY)
        game.fonts.normal.draw(game.batch, "Appuyez pour retourner", W / 2f - 120f, 85f)

        game.batch.end()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() { pixelRegion?.texture?.dispose() }
}
