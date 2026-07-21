package com.astralya.utils

/**
 * FIX PERF #6 / REVIEW #5 — Random seedable et injectable.
 *
 * Remplace Math.random() et .random() Kotlin dispersés dans le code.
 * - Reproductible : GameRandom(seed = 42L) rejoue exactement la même séquence
 * - Un seul générateur dans tout le jeu (pas de mélange Java/Kotlin)
 * - Testable unitairement : passer un seed fixe dans les tests
 */
class GameRandom(seed: Long = System.currentTimeMillis()) {

    private val rng = java.util.Random(seed)

    /** Float dans [0, 1) */
    fun nextFloat(): Float = rng.nextFloat()

    /** Int dans [from, until) */
    fun nextInt(from: Int, until: Int): Int {
        require(until > from) { "until ($until) doit être > from ($from)" }
        return from + rng.nextInt(until - from)
    }

    /** Int dans [range.first, range.last] */
    fun nextInt(range: IntRange): Int = nextInt(range.first, range.last + 1)

    /** Bool avec probabilité p ∈ [0,1] */
    fun nextBool(probability: Float): Boolean = rng.nextFloat() < probability

    /**
     * FIX #6 — Retourne null si la liste est vide au lieu de lancer
     * IllegalArgumentException (qui crasherait le thread GL sans message).
     * Les appelants gèrent le cas null explicitement.
     */
    fun <T> pick(list: List<T>): T? {
        if (list.isEmpty()) return null
        return list[nextInt(0, list.size)]
    }

    /** Shuffle in-place Fisher-Yates */
    fun <T> shuffle(list: MutableList<T>) {
        for (i in list.size - 1 downTo 1) {
            val j = nextInt(0, i + 1)
            val tmp = list[i]; list[i] = list[j]; list[j] = tmp
        }
    }
}
