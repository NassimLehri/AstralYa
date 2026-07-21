package com.astralya.combat

import com.astralya.entities.*
import com.astralya.utils.GameRandom
import kotlin.math.roundToInt

sealed class CombatAction {
    data class Attack(val hero: Hero, val target: Enemy)                          : CombatAction()
    data class UseSkill(val hero: Hero, val skill: Skill, val target: Any?)       : CombatAction()
    data class UseCombo(val heroes: List<Hero>, val combo: ComboSkill, val targets: List<Enemy>) : CombatAction()
    data class UseItem(val hero: Hero, val item: Item, val target: Hero)          : CombatAction()
    object Flee : CombatAction()
}

data class ActionResult(
    val message: String,
    val damageDealt: Int  = 0,
    val healingDone: Int  = 0,
    val mpUsed: Int       = 0,
    val statusApplied: StatusEffect = StatusEffect.NONE,
    val targetRevived: Boolean = false,
    val critical: Boolean = false,
    val missed: Boolean   = false
)

data class CombatState(
    val party: List<Hero>,
    val enemies: List<Enemy>,
    val turn: Int = 1,
    val isPlayerTurn: Boolean = true,
    val log: MutableList<String> = mutableListOf(),
    val isOver: Boolean = false,
    val playerWon: Boolean = false
)

/**
 * FIX PERF #6 — CombatSystem reçoit GameRandom par injection.
 * Tous les appels Math.random() / .random() sont remplacés par rng.
 */
class CombatSystem(private val rng: GameRandom) {

    fun initCombat(party: List<Hero>, enemies: List<Enemy>): CombatState =
        CombatState(party = party, enemies = enemies)
            .also { it.log.add("⚔️ Le combat commence !") }

    fun getTurnOrder(state: CombatState): List<Any> {
        val combatants = mutableListOf<Pair<Any, Int>>()
        state.party.filter  { it.isAlive }.forEach { combatants += it to it.agility }
        state.enemies.filter{ it.isAlive }.forEach { combatants += it to it.agility }
        return combatants.sortedByDescending { it.second }.map { it.first }
    }

    fun executeHeroAction(action: CombatAction, state: CombatState): List<ActionResult> =
        when (action) {
            is CombatAction.Attack   -> listOf(executeAttack(action.hero, action.target))
            is CombatAction.UseSkill -> listOf(executeSkill(action.hero, action.skill, action.target, state))
            is CombatAction.UseCombo -> executeCombo(action.heroes, action.combo, action.targets)
            is CombatAction.UseItem  -> listOf(executeItem(action.hero, action.item, action.target))
            is CombatAction.Flee     -> listOf(ActionResult("L'équipe prend la fuite !"))
        }

    private fun executeAttack(hero: Hero, enemy: Enemy): ActionResult {
        // FIX PERF #6 — rng.nextBool / rng.nextFloat
        if (rng.nextBool(0.05f))
            return ActionResult("${hero.name} rate son attaque !", missed = true)

        val isCrit = rng.nextBool(0.10f)
        var damage = hero.totalAttack() + rng.nextInt(5, 16)
        if (isCrit) damage = (damage * 1.5f).roundToInt()
        val dealt = enemy.takeDamage(damage)
        return ActionResult(
            if (isCrit) "⭐ CRITIQUE ! ${hero.name} inflige $dealt dégâts à ${enemy.name} !"
            else        "${hero.name} attaque ${enemy.name} pour $dealt dégâts.",
            damageDealt = dealt, critical = isCrit
        )
    }

