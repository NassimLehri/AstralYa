package com.astralya.engine.core

import com.astralya.ui.components.UIComponent
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 * Epic 9 — UIManager gère les couches d'interface globales.
 */
class UIManager {

    private val layers = mutableListOf<UIComponent>()
    private val modalStack = mutableListOf<UIComponent>()

    /** Ajoute un composant permanent (ex: HUD) */
    fun addHUD(component: UIComponent) {
        layers.add(component)
    }

    /** Ouvre une fenêtre modale (bloque les interactions en dessous) */
    fun openModal(component: UIComponent) {
        component.isFocused = true
        modalStack.add(component)
    }

    fun closeModal() {
        if (modalStack.isNotEmpty()) {
            modalStack.removeAt(modalStack.size - 1)
        }
    }

    fun update(delta: Float) {
        layers.forEach { it.update(delta) }
        modalStack.forEach { it.update(delta) }
    }

    fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        layers.forEach { it.draw(batch, shape) }
        modalStack.forEach { it.draw(batch, shape) }
    }

    fun handleInput(): Boolean {
        // La pile modale a priorité absolue
        for (i in modalStack.indices.reversed()) {
            if (modalStack[i].handleInput()) return true
        }
        
        // Puis les calques standards
        for (i in layers.indices.reversed()) {
            if (layers[i].handleInput()) return true
        }
        
        return false
    }

    fun clear() {
        layers.clear()
        modalStack.clear()
    }
}
