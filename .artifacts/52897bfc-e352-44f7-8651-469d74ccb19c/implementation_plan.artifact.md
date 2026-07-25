# Implementation Plan - Finalizing Epic 17 (Editors) & Data-Driven Quests

This plan completes the "Developer Tools" epic by making the Quest system data-driven and expanding the `DataEditorScreen` to handle all game content (Items, Enemies, Quests) with full property editing.

## Proposed Changes

### [1. Data-Driven Quests (Epic 11 Completion)]

#### [NEW] `quests.json` (in `android/src/main/assets/data/`)
- Move all hardcoded quests from `QuestRegistry` to this JSON file.

#### [MODIFY] [QuestRegistry.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/game/quests/QuestSystem.kt)
- Refactor `loadQuests()` to pull data from `DataManager`.

#### [MODIFY] [DataManager.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/engine/core/DataManager.kt)
- Add support for loading `Quest` objects from JSON.

---

### [2. Advanced Data Editor (Epic 17)]

#### [MODIFY] [DataEditorScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/ui/screens/DataEditorScreen.kt)
- **Quest Mode**: Add a third mode ("QUESTS") to edit quest titles, descriptions, and rewards.
- **Enhanced Item Panel**: Add controls for `ItemRarity` (cycle through tiers) and `ItemType`.
- **Enhanced Enemy Panel**: Add controls for `Element` and `aiType`.
- **Better Navigation**: Ensure the "Details" panel on the right scales better with more fields.

---

### [3. Reliability Fixes]

#### [MODIFY] [Entities.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/game/entities/Entities.kt)
- Ensure all relevant data classes have no-arg constructors (or default values) for LibGDX JSON deserialization.

## Verification Plan

### Automated Tests
- Run `QuestSystemTest.kt` to ensure quest logic still works with JSON data.

### Manual Verification (Desktop)
- **Launch Editor**: Use `EditorLauncher`.
- **Edit Quest**: Modify the gold reward of "L'Éveil des Gardiens".
- **Save & Reload**: Verify `quests.json` on disk updates and the change is reflected in the game.
- **Full Coverage**: Navigate between Items, Enemies, and Quests modes.
