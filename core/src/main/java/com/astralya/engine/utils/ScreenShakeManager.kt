package com.astralya.engine.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Epic 14 — Gère les secousses de caméra de manière centralisée.
 */
class ScreenShakeManager {

    enum class Intensity(val value: Float) {
        LOW(4f), MEDIUM(10f), HIGH(20f), CRITICAL(40f)
    }

    private var time = 0f
    private var duration = 0f
    private var intensity = 0f
    private val offset = Vector2()

    fun shake(intensity: Intensity, duration: Int = 500) {
        this.intensity = intensity.value
        this.duration = duration / 1000f
        this.time = this.duration
    }

    fun update(delta: Float, camera: Camera) {
        if (time > 0) {
            time -= delta
            
            // Calcul du décalage (vibration aléatoire diminuant avec le temps)
            val currentIntensity = intensity * (time / duration)
            offset.x = MathUtils.random(-1f, 1f) * currentIntensity
            offset.y = MathUtils.random(-1f, 1f) * currentIntensity
            
            camera.translate(offset.x, offset.y, 0f)
            camera.update()
        }
    }

    fun isShaking() = time > 0
}
