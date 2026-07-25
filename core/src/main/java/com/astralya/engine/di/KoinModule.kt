package com.astralya.engine.di

import com.astralya.engine.core.*
import com.astralya.audio.AudioManager
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.SaveManager
import com.astralya.game.save.repository.GameRepository
import com.astralya.game.world.MapRegistry
import com.astralya.game.world.DungeonRegistry
import com.astralya.game.quests.QuestRegistry
import com.astralya.game.combat.CombatSystem
import com.astralya.engine.utils.FontManager
import com.astralya.engine.utils.GameRandom
import com.astralya.engine.utils.WeatherSystem
import com.astralya.engine.utils.ScreenShakeManager
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Game
import org.koin.dsl.module

fun createAppModule(
    gameInstance: Game,
    repository: GameRepository,
    gameState: GameStateManager
) = module {
    single { gameInstance }
    single { repository }
    single { gameState }
    
    single { AssetManager() }
    single { ResourceManager(get()) }
    single { DataManager() }
    single { EventBus() }
    single { LocalizationManager() }
    single { UIManager() }
    single { ScreenManager(get()) }
    single { AudioManager() }
    single { FontManager() }
    single { MapRegistry() }
    single { DungeonRegistry() }
    single { QuestRegistry() }
    single { GameRandom() }
    single { WeatherSystem() }
    single { ScreenShakeManager() }
    single { SaveManager(get(), get()) }

    factory { CombatSystem(get(), get(), get()) }
}
