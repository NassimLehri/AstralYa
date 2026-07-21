package com.astralya

import com.astralya.utils.GameRandom
import org.junit.Assert.*
import org.junit.Test

class GameRandomTest {

    @Test
    fun `meme seed produit meme sequence`() {
        val rng1 = GameRandom(seed = 42L)
        val rng2 = GameRandom(seed = 42L)
        val seq1 = List(20) { rng1.nextInt(0, 100) }
        val seq2 = List(20) { rng2.nextInt(0, 100) }
        assertEquals("Séquences identiques pour même seed", seq1, seq2)
    }

    @Test
    fun `seeds differents produisent sequences differentes`() {
        val rng1 = GameRandom(seed = 1L)
        val rng2 = GameRandom(seed = 2L)
        val seq1 = List(10) { rng1.nextInt(0, 1000) }
        val seq2 = List(10) { rng2.nextInt(0, 1000) }
        assertNotEquals("Seeds différents → séquences différentes", seq1, seq2)
    }

    @Test
    fun `nextInt respecte les bornes`() {
        val rng = GameRandom(seed = 99L)
        repeat(1000) {
            val v = rng.nextInt(5, 10)
            assertTrue("$v doit être dans [5,10)", v in 5 until 10)
        }
    }

    @Test
    fun `nextBool respecte la probabilite`() {
        val rng = GameRandom(seed = 7L)
        val trues = (1..10000).count { rng.nextBool(0.3f) }
        // 30% ± 3% (marge statistique raisonnable)
        assertTrue("Probabilité 30% : attendu ~3000, obtenu $trues",
            trues in 2700..3300)
    }

    @Test
    fun `pick retourne null sur liste vide`() {
        val rng = GameRandom(seed = 1L)
        val result = rng.pick(emptyList<String>())
        assertNull("pick() sur liste vide doit retourner null", result)
    }

    @Test
    fun `pick retourne element de la liste`() {
        val rng = GameRandom(seed = 1L)
        val list = listOf("a", "b", "c")
        repeat(100) {
            val v = rng.pick(list)
            assertTrue("pick() doit retourner un élément de la liste", v in list)
        }
    }

    @Test
    fun `nextInt range`() {
        val rng = GameRandom(seed = 42L)
        repeat(500) {
            val v = rng.nextInt(1..6)
            assertTrue("Dé 6 faces : $v", v in 1..6)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `nextInt until inferieur a from leve exception`() {
        GameRandom().nextInt(10, 5)
    }
}
