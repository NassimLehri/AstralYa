package com.astralya.ui.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.astralya.AstralYaGame
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Epic 10 — Affiche les métriques de performance en temps réel.
 */
class DebugProfiler(private val game: AstralYaGame) : UIComponent, KoinComponent {
    override var x: Float = 10f
    override var y: Float = 470f
    override var width: Float = 200f
    override var height: Float = 100f
    override var isVisible: Boolean = true
    override var isFocused: Boolean = false

    private val sb = StringBuilder()

    override fun update(delta: Float) {}

    override fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isVisible) return

        batch.begin()
        game.fonts.tiny.setColor(Color.LIME)
        
        sb.setLength(0)
        sb.append("FPS: ").append(Gdx.graphics.framesPerSecond)
        sb.append("\nDraw Calls: ").append(game.profiler.drawCalls)
        sb.append("\nTex Binds: ").append(game.profiler.textureBindings)
        sb.append("\nShader Sw: ").append(game.profiler.shaderSwitches)
        
        val usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
        sb.append("\nRAM: ").append(usedMem).append(" MB")

        game.fonts.tiny.draw(batch, sb, x, y)
        
        batch.setColor(Color.WHITE)
        batch.end()
    }

    override fun handleInput(): Boolean {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F3)) {
            isVisible = !isVisible
            return true
        }
        return false
    }
}
