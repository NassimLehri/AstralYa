package com.astralya.engine.core

import com.astralya.game.world.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.astralya.engine.utils.Direction

/**
 * Epic 16 — Gère les instances de PNJ et leurs routines à l'écran.
 */
class NPCInstance(val data: NPC) {
    var x = data.position.x
    var y = data.position.y
    var currentDir = Direction.DOWN
    var isMoving = false
    var isInteracting = false
    
    var currentTaskIndex = 0
    var taskTimer = 0f

    fun update(delta: Float) {
        if (isInteracting) {
            isMoving = false
            return
        }

        val routine = data.routine ?: return
        if (currentTaskIndex >= routine.tasks.size) {
            if (routine.loop) currentTaskIndex = 0 else return
        }

        val task = routine.tasks[currentTaskIndex]
        when (task.type) {
            NPCTaskType.WAIT -> {
                isMoving = false
                taskTimer += delta
                if (taskTimer >= task.duration) {
                    advanceTask()
                }
            }
            NPCTaskType.MOVE_TO -> {
                isMoving = true
                val speed = 80f // NPC speed
                val dx = task.targetX - x
                val dy = task.targetY - y
                val dist = Vector2.dst(x, y, task.targetX, task.targetY)
                
                if (dist < 2f) {
                    x = task.targetX
                    y = task.targetY
                    advanceTask()
                } else {
                    val angle = MathUtils.atan2(dy, dx)
                    x += MathUtils.cos(angle) * speed * delta
                    y += MathUtils.sin(angle) * speed * delta
                    
                    // Update Direction
                    if (Math.abs(dx) > Math.abs(dy)) {
                        currentDir = if (dx > 0) Direction.RIGHT else Direction.LEFT
                    } else {
                        currentDir = if (dy > 0) Direction.UP else Direction.DOWN
                    }
                }
            }
            NPCTaskType.PLAY_ANIM -> {
                isMoving = false
                // Logic for special animations...
                advanceTask()
            }
        }
    }

    private fun advanceTask() {
        currentTaskIndex++
        taskTimer = 0f
    }
}
