package com.astralya.engine.utils

enum class BTStatus { SUCCESS, FAILURE, RUNNING }

/**
 * Epic 16 — Moteur d'Arbre de Comportement (BT) simplifié.
 */
interface BTNode {
    fun tick(): BTStatus
}

class BTSequence(private val children: List<BTNode>) : BTNode {
    private var currentIndex = 0

    override fun tick(): BTStatus {
        if (currentIndex >= children.size) {
            currentIndex = 0
            return BTStatus.SUCCESS
        }

        val status = children[currentIndex].tick()
        return when (status) {
            BTStatus.SUCCESS -> {
                currentIndex++
                if (currentIndex >= children.size) {
                    currentIndex = 0
                    BTStatus.SUCCESS
                } else BTStatus.RUNNING
            }
            else -> status
        }
    }
}

class BTSelector(private val children: List<BTNode>) : BTNode {
    private var currentIndex = 0

    override fun tick(): BTStatus {
        if (currentIndex >= children.size) {
            currentIndex = 0
            return BTStatus.FAILURE
        }

        val status = children[currentIndex].tick()
        return when (status) {
            BTStatus.FAILURE -> {
                currentIndex++
                if (currentIndex >= children.size) {
                    currentIndex = 0
                    BTStatus.FAILURE
                } else BTStatus.RUNNING
            }
            else -> status
        }
    }
}

class BTLeaf(private val action: () -> BTStatus) : BTNode {
    override fun tick(): BTStatus = action()
}
