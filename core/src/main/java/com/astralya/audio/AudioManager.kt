package com.astralya.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

/**
 * Epic 8 — AudioManager agissant comme une Façade pour les sous-gestionnaires.
 */
class AudioManager {

    private val musicManager = MusicManager()
    private val sfxManager = SfxManager()
    private val ambientManager = AmbientManager()

    var masterVolume: Float = 1.0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            musicManager.setVolume(field * 0.7f)
            sfxManager.setVolume(field)
            ambientManager.setVolume(field * 0.5f)
        }

    fun playMusic(music: Music, loop: Boolean = true) {
        musicManager.play(music, loop)
    }

    fun stopMusic(fade: Boolean = true) {
        musicManager.stop(fade)
    }

    fun playSound(sound: Sound, priority: SfxManager.Priority = SfxManager.Priority.NORMAL) {
        sfxManager.play(sound, priority)
    }

    fun startAmbient(music: Music) {
        ambientManager.play(music)
    }

    fun stopAmbient() {
        ambientManager.stop()
    }

    fun update(delta: Float) {
        musicManager.update(delta)
    }

    fun dispose() {
        musicManager.stop(false)
        ambientManager.stop()
    }
}
