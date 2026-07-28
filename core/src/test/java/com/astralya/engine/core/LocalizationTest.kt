package com.astralya.engine.core

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files
import com.badlogic.gdx.files.FileHandle
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File
import java.util.Locale

class LocalizationTest {

    private lateinit var localization: LocalizationManager

    @Before
    fun setUp() {
        val app = mock(Application::class.java)
        Gdx.app = app
        val files = mock(Files::class.java)
        Gdx.files = files
        
        `when`(files.internal(anyString())).thenAnswer { inv ->
            val path = inv.getArgument<String>(0)
            val realFile = File("../android/src/main/assets/" + path + ".properties")
            val handle = mock(FileHandle::class.java)
            `when`(handle.exists()).thenReturn(realFile.exists())
            if (realFile.exists()) {
                `when`(handle.read()).thenReturn(realFile.inputStream())
            }
            handle
        }
        
        localization = LocalizationManager()
    }

    @Test
    fun `test key retrieval in french`() {
        localization.loadBundle(Locale.FRENCH)
        assertEquals("Nouvelle Partie", localization.get("menu.new_game"))
    }

    @Test
    fun `test key retrieval in english`() {
        localization.loadBundle(Locale.ENGLISH)
        assertEquals("New Game", localization.get("menu.new_game"))
    }

    @Test
    fun `test formatting with arguments`() {
        localization.loadBundle(Locale.FRENCH)
        val formatted = localization.format("ui.gold", 500)
        assertEquals("Or: 500", formatted)
    }
}
