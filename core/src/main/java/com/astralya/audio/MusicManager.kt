package com.astralya.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.MathUtils

/**
 * Epic 8 — MusicManager avec support du crossfade.
 */
class MusicManager {
    private var currentMusic: Music? = null
    private var nextMusic: Music? = null
    
    private var currentVolume = 0f
    private var nextVolume = 0f
    
    private var fadeTimer = 0f
    private val FADE_TIME = 1.5f // secondes
    
    private var isCrossfading = false
    private var masterVolume = 0.7f

    fun setVolume(volume: Float) {
        masterVolume = volume
        if (!isCrossfading) currentMusic?.volume = masterVolume
    }

    fun play(music: Music, loop: Boolean = true) {
        if (music == currentMusic) return
        
        if (currentMusic == null) {
            currentMusic = music
            currentMusic?.volume = masterVolume
            currentMusic?.isLooping = loop
            currentMusic?.play()
        } else {
            nextMusic = music
            nextMusic?.volume = 0f
            nextMusic?.isLooping = loop
            nextMusic?.play()
            
            fadeTimer = 0f
            isCrossfading = true
        }
    }

    fun update(delta: Float) {
        if (!isCrossfading) return
        
        fadeTimer += delta
        val progress = MathUtils.clamp(fadeTimer / FADE_TIME, 0f, 1f)
        
        currentMusic?.volume = masterVolume * (1f - progress)
        nextMusic?.volume = masterVolume * progress
        
        if (progress >= 1f) {
            currentMusic?.stop()
            currentMusic = nextMusic
            nextMusic = null
            isCrossfading = false
        }
    }

    fun stop(fade: Boolean = true) {
        if (!fade) {
            currentMusic?.stop()
            nextMusic?.stop()
            currentMusic = null
            nextMusic = null
            isCrossfading = false
            return
        }
        // Start fading current to 0 without a next music
        isCrossfading = true
        fadeTimer = 0f
        nextMusic = null 
    }

    fun pause() = currentMusic?.pause()
    fun resume() = currentMusic?.play()
}
