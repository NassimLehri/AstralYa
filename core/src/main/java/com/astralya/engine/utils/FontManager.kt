package com.astralya.engine.utils

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.Gdx

/**
 * FontManager MODERNISÉ — Utilise FreeType pour générer des polices nettes à toutes les tailles.
 */
class FontManager {

    lateinit var tiny:   BitmapFont
    lateinit var small:  BitmapFont
    lateinit var normal: BitmapFont
    lateinit var medium: BitmapFont
    lateinit var large:  BitmapFont
    lateinit var title:  BitmapFont

    init {
        // Préférence : charger un BitmapFont (.fnt + .png) si présent dans assets/fonts
        val fontsDir = Gdx.files.internal("fonts")
        var loadedBitmap = false
        if (fontsDir.exists()) {
            val list = fontsDir.list()
            val fnts = list?.filter { it.extension().equals("fnt", ignoreCase = true) } ?: emptyList()
            var chosen: com.badlogic.gdx.files.FileHandle? = null
            for (fh in fnts) {
                try {
                    val content = fh.readString()
                    val m = Regex("page\\s+id=\\d+\\s+file=\"([^\"]+)\"").find(content)
                    val pageName = m?.groups?.get(1)?.value
                    if (pageName != null) {
                        val img = Gdx.files.internal("fonts/$pageName")
                        if (img.exists()) { chosen = fh; break }
                    }
                } catch (e: Exception) {
                    // ignore malformed fnt
                }
            }
            if (chosen != null) {
                Gdx.app.log("AstralYa", "Chargement BitmapFont: ${chosen.path()}")
                tiny   = BitmapFont(chosen).apply { data.setScale(0.6f) }
                small  = BitmapFont(chosen).apply { data.setScale(0.8f) }
                normal = BitmapFont(chosen)
                medium = BitmapFont(chosen).apply { data.setScale(1.2f) }
                large  = BitmapFont(chosen).apply { data.setScale(1.6f) }
                title  = BitmapFont(chosen).apply { data.setScale(2.4f) }
                // Configure BitmapFont texture filters & integer positions
                try {
                    val fonts = listOf(tiny, small, normal, medium, large, title)
                    fonts.forEach { f -> f.regions.forEach { it.texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest); f.setUseIntegerPositions(true) } }
                } catch (_: Exception) {}
                loadedBitmap = true
            }
        }

        if (!loadedBitmap) {
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
                
                parameter.kerning = true
                parameter.incremental = true
                parameter.size = 8; tiny = generator.generateFont(parameter)
                parameter.size = 10; small = generator.generateFont(parameter)
                parameter.size = 12; normal = generator.generateFont(parameter)
                parameter.size = 16; medium = generator.generateFont(parameter)
                parameter.size = 24; large = generator.generateFont(parameter)
                parameter.size = 34; title = generator.generateFont(parameter)

                // Configure generated fonts for pixel-perfect rendering
                fun configure(f: BitmapFont) {
                    try {
                        f.regions.forEach { it.texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest) }
                        f.data.setScale(1f)
                        f.setUseIntegerPositions(true)
                    } catch (_: Exception) {}
                }
                configure(tiny); configure(small); configure(normal); configure(medium); configure(large); configure(title)
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