    private fun executeSkill(
        hero: Hero, skill: Skill, target: Any?, state: CombatState
    ): ActionResult {
        if (!hero.useMp(skill.mpCost))
            return ActionResult("${hero.name} n'a pas assez de MP pour ${skill.name} !")

        return when (skill.type) {
            SkillType.ATTACK -> {
                if (skill.hitAll) {
                    var totalDmg = 0
                    state.enemies.filter { it.isAlive }.forEach { e ->
                        totalDmg += e.takeDamage(calcMagicDamage(hero, skill))
                    }
                    ActionResult("${hero.name} lance ${skill.name} ! ($totalDmg dégâts totaux)",
                        damageDealt = totalDmg, mpUsed = skill.mpCost)
                } else {
                    val enemy = target as? Enemy ?: state.enemies.first { it.isAlive }
                    val dealt = enemy.takeDamage(calcMagicDamage(hero, skill))
                    ActionResult("${hero.name} utilise ${skill.name} sur ${enemy.name} pour $dealt dégâts !",
                        damageDealt = dealt, mpUsed = skill.mpCost)
                }
            }
            SkillType.HEAL -> {
                if (skill.healAmount == -1) {
                    val ally = target as? Hero ?: state.party.firstOrNull { !it.isAlive }
                    return if (ally != null && !ally.isAlive) {
                        ally.revive(0.5f)
                        ActionResult("✨ ${hero.name} ressuscite ${ally.name} !",
                            targetRevived = true, mpUsed = skill.mpCost)
                    } else {
                        ActionResult("Aucune cible valide pour la résurrection.", mpUsed = skill.mpCost)
                    }
                }
                if (skill.hitAll) {
                    var total = 0
                    state.party.filter { it.isAlive }.forEach { ally ->
                        total += ally.heal(skill.healAmount + hero.totalMagic() / 2)
                    }
                    ActionResult("💚 ${hero.name} soigne toute l'équipe ! (+$total HP)",
                        healingDone = total, mpUsed = skill.mpCost)
                } else {
                    val ally   = target as? Hero ?: hero
                    val healed = ally.heal(skill.healAmount + hero.totalMagic() / 2)
                    ActionResult("💚 ${hero.name} soigne ${ally.name} pour $healed HP.",
                        healingDone = healed, mpUsed = skill.mpCost)
                }
            }
            SkillType.BUFF -> {
                if (skill.hitAll) {
                    state.party.filter { it.isAlive }.forEach { it.statusEffect = skill.statusEffect }
                    ActionResult("🛡️ ${hero.name} applique ${skill.name} à toute l'équipe !",
                        statusApplied = skill.statusEffect, mpUsed = skill.mpCost)
                } else {
                    val ally = target as? Hero ?: hero
                    ally.statusEffect = skill.statusEffect
                    ActionResult("🛡️ ${hero.name} protège ${ally.name} !",
                        statusApplied = skill.statusEffect, mpUsed = skill.mpCost)
                }
            }
            SkillType.DEBUFF -> {
                val enemy = target as? Enemy ?: state.enemies.first { it.isAlive }
                enemy.statusEffect = skill.statusEffect
                ActionResult("💀 ${hero.name} inflige ${skill.statusEffect} à ${enemy.name} !",
                    statusApplied = skill.statusEffect, mpUsed = skill.mpCost)
            }
            SkillType.COMBO -> ActionResult("Utilisez UseCombo pour les combos !")
        }
    }

    private fun executeCombo(
        heroes: List<Hero>, combo: ComboSkill, targets: List<Enemy>
    ): List<ActionResult> {
        val results = mutableListOf<ActionResult>()
        heroes.forEach { hero ->
            if (!hero.useMp(combo.mpCostPerHero)) {
                results += ActionResult("${hero.name} n'a pas assez de MP pour le combo !")
                return results
            }
        }
        val avgMagic = heroes.sumOf { it.totalMagic() } / heroes.size.coerceAtLeast(1)
        val baseDmg  = combo.basePower + avgMagic * 2
        targets.filter { it.isAlive }.forEach { enemy ->
            val dealt = enemy.takeDamage(baseDmg + rng.nextInt(10, 31))
            results += ActionResult("✨ COMBO ${combo.name} ! $dealt dégâts sur ${enemy.name} !",
                damageDealt = dealt)
        }
        return results
    }

    private fun executeItem(user: Hero, item: Item, target: Hero): ActionResult = when {
        item.hpRestore > 0 -> {
            val healed = target.heal(item.hpRestore)
            ActionResult("${user.name} utilise ${item.name} sur ${target.name}. +$healed HP.",
                healingDone = healed)
        }
        item.mpRestore > 0 -> {
            target.restoreMp(item.mpRestore)
            ActionResult("${user.name} utilise ${item.name} sur ${target.name}. +${item.mpRestore} MP.")
        }
        else -> ActionResult("${item.name} n'a aucun effet en combat.")
    }

