package com.astralya.engine.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

/**
 * Lightweight diagnostic renderer that can render the map and overlay a simple grid for debugging.
 */
class DiagnosticTiledMapRenderer : OrthogonalTiledMapRenderer {

    private val shape = ShapeRenderer()

    constructor(map: TiledMap, unitScale: Float = 1f) : super(map, unitScale)
    constructor(map: TiledMap, unitScale: Float = 1f, batch: Batch) : super(map, unitScale, batch)


    fun renderWithDiagnostics(camera: OrthographicCamera, drawGrid: Boolean = false) {
        setView(camera)
        render()
        if (drawGrid) {
            // Overlay a translucent grid aligned to the camera for quick visual checks
            val cam = camera
            shape.projectionMatrix = cam.combined
            shape.begin(ShapeRenderer.ShapeType.Line)
            shape.color = Color(1f, 0f, 0f, 0.35f)
            // Draw a small grid centered on camera position
            val cell = 32f
            val left = cam.position.x - cam.viewportWidth * 0.5f
            val right = cam.position.x + cam.viewportWidth * 0.5f
            val bottom = cam.position.y - cam.viewportHeight * 0.5f
            val top = cam.position.y + cam.viewportHeight * 0.5f
            var x = (Math.floor((left / cell).toDouble()) * cell).toFloat()
            while (x < right) {
                shape.line(x, bottom, x, top)
                x += cell
            }
            var y = (Math.floor((bottom / cell).toDouble()) * cell).toFloat()
            while (y < top) {
                shape.line(left, y, right, y)
                y += cell
            }
            shape.end()
        }
    }

    fun disposeDiagnostics() {
        shape.dispose()
        dispose()
    }
}
