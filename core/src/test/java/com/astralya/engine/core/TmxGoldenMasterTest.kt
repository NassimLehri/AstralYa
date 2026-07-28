package com.astralya.engine.core

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.invocation.InvocationOnMock
import java.io.File

class TmxGoldenMasterTest {

    @Before
    fun setUp() {
        Gdx.app = mock(Application::class.java)
    }

    @Test
    fun `test village_tmx tile regions are not whole textures`() {
        val files = mock(com.badlogic.gdx.Files::class.java)
        Gdx.files = files
        `when`(files.internal(anyString())).thenAnswer { inv: InvocationOnMock ->
            val path = inv.getArgument<String>(0)
            com.badlogic.gdx.files.FileHandle(File(path))
        }
        `when`(files.local(anyString())).thenAnswer { inv: InvocationOnMock ->
            val path = inv.getArgument<String>(0)
            com.badlogic.gdx.files.FileHandle(File(path))
        }

        val loader = TmxMapLoader() 
        val mapFile = "../android/src/main/assets/maps/village.tmx"
        val map = loader.load(mapFile)
        
        assertNotNull("Map village.tmx non chargée", map)
        
        val layer = map.layers.get("Base_Water") as TiledMapTileLayer
        val cell = layer.getCell(0, 0)
        assertNotNull("Cell (0,0) de Base_Water vide", cell)
        
        val tile = cell.tile
        val region = tile.textureRegion
        
        println("Tile region: ${region.regionWidth}x${region.regionHeight} at ${region.regionX},${region.regionY}")
        
        // If it's repeating the whole texture, width/height would be 512
        assertNotEquals("ERREUR: Le tile region est la texture entière (512px)", 512, region.regionWidth)
        assertEquals("Le tile doit faire 32px de large", 32, region.regionWidth)
        assertEquals("Le tile doit faire 32px de haut", 32, region.regionHeight)
        
        map.dispose()
    }
}
