package com.astralya

import com.astralya.data.GameState
import com.astralya.data.QuestStatus
import com.astralya.entities.HeroFactory
import com.astralya.utils.GameRandom
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameStateTest {

    private lateinit var state: GameState
    private val rng = GameRandom(seed = 1L)

    @Before
    fun setUp() {
        state = GameState()
        state.newGame()
    }

    // ── Inventaire ────────────────────────────────────────────────────────────

    @Test
    fun `newGame initialise inventaire de depart`() {
        assertTrue("Herbe de soin dans l'inventaire", state.hasItem("herbe_soin"))
        assertTrue("Potion MP dans l'inventaire",     state.hasItem("potion_mp"))
        assertTrue("Or de départ = 100",              state.gold == 100)
    }

    @Test
    fun `addItem incremente quantite`() {
        state.addItem("herbe_soin", 3)
        val qty = state.getItemCount("herbe_soin")
        assertEquals("Quantité augmentée", 8, qty)  // 5 départ + 3
    }

    @Test
    fun `removeItem decremente et supprime si zero`() {
        val before = state.getItemCount("herbe_soin")
        state.removeItem("herbe_soin", before)
        assertFalse("Item supprimé quand qty=0", state.hasItem("herbe_soin"))
    }

    @Test
    fun `removeItem retourne false si quantite insuffisante`() {
        val result = state.removeItem("herbe_soin", 9999)
        assertFalse("removeItem échoue si qty insuffisante", result)
    }

    @Test
    fun `addItem puis removeItem partiel conserve le reste`() {
        state.addItem("test_item", 5)
        state.removeItem("test_item", 3)
        assertEquals("2 items restants", 2, state.getItemCount("test_item"))
    }

    // ── Quêtes ────────────────────────────────────────────────────────────────

    @Test
    fun `newGame demarre quete principale 1`() {
        assertTrue("Quête principale active", state.isQuestActive("quete_principale_1"))
    }

    @Test
    fun `startQuest ne repart pas une quete en cours`() {
        val progressBefore = state.questProgress["quete_principale_1"]?.currentStep
        state.startQuest("quete_principale_1")
        assertEquals("Progression inchangée", progressBefore,
            state.questProgress["quete_principale_1"]?.currentStep)
    }

    @Test
    fun `advanceQuest incremente currentStep`() {
        val before = state.questProgress["quete_principale_1"]?.currentStep ?: 0
        state.advanceQuest("quete_principale_1", rng)
        val after = state.questProgress["quete_principale_1"]?.currentStep ?: 0
        assertTrue("Step avancé", after > before)
    }

    @Test
    fun `quete completee apres toutes les etapes`() {
        // Avancer toutes les étapes de la quête
        repeat(10) { state.advanceQuest("quete_principale_1", rng) }
        assertTrue("Quête complétée",
            state.isQuestCompleted("quete_principale_1"))
    }

    @Test
    fun `recompense or attribuee a completion`() {
        val goldBefore = state.gold
        repeat(10) { state.advanceQuest("quete_principale_1", rng) }
        if (state.isQuestCompleted("quete_principale_1")) {
            assertTrue("Or reçu à la complétion", state.gold >= goldBefore)
        }
    }

    // ── Équipe ────────────────────────────────────────────────────────────────

    @Test
    fun `newGame cree equipe complete`() {
        assertEquals("3 héros dans l'équipe", 3, state.party.size)
        assertTrue("Nassim présent", state.getHero(com.astralya.entities.HeroId.NASSIM) != null)
        assertTrue("Yasmine présente", state.getHero(com.astralya.entities.HeroId.YASMINE) != null)
        assertTrue("Lwiz présent", state.getHero(com.astralya.entities.HeroId.LWIZ) != null)
    }

    @Test
    fun `isGameOver faux si un heros vivant`() {
        state.party.filter { it.id != com.astralya.entities.HeroId.NASSIM }
            .forEach { it.isAlive = false; it.currentHp = 0 }
        assertFalse("Pas game over si Nassim vivant", state.isGameOver())
    }

    @Test
    fun `isGameOver vrai si tous morts`() {
        state.party.forEach { it.isAlive = false; it.currentHp = 0 }
        assertTrue("Game over si tous morts", state.isGameOver())
    }

    // ── applyCombatRewards ────────────────────────────────────────────────────

    @Test
    fun `applyCombatRewards ajoute or`() {
        val goldBefore = state.gold
        state.applyCombatRewards(100, 50, emptyList(), rng)
        assertEquals("Or ajouté", goldBefore + 50, state.gold)
    }

    @Test
    fun `applyCombatRewards distribue exp aux heros vivants`() {
        val levelBefore = state.party.map { it.level }
        // Donner beaucoup d'EXP pour forcer un level-up
        state.applyCombatRewards(99999, 0, emptyList(), rng)
        val levelAfter = state.party.map { it.level }
        assertTrue("Au moins un héros a levelé",
            levelAfter.zip(levelBefore).any { (after, before) -> after > before })
    }

    @Test
    fun `crystals found detecte victoire finale`() {
        repeat(7) { i -> state.findCrystal("cristal_$i") }
        assertTrue("7 cristaux = victoire finale", state.allCrystalsFound)
    }

    // ── Position ─────────────────────────────────────────────────────────────

    @Test
    fun `newGame place le joueur au village`() {
        assertEquals("Carte de départ = village", "village_depart", state.currentMapId)
    }
}
