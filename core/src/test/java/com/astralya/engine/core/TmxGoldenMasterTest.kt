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

    @Test
    fun `test village_tmx declares 32px tiles`() {
        // Parse TMX as plain XML to avoid native texture loading in unit tests
        val mapFile = File("../android/src/main/assets/maps/village.tmx")
        assertTrue("Le fichier village.tmx doit exister", mapFile.exists())

        val db = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = db.parse(mapFile)
        val mapEl = doc.documentElement
        val tileWidth = mapEl.getAttribute("tilewidth").toInt()
        val tileHeight = mapEl.getAttribute("tileheight").toInt()

        assertEquals("Le tilewidth doit être 32", 32, tileWidth)
        assertEquals("Le tileheight doit être 32", 32, tileHeight)

        // Ensure the layer 'Base_Water' is present
        val layers = mapEl.getElementsByTagName("layer")
        var found = false
        for (i in 0 until layers.length) {
            val el = layers.item(i) as org.w3c.dom.Element
            if (el.getAttribute("name") == "Base_Water") {
                found = true
                break
            }
        }
        assertTrue("La layer Base_Water doit exister", found)
    }
}
