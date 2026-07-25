package com.astralya.game.world

import com.astralya.game.save.GameStateManager
import com.astralya.game.quests.QuestRegistry
import com.astralya.engine.core.EventBus
import com.astralya.engine.core.DataManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class DungeonTest {

    private lateinit var state: GameStateManager

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(module {
                single { QuestRegistry() }
                single { EventBus() }
                single { DataManager() }
            })
        }
        state = GameStateManager()
    }

    @Test
    fun `multi-switch door requires all switches`() {
        val door = LockedDoor("door1", Position(0f, 0f), requiredSwitchIds = listOf("sw1", "sw2"))
        
        // No switches active
        var isOpen = door.requiredSwitchIds.all { state.getSwitchState(it) }
        assertFalse("Door should be locked", isOpen)
        
        // One switch active
        state.setSwitchState("sw1", true)
        isOpen = door.requiredSwitchIds.all { state.getSwitchState(it) }
        assertFalse("Door should still be locked", isOpen)
        
        // All switches active
        state.setSwitchState("sw2", true)
        isOpen = door.requiredSwitchIds.all { state.getSwitchState(it) }
        assertTrue("Door should be open", isOpen)
    }

    @Test
    fun `fixed encounter state persistence`() {
        val encId = "boss1"
        assertFalse("Encounter not defeated yet", state.defeatedEncounters.contains(encId))
        
        state.defeatedEncounters.add(encId)
        assertTrue("Encounter should be marked defeated", state.defeatedEncounters.contains(encId))
    }
}
