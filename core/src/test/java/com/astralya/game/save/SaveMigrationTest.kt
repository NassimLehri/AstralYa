package com.astralya.game.save

import com.astralya.game.save.models.SaveData
import com.badlogic.gdx.utils.Json
import org.junit.Assert.*
import org.junit.Test

class SaveMigrationTest {

    private val json = Json()

    @Test
    fun `test migration V1 to V2`() {
        val v1Json = """{"version": 1, "gold": 100}"""
        
        // Simuler une migration
        val migratedJson = SaveMigrationManager.migrate(v1Json, 1, 2)
        
        // Pour l'instant V1toV2 ne change rien dans le code, mais on vérifie que le flux marche
        assertTrue("Le JSON doit être valide", migratedJson.contains("version"))
    }

    @Test
    fun `serialization test`() {
        val data = SaveData(version = 1, gold = 500, reputation = 10)
        val blob = json.toJson(data)
        
        val decoded = json.fromJson(SaveData::class.java, blob)
        assertEquals(500, decoded.gold)
        assertEquals(10, decoded.reputation)
    }
}
