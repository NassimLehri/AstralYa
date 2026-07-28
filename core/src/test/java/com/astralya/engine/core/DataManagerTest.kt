package com.astralya.engine.core

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files
import com.badlogic.gdx.files.FileHandle
import com.astralya.game.entities.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class DataManagerTest {

    @Before
    fun setUp() {
        val app = mock(Application::class.java)
        Gdx.app = app
        val files = mock(Files::class.java)
        Gdx.files = files
        
        // Mock loading from real files for the test if possible, or just mock the file handles
        // Since I'm in the IDE, I can try to point to the actual assets
        val assetsPath = "android/src/main/assets/data/"
        
        `when`(files.internal(anyString())).thenAnswer { inv ->
            val path = inv.getArgument<String>(0)
            // Redirect to the relative path from the core module perspective
            val realFile = File("../" + path)
            val handle = mock(FileHandle::class.java)
            `when`(handle.exists()).thenReturn(realFile.exists())
            if (realFile.exists()) {
                `when`(handle.readString()).thenReturn(realFile.readText())
            }
            handle
        }
    }

    @Test
    fun `test loadAll and retrieval`() {
        DataManager.loadAll()
        
        val items = DataManager.getAllItems()
        assertTrue("Items should be loaded", items.isNotEmpty())
        assertNotNull("Herbe de soin should exist", DataManager.getItem("herbe_soin"))
        
        val enemies = DataManager.getAllEnemies()
        assertTrue("Enemies should be loaded", enemies.isNotEmpty())
        assertNotNull("Slime vert should exist", DataManager.getEnemy("slime_vert"))
        
        val skill = DataManager.getSkill("coup_stellaire")
        assertNotNull("Skill should exist", skill)
        assertEquals(Element.STELLAR, skill?.element)
    }
}
