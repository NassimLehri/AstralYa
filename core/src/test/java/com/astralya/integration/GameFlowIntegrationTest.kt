package com.astralya.integration

import com.astralya.engine.core.*
import com.astralya.engine.utils.GameRandom
import com.astralya.game.combat.CombatSystem
import com.astralya.game.entities.*
import com.astralya.game.quests.QuestRegistry
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.QuestStatus
import com.astralya.game.world.MapRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class GameFlowIntegrationTest {

    private lateinit var state: GameStateManager
    private lateinit var eventBus: EventBus
    private lateinit var dataManager: DataManager
    private lateinit var questRegistry: QuestRegistry
    private lateinit var mapRegistry: MapRegistry
    private lateinit var combatSystem: CombatSystem

    @Before
    fun setUp() {
        stopKoin()
        eventBus = EventBus()
        dataManager = DataManager()
        questRegistry = QuestRegistry()
        mapRegistry = MapRegistry()
        combatSystem = CombatSystem(GameRandom(1L), eventBus, com.astralya.engine.utils.WeatherSystem())

        startKoin {
            modules(module {
                single { eventBus }
                single { dataManager }
                single { questRegistry }
                single { mapRegistry }
            })
        }

        dataManager.forceLoad(
            skillsMap = mapOf(
                "coup_stellaire" to Skill("coup_stellaire", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 100)
            ),
            enemiesMap = mapOf(
                "slime_vert" to Enemy("slime_vert", "Slime", Element.NEUTRAL, 50, 10, 5, 5, 5, expReward = 100, goldReward = 50)
            )
        )

        state = GameStateManager()
        state.eventBus = eventBus
        state.newGame()
    }

    @Test
    fun `full gameplay loop - quest start to combat victory`() {
        // 1. Initial State
        assertTrue("Quest quete_principale_1 should be started", state.isQuestActive("quete_principale_1"))
        val goldBefore = state.gold

        // 2. Simulate combat victory (triggering via EventBus)
        eventBus.publish(EnemyDefeatedEvent("slime_vert"))
        
        // 3. Advance quest manually (mimicking NPC interaction)
        state.advanceQuest("quete_principale_1", GameRandom(1L))
        
        // 4. Verify results
        assertEquals("Gold should have increased", goldBefore + 50, state.gold)
        assertTrue("Quest should be completed", state.isQuestCompleted("quete_principale_1"))
    }
}
