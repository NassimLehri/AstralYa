package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.astralya.AstralYaGame

class IntroScreen(private val game: AstralYaGame) : Screen {

    private var elapsed = 0f
    private val scrollSpeed = 45f
    private var textY = -100f
    
    private val introText = listOf(
        "Dans un temps immémorial, Astralya était un havre de paix,",
        "protégé par l'éclat des Sept Cristaux Stellaires.",
        "",
        "Mais des profondeurs du Néant surgit Morvax,",
        "un ancien Gardien corrompu par une soif de pouvoir infinie.",
        "",
        "Il brisa l'équilibre, dispersant les Cristaux",
        "et plongeant le monde dans une pénombre grandissante.",
        "",
        "Trois jeunes héros ont été choisis par le destin :",
        "Nassim, le vaillant protecteur,",
        "Yasmine, la lumière guérisseuse,",
        "et Lwiz, le maître des constellations.",
        "",
        "Leur quête commence ici, dans le petit village d'Étoilebourg.",
        "Le futur d'Astralya repose entre leurs mains..."
    )

    override fun show() {
        game.viewport.camera.position.set(400f, 240f, 0f)
        game.viewport.camera.update()
        textY = -50f
    }

    override fun render(delta: Float) {
        elapsed += delta
        textY += scrollSpeed * delta

        Gdx.gl.glClearColor(0.01f, 0.01f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        game.batch.begin()
        
        // Draw centered text
        for (i in introText.indices) {
            val lineY = textY - i * 40f
            if (lineY > -50f && lineY < H + 50f) {
                // Fade in/out at edges
                val alpha = when {
                    lineY < 100f -> (lineY + 50f) / 150f
                    lineY > H - 100f -> (H + 50f - lineY) / 150f
                    else -> 1f
                }.coerceIn(0f, 1f)
                
                game.fonts.normal.setColor(1f, 0.95f, 0.8f, alpha)
                game.fonts.normal.draw(game.batch, introText[i], 50f, lineY, W - 100f, 1, true)
            }
        }

        game.fonts.tiny.setColor(0.5f, 0.5f, 0.6f, 0.8f)
        game.fonts.tiny.draw(game.batch, "Appuyez pour passer", W - 200f, 40f)
        
        game.batch.end()
        game.fonts.resetColors()

        // Transition conditions
        val lastLineY = textY - introText.size * 40f
        if (lastLineY > H || Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(ExplorationScreen(game, game.gameState))
            dispose()
        }
    }

    override fun resize(w: Int, h: Int) {
        game.viewport.update(w, h, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {}
}
