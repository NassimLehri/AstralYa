package com.astralya.audio

import com.badlogic.gdx.audio.Music

/**
 * Epic 8 — AmbientManager pour les sons d'ambiance (vent, pluie) tournant en boucle.
 */
class AmbientManager {
    private var currentAmbient: Music? = null
    private var volume = 0.5f

    fun setVolume(v: Float) {
        volume = v
        currentAmbient?.volume = volume
    }

    fun play(music: Music) {
        if (music == currentAmbient) return
        currentAmbient?.stop()
        currentAmbient = music
        currentAmbient?.isLooping = true
        currentAmbient?.volume = volume
        currentAmbient?.play()
    }

    fun stop() {
        currentAmbient?.stop()
        currentAmbient = null
    }
}
