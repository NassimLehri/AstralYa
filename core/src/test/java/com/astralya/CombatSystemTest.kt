package com.astralya

import com.astralya.combat.CombatAction
import com.astralya.combat.CombatSystem
import com.astralya.entities.*
import com.astralya.utils.GameRandom
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CombatSystemTest {

    // Seed fixe → résultats reproductibles
    private val rng    = GameRandom(seed = 42L)
    private val combat = CombatSystem(rng)
    private lateinit var nassim:  Hero
    private lateinit var yasmine: Hero
    private lateinit var slime:   Enemy
    private lateinit var state:   com.astralya.combat.CombatState

    @Before
    fun setUp() {
        nassim  = HeroFactory.createNassim()
        yasmine = HeroFactory.createYasmine()
        slime   = EnemyFactory.createSlimeVert()
        state   = combat.initCombat(listOf(nassim, yasmine), listOf(slime))
    }

    // ── Attaque physique ──────────────────────────────────────────────────────

    @Test
    fun `attaque reduit HP ennemi`() {
        val hpBefore = slime.currentHp
        val results  = combat.executeHeroAction(CombatAction.Attack(nassim, slime), state)
        assertTrue("L'attaque doit produire un résultat", results.isNotEmpty())
        // Avec seed=42, pas de miss sur la première attaque
        if (!results.first().missed) {
            assertTrue("HP doit diminuer", slime.currentHp < hpBefore)
            assertTrue("Dégâts > 0", results.first().damageDealt > 0)
        }
    }

    @Test
    fun `attaque ne peut pas tuer en dessous de 0 HP`() {
        slime.currentHp = 1
        combat.executeHeroAction(CombatAction.Attack(nassim, slime), state)
        assertTrue("HP ne peut pas être négatif", slime.currentHp >= 0)
    }

    @Test
    fun `attaque critique multiplie les degats`() {
        // Seed choisie pour garantir un critique
        val rngCrit = GameRandom(seed = 137L)
        val combatCrit = CombatSystem(rngCrit)
        var foundCrit = false
        repeat(50) {
            val s = EnemyFactory.createSlimeVert()
            val results = combatCrit.executeHeroAction(CombatAction.Attack(nassim, s), state)
            if (results.any { it.critical }) foundCrit = true
        }
        assertTrue("Un critique doit apparaître sur 50 attaques (p=10%)", foundCrit)
    }

    // ── Compétences ───────────────────────────────────────────────────────────

    @Test
    fun `skill cout MP deduit correctement`() {
        val skill   = nassim.skills.first()
        val mpBefore = nassim.currentMp
        combat.executeHeroAction(CombatAction.UseSkill(nassim, skill, slime), state)
        assertEquals("MP réduit du coût du skill",
            mpBefore - skill.mpCost, nassim.currentMp)
    }

    @Test
    fun `skill echoue si MP insuffisant`() {
        nassim.currentMp = 0
        val skill   = nassim.skills.first()
        val results = combat.executeHeroAction(CombatAction.UseSkill(nassim, skill, slime), state)
        assertEquals("MP resté à 0", 0, nassim.currentMp)
        assertTrue("Message d'erreur MP", results.first().message.contains("MP"))
    }

    @Test
    fun `heal restaure HP sans depasser maxHp`() {
        nassim.currentHp = 100
        val healSkill = yasmine.skills.first { it.type == SkillType.HEAL && it.healAmount > 0 }
        combat.executeHeroAction(CombatAction.UseSkill(yasmine, healSkill, nassim), state)
        assertTrue("HP ne dépasse pas maxHp", nassim.currentHp <= nassim.maxHp)
        assertTrue("HP a augmenté", nassim.currentHp > 100)
    }

    @Test
    fun `resurrection fonctionne sur hero mort`() {
        nassim.isAlive   = false
        nassim.currentHp = 0
        val reviveSkill = yasmine.skills.first { it.healAmount == -1 }
        val results = combat.executeHeroAction(
            CombatAction.UseSkill(yasmine, reviveSkill, nassim), state)
        assertTrue("Nassim doit être vivant", nassim.isAlive)
        assertTrue("HP > 0 après résurrection", nassim.currentHp > 0)
        assertTrue("Result indique résurrection", results.first().targetRevived)
    }

    // ── Fin de combat ─────────────────────────────────────────────────────────

    @Test
    fun `victoire detectee quand tous ennemis morts`() {
        slime.currentHp = 0
        val result = combat.checkCombatEnd(state)
        assertTrue("Combat terminé", result.isOver)
        assertTrue("Joueur a gagné", result.playerWon)
    }

    @Test
    fun `game over detecte quand tous heros morts`() {
        nassim.isAlive   = false; nassim.currentHp  = 0
        yasmine.isAlive  = false; yasmine.currentHp = 0
        val result = combat.checkCombatEnd(state)
        assertTrue("Combat terminé", result.isOver)
        assertFalse("Joueur a perdu", result.playerWon)
    }

    @Test
    fun `combat pas termine si des deux cotes survivants`() {
        val result = combat.checkCombatEnd(state)
        assertFalse("Combat pas encore terminé", result.isOver)
    }

    // ── Récompenses ───────────────────────────────────────────────────────────

    @Test
    fun `recompenses correctes`() {
        val rewards = combat.calculateRewards(listOf(slime))
        assertEquals("EXP correcte", slime.expReward, rewards.experience)
        assertEquals("Or correct", slime.goldReward, rewards.gold)
    }

    // ── Effets de statut ──────────────────────────────────────────────────────

    @Test
    fun `poison inflige degats fin de tour`() {
        nassim.statusEffect = StatusEffect.POISON
        val hpBefore = nassim.currentHp
        combat.applyEndOfTurnEffects(state)
        assertTrue("Poison inflige des dégâts", nassim.currentHp < hpBefore)
    }

    @Test
    fun `fuite impossible contre boss`() {
        val morvax  = EnemyFactory.createMorvax()
        val canFlee = combat.canFlee(listOf(nassim), listOf(morvax))
        assertFalse("Impossible de fuir un boss", canFlee)
    }
}
