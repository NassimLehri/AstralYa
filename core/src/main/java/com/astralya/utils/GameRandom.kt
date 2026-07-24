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

    /** Float dans [from, until) */
    fun nextFloat(from: Float, until: Float): Float {
        require(until > from) { "until ($until) doit être > from ($from)" }
        return from + rng.nextFloat() * (until - from)
    }

    /** Int dans [from, until) */
    fun nextInt(from: Int, until: Int): Int {
        require(until > from) { "until ($until) doit être > from ($from)" }
        return from + rng.nextInt(until - from)
    }

    /** Int dans [range.first, range.last] */
    fun nextInt(range: IntRange): Int {
        val first = range.first
        val last = range.last
        require(last >= first) { "range.last ($last) doit être >= range.first ($first)" }

        val bound = last.toLong() - first.toLong() + 1
        return if (bound > 0 && bound <= Int.MAX_VALUE) {
            first + rng.nextInt(bound.toInt())
        } else {
            // Cas où bound > Int.MAX_VALUE (ex: Int.MIN_VALUE..Int.MAX_VALUE)
            (first + (rng.nextDouble() * bound).toLong()).toInt()
        }
    }

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

}