    fun executeEnemyTurn(enemy: Enemy, state: CombatState): List<ActionResult> {
        val results    = mutableListOf<ActionResult>()
        val aliveParty = state.party.filter { it.isAlive }
        if (aliveParty.isEmpty()) return results

        // FIX PERF #6 — rng.pick() au lieu de .random()
        val target = rng.pick(aliveParty) ?: aliveParty.firstOrNull() ?: return results
        val skill  = enemy.selectAction(rng)    // rng injecté dans Enemy

        if (skill != null) {
            if (skill.hitAll) {
                aliveParty.forEach { hero ->
                    val dealt = hero.takeDamage(calcEnemyDamage(enemy, skill))
                    if (skill.statusEffect != StatusEffect.NONE) hero.statusEffect = skill.statusEffect
                    results += ActionResult(
                        "💀 ${enemy.name} utilise ${skill.name} sur ${hero.name} ! $dealt dégâts.",
                        damageDealt = dealt, statusApplied = skill.statusEffect)
                }
            } else {
                val dealt = target.takeDamage(calcEnemyDamage(enemy, skill))
                if (skill.statusEffect != StatusEffect.NONE) target.statusEffect = skill.statusEffect
                results += ActionResult(
                    "💀 ${enemy.name} utilise ${skill.name} sur ${target.name} ! $dealt dégâts.",
                    damageDealt = dealt, statusApplied = skill.statusEffect)
            }
        } else {
            val dealt = target.takeDamage(enemy.attack + rng.nextInt(3, 11))
            results += ActionResult(
                "⚔️ ${enemy.name} attaque ${target.name} pour $dealt dégâts.",
                damageDealt = dealt)
        }
        return results
    }

    private fun calcMagicDamage(hero: Hero, skill: Skill): Int {
        val base     = skill.basePower + hero.totalMagic() * 2
        val variance = (base * 0.1f).toInt().coerceAtLeast(1)
        return (base + rng.nextInt(-variance, variance + 1)).coerceAtLeast(1)
    }

    private fun calcEnemyDamage(enemy: Enemy, skill: EnemySkill): Int {
        val base     = skill.basePower + enemy.magic
        val variance = (base * 0.1f).toInt().coerceAtLeast(1)
        return (base + rng.nextInt(-variance, variance + 1)).coerceAtLeast(1)
    }

    fun checkCombatEnd(state: CombatState): CombatState {
        val allEnemiesDead = state.enemies.none { it.isAlive }
        val allHeroesDead  = state.party.none  { it.isAlive }
        return state.copy(
            isOver    = allEnemiesDead || allHeroesDead,
            playerWon = allEnemiesDead
        )
    }

    // FIX PERF #6 — rng.nextFloat() au lieu de Math.random()
    fun canFlee(party: List<Hero>, enemies: List<Enemy>): Boolean {
        if (enemies.any { it.isBoss }) return false
        val partyAgi  = party.filter  { it.isAlive }.map { it.agility }.average()
        val enemyAgi  = enemies.filter{ it.isAlive }.map { it.agility }.average()
        return rng.nextBool((partyAgi / (partyAgi + enemyAgi)).toFloat())
    }

    data class CombatRewards(val experience: Int, val gold: Int, val items: List<String>)

    fun calculateRewards(enemies: List<Enemy>): CombatRewards {
        val totalExp  = enemies.sumOf { it.expReward }
        val totalGold = enemies.sumOf { it.goldReward }
        // FIX PERF #6 — rng.nextBool au lieu de Math.random() < 0.35
        val drops = enemies.flatMap { e -> e.dropItems.filter { rng.nextBool(0.35f) } }
        return CombatRewards(totalExp, totalGold, drops)
    }

    fun applyEndOfTurnEffects(state: CombatState): List<String> {
        val messages = mutableListOf<String>()
        state.party.filter { it.isAlive }.forEach { hero ->
            when (hero.statusEffect) {
                StatusEffect.POISON -> {
                    val dmg = (hero.maxHp * 0.05f).toInt().coerceAtLeast(5)
                    hero.takeDamage(dmg)
                    messages += "☠️ ${hero.name} subit $dmg dégâts de poison."
                }
                StatusEffect.BURN -> {
                    val dmg = (hero.maxHp * 0.08f).toInt().coerceAtLeast(8)
                    hero.takeDamage(dmg)
                    messages += "🔥 ${hero.name} brûle pour $dmg dégâts."
                }
                else -> {}
            }
        }
        state.enemies.filter { it.isAlive }.forEach { enemy ->
            if (enemy.statusEffect == StatusEffect.POISON) {
                val dmg = (enemy.maxHp * 0.05f).toInt().coerceAtLeast(5)
                enemy.takeDamage(dmg)
                messages += "☠️ ${enemy.name} subit $dmg dégâts de poison."
            }
        }
        return messages
    }
}
