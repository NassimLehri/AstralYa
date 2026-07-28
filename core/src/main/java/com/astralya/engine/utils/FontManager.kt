package com.astralya.engine.utils

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.Gdx

/**
 * FontManager MODERNISÉ — Utilise FreeType pour générer des polices nettes à toutes les tailles.
 */
class FontManager {

    val tiny:   BitmapFont
    val small:  BitmapFont
    val normal: BitmapFont
    val medium: BitmapFont
    val large:  BitmapFont
    val title:  BitmapFont

    init {
        // Chemin vers la police TTF. Si absente, on utilise la police par défaut de LibGDX (fallback).
        val fontFile = Gdx.files.internal("fonts/main.ttf")
        
        if (fontFile.exists()) {
            val generator = FreeTypeFontGenerator(fontFile)
            val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
            
            // Pixel-style parameters for PressStart2P
            parameter.borderWidth = 0f
            parameter.borderColor = Color(0f,0f,0f,0f)
            parameter.shadowOffsetX = 0
            parameter.shadowOffsetY = 0
            parameter.shadowColor = Color(0f,0f,0f,0f)
            parameter.minFilter = TextureFilter.Nearest
            parameter.magFilter = TextureFilter.Nearest
            
            parameter.size = 8; tiny = generator.generateFont(parameter)
            parameter.size = 10; small = generator.generateFont(parameter)
            parameter.size = 12; normal = generator.generateFont(parameter)
            parameter.size = 16; medium = generator.generateFont(parameter)
            parameter.size = 24; large = generator.generateFont(parameter)
            parameter.size = 34; title = generator.generateFont(parameter)
            
            generator.dispose()
        } else {
            // Fallback sur BitmapFont par défaut (Arial 15px) avec mise à l'échelle (moins net)
            tiny   = BitmapFont().apply { data.setScale(0.62f) }
            small  = BitmapFont().apply { data.setScale(0.75f) }
            normal = BitmapFont().apply { data.setScale(0.90f) }
            medium = BitmapFont().apply { data.setScale(1.05f) }
            large  = BitmapFont().apply { data.setScale(1.35f) }
            title  = BitmapFont().apply { data.setScale(2.20f) }
            
            Gdx.app.log("AstralYa", "ATTENTION: fonts/main.ttf non trouvée. Utilisation du fallback BitmapFont.")
        }
    }

    /** Remet toutes les couleurs à blanc (à appeler en fin de draw()) */
    fun resetColors() {
        val white = Color.WHITE
        tiny.setColor(white);   small.setColor(white)
        normal.setColor(white); medium.setColor(white)
        large.setColor(white);  title.setColor(white)
    }

    fun dispose() {
        tiny.dispose(); small.dispose(); normal.dispose()
        medium.dispose(); large.dispose(); title.dispose()
    }
}
