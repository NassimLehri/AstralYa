package com.astralya.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

/**
 * Gère le cycle Jour/Nuit.
 * Cycle complet : 720 secondes (12 minutes).
 */
class TimeSystem {

    private var timeOfDay = 0.25f // Démarre le matin (0.0 = Minuit, 0.5 = Midi)
    private val cycleDuration = 720f 

    private val colorNight = Color(0.2f, 0.2f, 0.5f, 1f)
    private val colorDawn  = Color(1.0f, 0.7f, 0.5f, 1f)
    private val colorDay   = Color(1.0f, 1.0f, 1.0f, 1f)
    private val colorDusk  = Color(0.8f, 0.4f, 0.3f, 1f)

    private val tempColor = Color()

    fun update(delta: Float) {
        timeOfDay = (timeOfDay + delta / cycleDuration) % 1.0f
    }

    fun getAmbientColor(baseTint: Color): Color {
        val t = timeOfDay
        
        // Interpolation simple du cycle
        when {
            t < 0.2f -> lerpColors(colorNight, colorDawn, t / 0.2f)           // Minuit -> Aube
            t < 0.3f -> lerpColors(colorDawn, colorDay, (t - 0.2f) / 0.1f)    // Aube -> Jour
            t < 0.7f -> tempColor.set(colorDay)                               // Jour plein
            t < 0.8f -> lerpColors(colorDay, colorDusk, (t - 0.7f) / 0.1f)    // Jour -> Crépuscule
            t < 0.9f -> lerpColors(colorDusk, colorNight, (t - 0.8f) / 0.1f)  // Crépuscule -> Nuit
            else -> tempColor.set(colorNight)                                 // Nuit noire
        }
        
        // On multiplie par la teinte de base de la map
        tempColor.r *= baseTint.r
        tempColor.g *= baseTint.g
        tempColor.b *= baseTint.b
        
        return tempColor
    }

    private fun lerpColors(c1: Color, c2: Color, progress: Float) {
        tempColor.set(c1).lerp(c2, progress)
    }

    fun getTimeString(): String {
        val totalMinutes = (timeOfDay * 24 * 60).toInt()
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return String.format("%02d:%02d", hours, mins)
    }
    
    fun isNight(): Boolean = timeOfDay < 0.2f || timeOfDay > 0.85f
}
