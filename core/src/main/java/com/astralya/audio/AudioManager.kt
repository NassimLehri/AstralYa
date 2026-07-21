package com.astralya.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

/**
 * AudioManager avec fade-out/fade-in entre les musiques.
 *
 * update(delta) doit être appelé depuis AstralYaGame.render()
 * pour que les fades progressent à chaque frame.
 *
 * Séquence de transition :
 *   1. fade-out de la musique courante (FADE_DURATION secondes)
 *   2. stop + play de la nouvelle musique
 *   3. fade-in de la nouvelle musique
 */
class AudioManager {

    var musicVolume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            if (fadeState == FadeState.IDLE) currentMusic?.volume = field
        }

    var sfxVolume: Float = 1.0f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }
    var isMusicEnabled: Boolean = true
    var isSfxEnabled:   Boolean = true

    private var currentMusic:  Music? = null
    private var pendingMusic:  Music? = null
    private var pendingLoop:   Boolean = true

    private enum class FadeState { IDLE, FADE_OUT, FADE_IN }
    private var fadeState  = FadeState.IDLE
    private var fadeTimer  = 0f
    private val FADE_DURATION = 0.5f   // secondes

    // ── API publique ──────────────────────────────────────────────────────────

    /**
     * Demande un changement de musique avec fade.
     * Si c'est la même musique déjà en cours, ne fait rien.
     */
    fun playMusic(music: Music, loop: Boolean = true) {
        if (music === currentMusic && currentMusic?.isPlaying == true) return
        if (!isMusicEnabled) {
            switchImmediately(music, loop); return
        }
        pendingMusic = music
        pendingLoop  = loop
        if (currentMusic != null && currentMusic!!.isPlaying) {
            fadeState = FadeState.FADE_OUT
            fadeTimer = 0f
        } else {
            startFadeIn(music, loop)
        }
    }

    /** Arrêt immédiat avec fade-out optionnel */
    fun stopMusic(fade: Boolean = true) {
        if (!fade || currentMusic == null) {
            currentMusic?.stop(); return
        }
        pendingMusic = null
        fadeState    = FadeState.FADE_OUT
        fadeTimer    = 0f
    }

    fun pauseMusic()  { currentMusic?.pause() }
    fun resumeMusic() { if (isMusicEnabled) currentMusic?.play() }


    fun playSound(sound: Sound) {
        if (isSfxEnabled) sound.play(sfxVolume)
    }

    // ── update() — appelé depuis AstralYaGame.render() ───────────────────────

    fun update(delta: Float) {
        when (fadeState) {
            FadeState.FADE_OUT -> {
                fadeTimer += delta
                val t = (fadeTimer / FADE_DURATION).coerceIn(0f, 1f)
                currentMusic?.volume = musicVolume * (1f - t)
                if (t >= 1f) {
                    currentMusic?.stop()
                    val next = pendingMusic
                    if (next != null) startFadeIn(next, pendingLoop)
                    else fadeState = FadeState.IDLE
                }
            }
            FadeState.FADE_IN -> {
                fadeTimer += delta
                val t = (fadeTimer / FADE_DURATION).coerceIn(0f, 1f)
                currentMusic?.volume = musicVolume * t
                if (t >= 1f) {
                    currentMusic?.volume = musicVolume
                    fadeState = FadeState.IDLE
                }
            }
            FadeState.IDLE -> {}
        }
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private fun startFadeIn(music: Music, loop: Boolean) {
        currentMusic = music
        music.volume  = 0f
        music.isLooping = loop
        if (isMusicEnabled) music.play()
        fadeState = FadeState.FADE_IN
        fadeTimer = 0f
        pendingMusic = null
    }

    private fun switchImmediately(music: Music, loop: Boolean) {
        currentMusic?.stop()
        currentMusic   = music
        music.volume   = musicVolume
        music.isLooping = loop
        music.play()
        fadeState = FadeState.IDLE
    }

    fun dispose() { currentMusic?.dispose() }
}
