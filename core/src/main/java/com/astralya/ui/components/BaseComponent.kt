package com.astralya.ui.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 * Interface de base pour tous les éléments d'interface.
 */
interface UIComponent {
    var x: Float
    var y: Float
    var width: Float
    var height: Float
    var isVisible: Boolean
    var isFocused: Boolean

    fun update(delta: Float)
    fun draw(batch: SpriteBatch, shape: ShapeRenderer)
    fun handleInput(): Boolean // Retourne true si l'input est consommé
}

/**
 * Conteneur pouvant regrouper plusieurs composants.
 */
abstract class UIContainer : UIComponent {
    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f
    override var isVisible: Boolean = true
    override var isFocused: Boolean = false

    protected val children = mutableListOf<UIComponent>()

    fun addChild(component: UIComponent) {
        children.add(component)
    }

    fun removeChild(component: UIComponent) {
        children.remove(component)
    }

    override fun update(delta: Float) {
        if (!isVisible) return
        children.forEach { it.update(delta) }
    }

    override fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isVisible) return
        children.forEach { it.draw(batch, shape) }
    }

    override fun handleInput(): Boolean {
        if (!isVisible) return false
        // On parcourt à l'envers pour gérer la priorité d'affichage (z-index)
        for (i in children.indices.reversed()) {
            if (children[i].handleInput()) return true
        }
        return false
    }
}
