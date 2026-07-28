package com.astralya.engine.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.astralya.game.world.MapRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ResourceManagerTest {

    private lateinit var assetManager: AssetManager
    private lateinit var resourceManager: ResourceManager
    private lateinit var mapRegistry: MapRegistry

    @Before
    fun setUp() {
        assetManager = mock(AssetManager::class.java)
        resourceManager = ResourceManager(assetManager)
        mapRegistry = MapRegistry()
    }

    @Test
    fun `loadZone triggers asset loading`() {
        val village = mapRegistry.VILLAGE_DEPART
        resourceManager.loadZone(village.id, mapRegistry)
        
        // Verify TMX was requested
        verify(assetManager).load(contains("village.tmx"), any())
        
        // Verify required assets were requested
        village.requiredAssets.forEach { path ->
            verify(assetManager).load(eq(path), any())
        }
    }
}
