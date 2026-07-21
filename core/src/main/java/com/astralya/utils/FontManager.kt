package com.astralya.utils

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color

/**
 * FIX PERF #8 / REVIEW font.setScale() — Fonts dédiées par taille.
 *
 * Problème : font.data.setScale() modifie l'état global du BitmapFont.
 * Si draw() lève une exception entre setScale(X) et setScale(1f),
 * la taille reste bloquée pour toutes les frames suivantes.
 *
 * Solution : une instance BitmapFont par taille utilisée dans le jeu.
 * setScale() n'est plus jamais appelé dans render().
 *
 * Note : on utilise BitmapFont(FileHandle) avec la police par défaut
 * LibGDX (Arial 15px embarquée). Pour une vraie police pixel art,
 * remplacer par FreeTypeFontGenerator dans AssetLoader.
 */
class FontManager {

    // Toutes les tailles utilisées dans le projet
    val tiny:   BitmapFont = BitmapFont().apply { data.setScale(0.62f) }
    val small:  BitmapFont = BitmapFont().apply { data.setScale(0.75f) }
    val normal: BitmapFont = BitmapFont().apply { data.setScale(0.90f) }
    val medium: BitmapFont = BitmapFont().apply { data.setScale(1.05f) }
    val large:  BitmapFont = BitmapFont().apply { data.setScale(1.35f) }
    val title:  BitmapFont = BitmapFont().apply { data.setScale(2.20f) }

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
