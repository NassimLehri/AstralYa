package com.astralya

import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.astralya.data.AstralYaDatabase
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.repository.GameRepository

/**
 * AndroidLauncher gère le cycle de vie Android et l'injection des dépendances
 * globales (Database, Repository, GameState).
 */
class AndroidLauncher : AndroidApplication() {

    private var database: AstralYaDatabase? = null
    private var repository: GameRepository? = null
    private lateinit var gameState: GameStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        Log.d("AstralYa", "Initializing GameState...")
        gameState = GameStateManager()

        try {
            Log.d("AstralYa", "Initializing Database...")
            val db = AstralYaDatabase.getDatabase(this)
            database = db
            repository = GameRepository(
                db.heroDao(),
                db.inventoryDao(),
                db.questDao(),
                db.saveDao()
            )
            Log.d("AstralYa", "Database and Repository initialized successfully")
        } catch (t: Throwable) {
            Log.e("AstralYa", "CRITICAL ERROR during DB initialization", t)
            // We throw the exception to see the REAL cause in Logcat if it crashes
            throw RuntimeException("Database initialization failed", t)
        }

        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode   = true
            useWakelock        = true
            numSamples         = 0
            useAccelerometer   = false
            useCompass         = false
            useGyroscope       = false
        }

        val repo = repository
        if (repo != null) {
            Log.d("AstralYa", "Launching AstralYaGame...")
            initialize(AstralYaGame(repo, gameState), config)
        } else {
            Log.e("AstralYa", "Repository is NULL. Fatal error.")
            throw IllegalStateException("Impossible de démarrer : la base de données n'est pas accessible.")
        }
    }
}
