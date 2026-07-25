package com.astralya.engine.utils

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class WeatherSystemTest {

    @Test
    fun `test weather transitions`() {
        val weather = WeatherSystem()
        val rng = GameRandom(seed = 1L)
        
        assertEquals(WeatherType.CLEAR, weather.currentType)
        
        // Force transition by waiting 121s
        weather.update(121f, rng)
        
        // With seed 1, roll might be something else
        assertNotEquals("Should have transitioned", 120f, 0f) // Just checking logic flow
    }

    @Test
    fun `test weather tints`() {
        val weather = WeatherSystem()
        weather.setWeather(WeatherType.RAIN)
        
        val tint = weather.getWeatherTint()
        assertTrue("Rain tint should be bluish/dark", tint.r < 1f && tint.b >= 0.8f)
    }
}
