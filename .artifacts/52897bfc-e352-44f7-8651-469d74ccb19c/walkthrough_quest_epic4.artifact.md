# Walkthrough - Epic 4: Advanced Quest System

I have significantly expanded the narrative engine of AstralYa, introducing multi-step quests, branching dialogues, and an automated progression system driven by game events.

## Key Narrative Enhancements

### 📜 1. Sophisticated Quest Engine
- **Requirement Logic**: Quests can now have specific entry conditions.
    - *Example*: "Le Secret des Sables" now requires the player to be at least **Level 5** to start.
- **Multiple Rewards**: Refactored the reward system to support lists of outcomes including **Gold**, **EXP**, **Items**, and the new **Reputation** stat.
- **Quest Registry**: Centralized all quest data, allowing for complex multi-step journeys (e.g., Gather item -> Enter area -> Defeat boss).

### 💬 2. Branching NPC Dialogues
- **Dynamic Responses**: NPCs no longer repeat the same lines. Their dialogue now branches based on the status of specific quests.
    - **NOT_STARTED**: The NPC gives a quest hook or general greeting.
    - **IN_PROGRESS**: The NPC gives a reminder or guidance.
    - **COMPLETED**: The NPC offers thanks or new lore.
- **Village Overhaul**: Updated the **Ancien Lyros** with a complete narrative arc for the first main quest.

### 📡 3. Automated Progression (EventBus)
- **Zero-Manual-Checks**: The quest system now listens to the `EventBus` for `EnemyDefeatedEvent` and `ItemCollectedEvent`.
- **Auto-Advance**: If a quest step requires killing a "Golem de Cristal", the quest will automatically advance the moment the battle ends, providing a seamless player experience.

## Verification Results

### Automated Tests
- Created `QuestSystemTest.kt` to verify level requirements and auto-progression logic.
- **Status**: SUCCESS (All core narrative logic verified).

### Manual Verification Path
1. **Initial Talk**: Speak with Lyros at the start. He will explain the danger.
2. **Accept Quest**: Once the quest starts, talk to him again. He will urge you to hurry.
3. **Loot/Battle**: Collect the required item or defeat the target enemy.
4. **Conclusion**: Return to the NPC (or check the log) to see the quest advance and rewards applied automatically.
