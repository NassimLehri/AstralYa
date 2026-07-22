package com.astralya.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

enum class Direction { DOWN, LEFT, RIGHT, UP }

class AnimationComponent(texture: Texture, frameWidth: Int = 64, frameHeight: Int = 64) {

    private val walkDown: Animation<TextureRegion>
    private val walkLeft: Animation<TextureRegion>
    private val walkRight: Animation<TextureRegion>
    private val walkUp: Animation<TextureRegion>

    init {
        val regions = TextureRegion.split(texture, frameWidth, frameHeight)
        val rowCount = regions.size

        // Safety: If the texture is too small for LPC layout, we fall back to row 0
        fun getAnimation(row: Int): Animation<TextureRegion> {
            val actualRow = if (row < rowCount) row else 0
            val frames = regions[actualRow]
            // Use all available frames in the row
            return Animation(0.1f, *frames)
        }

        // Standard LPC Layout: Up (8), Left (9), Down (10), Right (11)
        walkUp    = getAnimation(8)
        walkLeft  = getAnimation(9)
        walkDown  = getAnimation(10)
        walkRight = getAnimation(11)

        walkUp.playMode    = Animation.PlayMode.LOOP
        walkLeft.playMode  = Animation.PlayMode.LOOP
        walkDown.playMode  = Animation.PlayMode.LOOP
        walkRight.playMode = Animation.PlayMode.LOOP
    }

    fun getKeyFrame(stateTime: Float, direction: Direction, isMoving: Boolean): TextureRegion {
        val animation = when (direction) {
            Direction.DOWN  -> walkDown
            Direction.LEFT  -> walkLeft
            Direction.RIGHT -> walkRight
            Direction.UP    -> walkUp
        }
        return if (isMoving) {
            animation.getKeyFrame(stateTime)
        } else {
            animation.getKeyFrame(0f) // Idle is first frame of walk
        }
    }
}
