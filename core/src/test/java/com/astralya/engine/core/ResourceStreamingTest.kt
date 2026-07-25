package com.astralya.engine.core

import com.astralya.game.world.MapRegistry
import com.badlogic.gdx.assets.AssetManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ResourceStreamingTest {

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
    fun `test loadZone requests correct assets`() {
        val forest = mapRegistry.FORET_ENCHANTEE
        resourceManager.loadZone(forest.id, mapRegistry)
        
        // Verify forest specific assets are loaded
        forest.requiredAssets.forEach { asset ->
            verify(assetManager).load<Any>(eq(asset), any())
        }
    }

    @Test
    fun `test unloadUnused clears non-adjacent zones`() {
        val forest = mapRegistry.FORET_ENCHANTEE
        val village = mapRegistry.VILLAGE_DEPART
        
        // Simulate village assets loaded
        `when`(assetManager.isLoaded(anyString())).thenReturn(true)
        
        resourceManager.unloadUnused(forest.id, mapRegistry)
        
        // Village specific assets (not in forest) should be unloaded
        village.requiredAssets.filter { !forest.requiredAssets.contains(it) }.forEach { asset ->
            verify(assetManager).unload(eq(asset))
        }
    }
}
