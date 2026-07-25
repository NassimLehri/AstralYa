package com.astralya.game.save

import com.badlogic.gdx.utils.Json
import com.astralya.game.save.models.SaveData

/**
 * Epic 5 — Gère l'évolution du schéma de données des sauvegardes.
 */
object SaveMigrationManager {

    fun migrate(jsonBlob: String, fromVersion: Int, targetVersion: Int): String {
        var currentJson = jsonBlob
        var currentVer = fromVersion

        while (currentVer < targetVersion) {
            currentJson = when (currentVer) {
                1 -> migrateV1toV2(currentJson)
                else -> currentJson
            }
            currentVer++
        }
        return currentJson
    }

    private fun migrateV1toV2(json: String): String {
        // Exemple: Ajouter un champ par défaut s'il n'existait pas
        // Pour l'instant on retourne tel quel
        return json
    }
}
