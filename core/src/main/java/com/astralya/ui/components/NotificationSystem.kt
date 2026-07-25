package com.astralya.ui.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.astralya.engine.utils.FontManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * NotificationSystem - Affiche des messages temporaires ("Toasts").
 */
class NotificationSystem : UIComponent, KoinComponent {
    override var x: Float = 0f
    override var y: Float = 440f
    override var width: Float = 800f
    override var height: Float = 40f
    override var isVisible: Boolean = true
    override var isFocused: Boolean = false

    private val fonts: FontManager by inject()

    private val queue = mutableListOf<String>()
    private var currentMessage: String? = null
    private var timer = 0f
    private val DISPLAY_TIME = 2.5f

    fun show(message: String) {
        queue.add(message)
    }

    override fun update(delta: Float) {
        if (currentMessage == null && queue.isNotEmpty()) {
            currentMessage = queue.removeAt(0)
            timer = 0f
        }

        if (currentMessage != null) {
            timer += delta
            if (timer >= DISPLAY_TIME) {
                currentMessage = null
            }
        }
    }

    override fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        val msg = currentMessage ?: return

        batch.begin()
        val alpha = if (timer < 0.3f) timer / 0.3f else if (timer > DISPLAY_TIME - 0.3f) (DISPLAY_TIME - timer) / 0.3f else 1f
        fonts.normal.setColor(1f, 0.9f, 0.4f, alpha)
        fonts.normal.draw(batch, msg, 0f, y, width, 1, true)
        batch.setColor(Color.WHITE)
        batch.end()
    }

    override fun handleInput(): Boolean = false
}
