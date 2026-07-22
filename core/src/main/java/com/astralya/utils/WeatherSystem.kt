package com.astralya.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

enum class WeatherType { CLEAR, RAIN, SNOW, STORM }

class WeatherSystem {

    var currentType = WeatherType.CLEAR
        private set

    private var timer = 0f
    private var nextTransition = 120f // Secondes avant prochain changement

    private val tintRain  = Color(0.7f, 0.7f, 0.8f, 1f)
    private val tintSnow  = Color(0.9f, 0.9f, 1.0f, 1f)
    private val tintStorm = Color(0.5f, 0.5f, 0.6f, 1f)
    private val tintClear = Color(1.0f, 1.0f, 1.0f, 1f)

    fun update(delta: Float, rng: GameRandom) {
        timer += delta
        if (timer >= nextTransition) {
            timer = 0f
            transition(rng)
        }
    }

    private fun transition(rng: GameRandom) {
        val roll = rng.nextInt(0, 100)
        currentType = when {
            roll < 60 -> WeatherType.CLEAR
            roll < 80 -> WeatherType.RAIN
            roll < 95 -> WeatherType.SNOW
            else      -> WeatherType.STORM
        }
        nextTransition = 60f + rng.nextFloat() * 180f // Dure entre 1 et 4 minutes
    }

    fun getWeatherTint(): Color {
        return when (currentType) {
            WeatherType.RAIN  -> tintRain
            WeatherType.SNOW  -> tintSnow
            WeatherType.STORM -> tintStorm
            WeatherType.CLEAR -> tintClear
        }
    }

    fun getParticleName(): String? {
        return when (currentType) {
            WeatherType.RAIN, WeatherType.STORM -> "rain"
            WeatherType.SNOW -> "snow"
            else -> null
        }
    }
    
    fun setWeather(type: WeatherType) {
        currentType = type
        timer = 0f
    }
}
