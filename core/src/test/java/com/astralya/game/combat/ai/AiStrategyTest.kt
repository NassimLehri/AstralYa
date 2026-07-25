package com.astralya.game.combat.ai

import com.astralya.game.combat.*
import com.astralya.game.entities.*
import com.astralya.engine.utils.GameRandom
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AiStrategyTest {

    private val rng = GameRandom(seed = 1L)
    private lateinit var state: CombatState
    private lateinit var supportEnemy: Enemy

    @Before
    fun setUp() {
        val nassim = Hero(HeroId.NASSIM, "Nassim", HeroRole.TANK_DPS, emptyList(), 100, 100, 10, 10, 10, 10)
        supportEnemy = Enemy("healer", "Healer", Element.NEUTRAL, 100, 10, 10, 10, 10,
            skills = listOf(EnemySkill("Heal", 20, isHeal = true), EnemySkill("Attack", 10)),
            expReward = 0, goldReward = 0, aiType = "SUPPORT")
        
        state = CombatState(listOf(nassim), listOf(supportEnemy))
    }

    @Test
    fun `SupportAi soigne quand blesse`() {
        supportEnemy.currentHp = 50
        val strategy = SupportAi()
        val action = strategy.selectAction(supportEnemy, state, rng)
        assertEquals("Doit choisir le soin", "Heal", action?.name)
    }

    @Test
    fun `AggressiveAi attaque toujours`() {
        val strategy = AggressiveAi()
        val action = strategy.selectAction(supportEnemy, state, rng)
        assertNotNull("Doit choisir une action", action)
        assertFalse("Ne doit pas soigner", action!!.isHeal)
    }
}
