package com.astralya.engine.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class ParticleManager {

    private val effects = mutableMapOf<String, ParticleEffect>()
    private val activeEffects = mutableListOf<ParticleEffect>()

    fun loadEffect(name: String, path: String, imagesDir: String) {
        val effect = ParticleEffect()
        try {
            // Allow loading from either an atlas or image directory
            effect.load(Gdx.files.internal(path), Gdx.files.internal(imagesDir))
        } catch (e: Exception) {
            Gdx.app.error("ParticleManager", "Failed to load effect $name from $path:$imagesDir - trying atlas fallback", e)
            // Fallback: try loading from atlas if imagesDir points to an atlas
            try {
                val atlas = com.badlogic.gdx.graphics.g2d.TextureAtlas(Gdx.files.internal(imagesDir))
                effect.load(Gdx.files.internal(path), Gdx.files.internal(""))
                // replace textures from atlas where possible
                for (region in atlas.regions) {
                    // no-op: ParticleEffect will reference regions by name if particle file used atlas names
                }
            } catch (_: Exception) {
                // give up, rethrow original
                throw e
            }
        }
        effects[name] = effect
    }

    fun spawn(name: String, x: Float, y: Float): ParticleEffect? {
        val proto = effects[name] ?: return null
        val newEffect = ParticleEffect(proto)
        newEffect.setPosition(x, y)
        newEffect.start()
        activeEffects.add(newEffect)
        return newEffect
    }

    fun update(delta: Float) {
        for (i in activeEffects.indices.reversed()) {
            val effect = activeEffects[i]
            effect.update(delta)
            if (effect.isComplete) {
                effect.dispose()
                activeEffects.removeAt(i)
            }
        }
    }

    fun draw(batch: SpriteBatch) {
        for (effect in activeEffects) {
            effect.draw(batch)
        }
    }

    fun clear() {
        activeEffects.forEach { it.dispose() }
        activeEffects.clear()
    }

    fun activeCount() = activeEffects.size

    fun dispose() {
        clear()
        effects.values.forEach { it.dispose() }
    }
}
