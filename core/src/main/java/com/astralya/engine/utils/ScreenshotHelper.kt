package com.astralya.engine.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Pixmap.Format

object ScreenshotHelper {
    @Volatile
    var enabled: Boolean = false
    private var captured: Boolean = false

    fun maybeCapture() {
        if (!enabled || captured) return
        try {
            val w = Gdx.graphics.width
            val h = Gdx.graphics.height
            val pixmap = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixmap(0, 0, w, h)
            val out = Gdx.files.local("screenshots/font_preview.png")
            PixmapIO.writePNG(out, pixmap)
            Gdx.app.log("ScreenshotHelper", "Saved screenshot to ${out.file().absolutePath}")
            pixmap.dispose()
            captured = true
        } catch (e: Throwable) {
            Gdx.app.error("ScreenshotHelper", "Failed to capture screenshot", e)
        }
    }
}