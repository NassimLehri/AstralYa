package com.astralya.game.combat.ai

import com.astralya.game.entities.*
import com.astralya.game.combat.CombatState
import com.astralya.engine.utils.GameRandom

/**
 * Epic 2 — Interface pour les stratégies d'IA de combat.
 */
interface AiStrategy {
    fun selectAction(enemy: Enemy, state: CombatState, rng: GameRandom): EnemySkill?
}

class AggressiveAi : AiStrategy {
    override fun selectAction(enemy: Enemy, state: CombatState, rng: GameRandom): EnemySkill? {
        if (enemy.skills.isEmpty()) return null
        // Priorité aux sorts de zone ou puissants
        return rng.pick(enemy.skills.filter { !it.isHeal })
    }
}

class SupportAi : AiStrategy {
    override fun selectAction(enemy: Enemy, state: CombatState, rng: GameRandom): EnemySkill? {
        // Soin si lui ou un allié est blessé
        val wounded = state.enemies.filter { it.isAlive && it.currentHp < it.maxHp * 0.7f }
        if (wounded.isNotEmpty()) {
            val healSkill = enemy.skills.find { it.isHeal }
            if (healSkill != null) return healSkill
        }
        return rng.pick(enemy.skills)
    }
}

class TacticalAi : AiStrategy {
    override fun selectAction(enemy: Enemy, state: CombatState, rng: GameRandom): EnemySkill? {
        // Utilise des altérations d'état
        val debuffSkills = enemy.skills.filter { it.statusEffect != StatusEffect.NONE }
        if (debuffSkills.isNotEmpty() && rng.nextBool(0.6f)) return rng.pick(debuffSkills)
        return rng.pick(enemy.skills)
    }
}

class BossAi : AiStrategy {
    override fun selectAction(enemy: Enemy, state: CombatState, rng: GameRandom): EnemySkill? {
        val hpRatio = enemy.currentHp.toFloat() / enemy.maxHp
        // Phase furieuse
        if (hpRatio < 0.3f) {
            val ultimate = enemy.skills.maxByOrNull { it.basePower }
            if (ultimate != null && rng.nextBool(0.4f)) return ultimate
        }
        return rng.pick(enemy.skills)
    }
}

object AiRegistry {
    private val strategies = mapOf(
        "AGGRESSIVE" to AggressiveAi(),
        "SUPPORT" to SupportAi(),
        "TACTICAL" to TacticalAi(),
        "BOSS" to BossAi(),
        "RANDOM" to AggressiveAi() // fallback
    )

    fun get(type: String): AiStrategy = strategies[type.uppercase()] ?: strategies["RANDOM"]!!
}
