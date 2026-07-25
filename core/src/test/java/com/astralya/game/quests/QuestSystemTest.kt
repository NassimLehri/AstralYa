package com.astralya.game.quests

import com.astralya.engine.core.*
import com.astralya.game.entities.*
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.QuestStatus
import com.astralya.engine.utils.GameRandom
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class QuestSystemTest {

    private lateinit var state: GameStateManager
    private lateinit var questRegistry: QuestRegistry
    private lateinit var eventBus: EventBus
    private lateinit var dataManager: DataManager

    @Before
    fun setUp() {
        stopKoin()
        questRegistry = QuestRegistry()
        eventBus = EventBus()
        dataManager = DataManager()
        
        startKoin {
            modules(module {
                single { questRegistry }
                single { eventBus }
                single { dataManager }
            })
        }
        
        dataManager.forceLoad(
            skillsMap = mapOf(
                "coup_stellaire" to Skill("coup_stellaire", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "tempete_astrale" to Skill("tempete_astrale", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "protection_divine" to Skill("protection_divine", "", "", 0, SkillType.BUFF, Element.NEUTRAL, 0),
                "jugement_sept_cieux" to Skill("jugement_sept_cieux", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "aura_guerrier" to Skill("aura_guerrier", "", "", 0, SkillType.BUFF, Element.NEUTRAL, 0),
                "soin_astral" to Skill("soin_astral", "", "", 0, SkillType.HEAL, Element.NEUTRAL, 0),
                "bouclier_sacre" to Skill("bouclier_sacre", "", "", 0, SkillType.BUFF, Element.NEUTRAL, 0),
                "purification" to Skill("purification", "", "", 0, SkillType.HEAL, Element.NEUTRAL, 0),
                "renaissance_astrale" to Skill("renaissance_astrale", "", "", 0, SkillType.HEAL, Element.NEUTRAL, 0),
                "benediction_astrale" to Skill("benediction_astrale", "", "", 0, SkillType.BUFF, Element.NEUTRAL, 0),
                "eclat_stellaire" to Skill("eclat_stellaire", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "pluie_cometes" to Skill("pluie_cometes", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "nova_cosmique" to Skill("nova_cosmique", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0),
                "coeur_constellations" to Skill("coeur_constellations", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 0)
            ),
            itemsMap = mapOf(
                "epee_acier" to Item("epee_acier", "", ItemType.WEAPON, ""),
                "baton_bois" to Item("baton_bois", "", ItemType.WEAPON, ""),
                "armure_cuir" to Item("armure_cuir", "", ItemType.ARMOR, ""),
                "herbe_soin" to Item("herbe_soin", "", ItemType.CONSUMABLE, ""),
                "potion_mp" to Item("potion_mp", "", ItemType.CONSUMABLE, ""),
                "antidote" to Item("antidote", "", ItemType.CONSUMABLE, ""),
                "vieille_tablette" to Item("vieille_tablette", "", ItemType.KEY_ITEM, "")
            )
        )
        
        state = GameStateManager()
        state.eventBus = eventBus
        state.newGame()
    }

    @Test
    fun `quest requirement level check`() {
        // quete_secret_sables requires MinLevel(5)
        state.party.forEach { it.level = 1 }
        val started = state.startQuest("quete_secret_sables")
        assertFalse("Quest should not start at level 1", started)
        
        state.party.forEach { it.level = 5 }
        val started2 = state.startQuest("quete_secret_sables")
        assertTrue("Quest should start at level 5", started2)
    }

    @Test
    fun `auto progress via eventbus`() {
        state.party.forEach { it.level = 5 }
        state.startQuest("quete_secret_sables")
        val progress = state.questProgress["quete_secret_sables"]!!
        assertEquals("Should be at step 0", 0, progress.currentStep)
        
        eventBus.publish(ItemCollectedEvent("vieille_tablette", 1))
        assertEquals("Should be at step 1 now", 1, progress.currentStep)
    }

    @Test
    fun `reward distribution`() {
        val goldBefore = state.gold
        state.advanceQuest("quete_principale_1", GameRandom())
        
        assertEquals("Gold should be rewarded", goldBefore + 50, state.gold)
        assertEquals("Quest should be completed", QuestStatus.COMPLETED, state.questProgress["quete_principale_1"]?.status)
    }
}
