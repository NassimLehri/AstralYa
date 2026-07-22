package com.astralya.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class ParticleManager {

    private val effects = mutableMapOf<String, ParticleEffect>()
    private val activeEffects = mutableListOf<ParticleEffect>()

    fun loadEffect(name: String, path: String, imagesDir: String) {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal(path), Gdx.files.internal(imagesDir))
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
        val iterator = activeEffects.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()
            effect.update(delta)
            if (effect.isComplete) {
                effect.dispose()
                iterator.remove()
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

    fun dispose() {
        clear()
        effects.values.forEach { it.dispose() }
    }
}
