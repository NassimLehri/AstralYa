package com.astralya.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class AudioEngineTest {

    private lateinit var audioManager: AudioManager
    private lateinit var music1: Music
    private lateinit var music2: Music
    private lateinit var sound: Sound

    @Before
    fun setUp() {
        audioManager = AudioManager()
        music1 = mock(Music::class.java)
        music2 = mock(Music::class.java)
        sound = mock(Sound::class.java)
    }

    @Test
    fun `test playMusic immediately starts if no current music`() {
        audioManager.playMusic(music1)
        verify(music1).play()
        // No crossfade if current is null
    }

    @Test
    fun `test music crossfade on second play`() {
        audioManager.playMusic(music1)
        reset(music1)
        
        `when`(music1.isPlaying).thenReturn(true)
        audioManager.playMusic(music2)
        
        verify(music2).play()
        // Crossfade logic: update will gradually change volumes
        audioManager.update(0.75f) // Halfway FADE_TIME (1.5s)
        
        verify(music1, atLeastOnce()).volume = anyFloat()
        verify(music2, atLeastOnce()).volume = anyFloat()
    }

    @Test
    fun `test playSound plays with current volume`() {
        audioManager.masterVolume = 0.5f
        audioManager.playSound(sound)
        verify(sound).play(0.5f)
    }
}
