package com.astralya.entities

import com.astralya.utils.GameRandom

enum class HeroId   { NASSIM, YASMINE, LWIZ }
enum class HeroRole { TANK_DPS, SUPPORT_HEAL, MAGE }
enum class Element  { STELLAR, LIGHT, COSMIC, DARK, NEUTRAL }
enum class SkillType { ATTACK, HEAL, BUFF, DEBUFF, COMBO }
enum class StatusEffect { NONE, POISON, BURN, FREEZE, STUN, BLESSED, SHIELDED }

data class Skill(
    val id: String, val name: String, val description: String,
    val mpCost: Int, val type: SkillType, val element: Element,
    val basePower: Int, val hitAll: Boolean = false,
    val healAmount: Int = 0,
    val statusEffect: StatusEffect = StatusEffect.NONE,
    val animationId: String = ""
)

data class ComboSkill(
    val id: String, val name: String, val description: String,
    val requiredHeroes: List<HeroId>, val mpCostPerHero: Int,
    val basePower: Int, val element: Element,
    val hitAll: Boolean = true, val animationId: String = ""
)

class Hero(
    val id: HeroId, val name: String, val role: HeroRole,
    val skills: List<Skill>,
    baseMaxHp: Int, baseMaxMp: Int,
    baseAttack: Int, baseDefense: Int,
    baseAgility: Int, baseMagic: Int
) {
    var level: Int = 1
    var experience: Int = 0
    var maxHp: Int = baseMaxHp;   var currentHp: Int = baseMaxHp
    var maxMp: Int = baseMaxMp;   var currentMp: Int = baseMaxMp
    var attack: Int = baseAttack;  var defense: Int = baseDefense
    var agility: Int = baseAgility; var magic: Int = baseMagic
    var statusEffect: StatusEffect = StatusEffect.NONE
    var isAlive: Boolean = true
    var weapon: Item? = null;  var armor: Item? = null;  var accessory: Item? = null

    val expToNextLevel: Int get() = (level * level * 50) + (level * 100)

    /**
     * FIX PERF #6 — GameRandom injecté, plus de .random() Kotlin non seedable.
     * Retourne true si un level-up s'est produit.
     */
    fun gainExp(amount: Int, rng: GameRandom): Boolean {
        experience += amount
        if (experience >= expToNextLevel) {
            experience -= expToNextLevel
            levelUp(rng)
            return true
        }
        return false
    }

    private fun levelUp(rng: GameRandom) {
        level++
        val hpGain = (maxHp * 0.08).toInt().coerceAtLeast(10)
        val mpGain = (maxMp * 0.06).toInt().coerceAtLeast(5)
        maxHp += hpGain;  currentHp = maxHp
        maxMp += mpGain;  currentMp = maxMp
        // FIX PERF #6 — rng injecté au lieu de (2..4).random()
        attack  += rng.nextInt(2, 5)
        defense += rng.nextInt(1, 4)
        agility += rng.nextInt(1, 4)
        magic   += rng.nextInt(2, 5)
    }

    fun takeDamage(amount: Int): Int {
        val reduced = (amount - defense / 2).coerceAtLeast(1)
        currentHp = (currentHp - reduced).coerceAtLeast(0)
        if (currentHp == 0) isAlive = false
        return reduced
    }

    fun heal(amount: Int): Int {
        val healed = amount.coerceAtMost(maxHp - currentHp)
        currentHp += healed
        return healed
    }

    fun restoreMp(amount: Int) { currentMp = (currentMp + amount).coerceAtMost(maxMp) }

    fun useMp(amount: Int): Boolean {
        if (currentMp < amount) return false
        currentMp -= amount; return true
    }

    fun revive(hpPercent: Float = 0.5f) {
        currentHp = (maxHp * hpPercent).toInt()
        isAlive = true; statusEffect = StatusEffect.NONE
    }

    fun totalAttack()  = attack  + (weapon?.attackBonus  ?: 0)
    fun totalDefense() = defense + (armor?.defenseBonus   ?: 0)
    fun totalMagic()   = magic   + (weapon?.magicBonus    ?: 0) + (accessory?.magicBonus ?: 0)
}

data class Item(
    val id: String, val name: String, val type: ItemType,
    val description: String,
    val attackBonus: Int = 0, val defenseBonus: Int = 0,
    val magicBonus: Int = 0,  val hpRestore: Int = 0,
    val mpRestore: Int = 0,   val value: Int = 0,
    val equipableBy: List<HeroId> = emptyList()
)

enum class ItemType { WEAPON, ARMOR, ACCESSORY, CONSUMABLE, KEY_ITEM }

data class EnemySkill(
    val name: String, val basePower: Int, val mpCost: Int = 0,
    val hitAll: Boolean = false,
    val statusEffect: StatusEffect = StatusEffect.NONE
)

class Enemy(
    val id: String, val name: String, val element: Element,
    maxHp: Int, val attack: Int, val defense: Int,
    val agility: Int, val magic: Int,
    val skills: List<EnemySkill> = emptyList(),
    val expReward: Int, val goldReward: Int,
    val dropItems: List<String> = emptyList(),
    val isBoss: Boolean = false
) {
    var currentHp: Int = maxHp
    val maxHp: Int = maxHp
    var statusEffect: StatusEffect = StatusEffect.NONE
    val isAlive: Boolean get() = currentHp > 0

    fun takeDamage(amount: Int): Int {
        val reduced = (amount - defense / 2).coerceAtLeast(1)
        currentHp = (currentHp - reduced).coerceAtLeast(0)
        return reduced
    }

    // FIX PERF #6 — rng injecté
    fun selectAction(rng: GameRandom): EnemySkill? {
        if (skills.isEmpty()) return null
        return if (rng.nextBool(0.4f)) rng.pick(skills) else null  // pick() retourne T?
    }
}
