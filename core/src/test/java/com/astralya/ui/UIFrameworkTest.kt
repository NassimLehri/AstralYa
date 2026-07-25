package com.astralya.ui

import com.astralya.engine.core.UIManager
import com.astralya.ui.components.UIComponent
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class UIFrameworkTest {

    @Test
    fun `test UIManager layer management`() {
        val uiManager = UIManager()
        val component = mock(UIComponent::class.java)
        
        uiManager.addHUD(component)
        uiManager.update(1f)
        
        verify(component).update(1f)
    }

    @Test
    fun `test modal priority`() {
        val uiManager = UIManager()
        val hud = mock(UIComponent::class.java)
        val modal = mock(UIComponent::class.java)
        
        `when`(modal.handleInput()).thenReturn(true)
        
        uiManager.addHUD(hud)
        uiManager.openModal(modal)
        
        val consumed = uiManager.handleInput()
        
        assertTrue("Input should be consumed by modal", consumed)
        verify(modal).handleInput()
        verify(hud, never()).handleInput()
    }

    @Test
    fun `test clear removes all layers`() {
        val uiManager = UIManager()
        val hud = mock(UIComponent::class.java)
        uiManager.addHUD(hud)
        
        uiManager.clear()
        uiManager.update(1f)
        
        verify(hud, never()).update(anyFloat())
    }
}
