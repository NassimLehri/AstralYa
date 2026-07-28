package com.astralya

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils

/**
 * MinimalTiledTest - A 100% vanilla LibGDX test harness.
 * No custom passes, no shaders, no filtering logic.
 */
class MinimalTiledTest : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private var renderer: OrthogonalTiledMapRenderer? = null
    private var map: TiledMap? = null
    
    private val mapsToTest = listOf(
        "maps/village.tmx",
        "maps/maison_interieur.tmx",
        "maps/grotte.tmx",
        "maps/cite_volante.tmx"
    )
    private var currentMapIndex = 0
    private var frameCount = 0

    override fun create() {
        camera = OrthographicCamera()
        loadCurrentMap()
    }

    private fun loadCurrentMap() {
        val mapPath = mapsToTest[currentMapIndex]
        println("\n>>> TESTING MAP: $mapPath")
        
        renderer?.dispose()
        map?.dispose()
        
        try {
            val loader = TmxMapLoader()
            map = loader.load(mapPath)
            val currentMap = map!!
            
            // LOG AUDIT INFO
            val props = currentMap.properties
            println("Map Dimensions: ${props.get("width")} x ${props.get("height")} tiles")
            println("Tile Size: ${props.get("tilewidth")} x ${props.get("tileheight")}")
            println("Orientation: ${props.get("orientation")}")
            
            println("Tilesets:")
            currentMap.tileSets.forEach { ts ->
                println("  - Name: ${ts.name}")
                // firstgid is usually a property of the tileset in the TMX
                // but ts.properties might not have it directly if it's external.
                // It's technically part of the TiledMapTileSet collection mapping.
            }
            
            renderer = OrthogonalTiledMapRenderer(currentMap, 1f)
            
            // Center camera on map
            val mapWidth = props.get("width", Int::class.java) * props.get("tilewidth", Int::class.java).toFloat()
            val mapHeight = props.get("height", Int::class.java) * props.get("tileheight", Int::class.java).toFloat()
            camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            camera.position.set(mapWidth / 2f, mapHeight / 2f, 0f)
            camera.update()
            
            frameCount = 0
        } catch (e: Exception) {
            System.err.println("FAILED TO LOAD $mapPath: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        val currentRenderer = renderer ?: return
        
        currentRenderer.setView(camera)
        currentRenderer.render()
        
        frameCount++
        
        // Take screenshot on frame 5 (ensure everything is pushed to GPU)
        if (frameCount == 5) {
            takeScreenshot()
            
            // Move to next map or exit
            currentMapIndex++
            if (currentMapIndex < mapsToTest.size) {
                loadCurrentMap()
            } else {
                println("ALL TESTS COMPLETED. EXITING.")
                Gdx.app.exit()
            }
        }
    }

    private fun takeScreenshot() {
        val mapName = mapsToTest[currentMapIndex].substringAfterLast("/").substringBeforeLast(".")
        val fileName = "baseline_$mapName.png"
        
        val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)
        val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
        BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
        
        // Save to current dir
        val handle = Gdx.files.local(fileName)
        PixmapIO.writePNG(handle, pixmap)
        pixmap.dispose()
        
        println("SAVED SCREENSHOT: ${handle.file().absolutePath}")
    }

    override fun dispose() {
        renderer?.dispose()
        map?.dispose()
    }
}
