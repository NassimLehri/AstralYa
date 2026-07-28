package com.astralya.engine.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

/**
 * Diagnostic renderer to provide evidence of tile mapping issues.
 */
class DiagnosticTiledMapRenderer(map: TiledMap, unitScale: Float, batch: Batch) 
    : OrthogonalTiledMapRenderer(map, unitScale, batch) {

    private val layerRenderCounts = mutableMapOf<String, Int>()
    var auditFrame = false

    override fun renderTileLayer(layer: TiledMapTileLayer) {
        val name = layer.name ?: "unnamed"
        val count = layerRenderCounts.getOrDefault(name, 0) + 1
        layerRenderCounts[name] = count

        if (auditFrame) {
            performDeepAudit(layer)
        }

        super.renderTileLayer(layer)
    }

    private fun performDeepAudit(layer: TiledMapTileLayer) {
        Gdx.app.log("ASTRA_AUDIT", "--- AUDIT: ${layer.name} ---")
        Gdx.app.log("ASTRA_AUDIT", "Render Call Count: ${layerRenderCounts[layer.name]}")
        
        var nonEmpty = 0
        var minGid = Int.MAX_VALUE
        var maxGid = Int.MIN_VALUE

        for (y in 0 until layer.height) {
            for (x in 0 until layer.width) {
                val cell = layer.getCell(x, y) ?: continue
                nonEmpty++
                val gid = cell.tile.id
                if (gid < minGid) minGid = gid
                if (gid > maxGid) maxGid = gid

                // Pinpoint Zone 10,10 to 15,15
                if (x in 10..15 && y in 10..15) {
                    val tile = cell.tile
                    val reg = tile.textureRegion
                    Gdx.app.log("ASTRA_AUDIT", "Tile($x,$y) | GID:$gid | Pixels:(${reg.regionX},${reg.regionY}) ${reg.regionWidth}x${reg.regionHeight} | UV:(${reg.u},${reg.v})")
                }
            }
        }
        Gdx.app.log("ASTRA_AUDIT", "Non-empty Tiles: $nonEmpty | GID Range: $minGid to $maxGid")
    }
    
    fun resetCounts() {
        layerRenderCounts.clear()
    }
}
