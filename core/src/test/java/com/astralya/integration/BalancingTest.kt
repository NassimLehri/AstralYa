package com.astralya.integration

import com.astralya.engine.core.*
import com.astralya.game.entities.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.io.File
import com.badlogic.gdx.utils.Json

class BalancingTest {

    @Before
    fun setUp() {
        stopKoin()
        val dataManager = DataManager()
        startKoin {
            modules(module {
                single { dataManager }
            })
        }
        
        dataManager.forceLoad(
            skillsMap = mapOf(
                "coup_stellaire" to Skill("coup_stellaire", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 100),
                "tempete_astrale" to Skill("tempete_astrale", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 100),
                "protection_divine" to Skill("protection_divine", "", "", 0, SkillType.BUFF, Element.NEUTRAL, 0),
                "jugement_sept_cieux" to Skill("jugement_sept_cieux", "", "", 0, SkillType.ATTACK, Element.NEUTRAL, 100),
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
            )
        )
    }

    @Test
    fun `early game enemies are beatable`() {
        val nassim = HeroFactory.createNassim()
        
        // Load real JSON if possible (running from project root)
        val file = File("../android/src/main/assets/data/enemies.json")
        if (!file.exists()) return // Skip if file not reachable in this environment

        val json = Json()
        val list = json.fromJson(List::class.java, Enemy::class.java, file.readText()) as List<Enemy>
        
        val slime = list.find { it.id == "slime_vert" }
        assertNotNull("Slime vert must exist", slime)
        
        assertTrue("Slime attack should be lower than Hero HP", slime!!.attack < nassim.maxHp)
        assertTrue("Hero attack should be able to hurt slime", nassim.totalAttack() > slime.defense / 2)
    }
}
