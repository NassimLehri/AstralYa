package com.astralya.game.quests

import com.astralya.engine.core.DataManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Epic 17 — QuestRegistry refactorisé pour être Data-Driven via DataManager.
 */
class QuestRegistry : KoinComponent {

    private val dataManager: DataManager by inject()

    fun getQuest(id: String): Quest? = dataManager.getQuest(id)
    
    fun getAllQuests(): Collection<Quest> = dataManager.getAllQuests()
}
