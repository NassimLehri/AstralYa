package com.astralya.audio

import com.badlogic.gdx.audio.Sound

/**
 * Epic 8 — SfxManager avec système de priorités.
 */
class SfxManager {
    enum class Priority(val level: Int) { LOW(0), NORMAL(1), HIGH(2), CRITICAL(3) }
    
    private var volume = 1.0f
    
    fun setVolume(v: Float) { volume = v }

    fun play(sound: Sound, priority: Priority = Priority.NORMAL) {
        // Pour l'instant, on joue tout, mais on pourra limiter le nombre de voix
        // simultanées selon la priorité dans une version plus avancée.
        sound.play(volume)
    }
}
