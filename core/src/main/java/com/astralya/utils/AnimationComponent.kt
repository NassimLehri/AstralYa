package com.astralya.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

enum class Direction { DOWN, LEFT, RIGHT, UP }

class AnimationComponent(textures: List<Texture>, frameWidth: Int = 64, frameHeight: Int = 64) {

    private val layers = textures.map { tex ->
        val regions = TextureRegion.split(tex, frameWidth, frameHeight)
        val rowCount = regions.size

        fun getAnimation(row: Int): Animation<TextureRegion> {
            val actualRow = if (row < rowCount) row else 0
            val frames = regions[actualRow]
            return Animation(0.1f, *frames)
        }

        // Standard LPC Layout: Up (8), Left (9), Down (10), Right (11)
        val up    = getAnimation(8).apply { playMode = Animation.PlayMode.LOOP }
        val left  = getAnimation(9).apply { playMode = Animation.PlayMode.LOOP }
        val down  = getAnimation(10).apply { playMode = Animation.PlayMode.LOOP }
        val right = getAnimation(11).apply { playMode = Animation.PlayMode.LOOP }
        
        mapOf(Direction.UP to up, Direction.LEFT to left, Direction.DOWN to down, Direction.RIGHT to right)
    }

    fun getKeyFrames(stateTime: Float, direction: Direction, isMoving: Boolean): List<TextureRegion> {
        return layers.map { anims ->
            val anim = anims[direction] ?: anims[Direction.DOWN]!!
            if (isMoving) anim.getKeyFrame(stateTime) else anim.getKeyFrame(0f)
        }
    }

    // Compatibility for single region usage
    fun getKeyFrame(stateTime: Float, direction: Direction, isMoving: Boolean): TextureRegion {
        return getKeyFrames(stateTime, direction, isMoving).last()
    }
}
