package com.astralya.ui.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.astralya.engine.utils.FontManager
import com.astralya.engine.core.ResourceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * DialogueBox - Gère l'affichage des dialogues avec effet machine à écrire.
 */
class DialogueBox : UIComponent, KoinComponent {
    override var x: Float = 40f
    override var y: Float = 30f
    override var width: Float = 720f // Basé sur 800-80
    override var height: Float = 130f
    override var isVisible: Boolean = false
    override var isFocused: Boolean = false

    private val fonts: FontManager by inject()
    private val resourceManager: ResourceManager by inject()

    private var pages: List<String> = emptyList()
    private var currentPage = 0
    private var charIndex = 0
    private var timer = 0f
    private val CHAR_SPEED = 0.03f

    var onFinished: (() -> Unit)? = null

    fun show(textList: List<String>) {
        pages = textList
        currentPage = 0
        charIndex = 0
        timer = 0f
        isVisible = true
        isFocused = true
    }

    override fun update(delta: Float) {
        if (!isVisible) return
        
        if (currentPage < pages.size) {
            val text = pages[currentPage]
            if (charIndex < text.length) {
                timer += delta
                if (timer >= CHAR_SPEED) {
                    charIndex++
                    timer = 0f
                }
            }
        }
    }

    override fun draw(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isVisible) return

        batch.begin()
        batch.setColor(0f, 0f, 0.2f, 0.9f)
        batch.draw(resourceManager.getTexture("ui_frame"), x, y, width, height)
        
        if (currentPage < pages.size) {
            val fullText = pages[currentPage]
            val visibleText = fullText.substring(0, charIndex)
            fonts.normal.setColor(Color.WHITE)
            fonts.normal.draw(batch, visibleText, x + 25f, y + height - 30f, width - 50f, 1, true)
            
            // Petit indicateur "Suivant"
            if (charIndex >= fullText.length) {
                val blink = if ((Gdx.graphics.frameId / 30) % 2 == 0L) ">" else ""
                fonts.small.draw(batch, blink, x + width - 40f, y + 25f)
            }
        }
        batch.setColor(Color.WHITE)
        batch.end()
    }

    override fun handleInput(): Boolean {
        if (!isVisible || !isFocused) return false

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            val text = pages.getOrNull(currentPage) ?: ""
            if (charIndex < text.length) {
                // Skip typewriter
                charIndex = text.length
            } else {
                // Next page
                currentPage++
                charIndex = 0
                if (currentPage >= pages.size) {
                    isVisible = false
                    isFocused = false
                    onFinished?.invoke()
                }
            }
            return true
        }
        return true
    }
}
