package com.astralya.ui.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.astralya.engine.core.ResourceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * GameWindow - Cadre standard pour les menus et fenêtres.
 */
open class GameWindow(
    override var x: Float,
    override var y: Float,
    override var width: Float,
    override var height: Float,
    var title: String = ""
) : UIComponent, KoinComponent {

    protected val resourceManager: ResourceManager by inject()
    override var isVisible: Boolean = true
    override var isFocused: Boolean = false

    override fun update(delta: Float) {}

    override fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isVisible) return
        
        batch.begin()
        batch.draw(resourceManager.getTexture("ui_frame"), x, y, width, height)
        // On pourrait dessiner le titre ici
        batch.end()
    }

    override fun handleInput(): Boolean {
        if (!isVisible || !isFocused) return false
        // Bloque les clics à travers la fenêtre par défaut
        return true 
    }
}
