package com.astralya

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.astralya.game.save.GameStateManager
import com.astralya.game.save.repository.GameRepository
import com.astralya.ui.screens.LoadingScreen
import com.astralya.engine.core.*
import com.astralya.engine.di.createAppModule
import com.astralya.engine.utils.FontManager
import com.astralya.engine.utils.GameRandom
import com.astralya.game.combat.CombatSystem
import com.astralya.game.world.MapRegistry
import com.astralya.game.world.DungeonRegistry
import com.astralya.game.quests.QuestRegistry
import com.astralya.game.save.SaveManager
import com.astralya.engine.utils.*
import org.koin.core.context.startKoin
import org.koin.core.component.inject
import org.koin.core.component.get

open class AstralYaGame(
    val repository: GameRepository,
    val gameStateManager: GameStateManager
) : AstralEngine() {

    val dataManager: DataManager by inject()
    val eventBus: EventBus by inject()
    val localization: LocalizationManager by inject()
    val fonts: FontManager by inject()
    val mapRegistry: MapRegistry by inject()
    val dungeonRegistry: DungeonRegistry by inject()
    val questRegistry: QuestRegistry by inject()
    val saveManager: SaveManager by inject()
    val weatherSystem: WeatherSystem by inject()
    val shakeManager: ScreenShakeManager by inject()

    fun getCombatSystem(): CombatSystem = get<CombatSystem>()

    val random: GameRandom = GameRandom()
    var assetsLoaded = false

    override fun create() {
        // Initialisation de Koin avant tout le reste
        startKoin {
            modules(createAppModule(this@AstralYaGame, repository, gameStateManager))
        }
        
        // Setup EventBus
        gameStateManager.eventBus = eventBus
        
        // LibGDX infrastructure via base class
        super.create()
    }

    override fun onEngineInit() {
        Gdx.graphics.isContinuousRendering = true
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        
        // Data and initial zone loading
        dataManager.loadAll()
        resourceManager.loadAll()
        resourceManager.loadZone(GameStateManager.STARTING_MAP_ID, mapRegistry)
        
        screenManager.setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        fonts.dispose()
        super.dispose()
    }
}
