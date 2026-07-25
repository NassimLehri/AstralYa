# Walkthrough - Modern Architecture Refactor

I have completely restructured the project architecture to follow a modular design inspired by modern game engines (Unity, Godot). This separation of concerns improves maintainability, scalability, and code clarity.

## New Architecture Overview

The project is now divided into three core pillars within the `:core` module:

### 1. `engine` (The Infrastructure)
- **`ResourceManager`**: Centralized asset management. All textures, music, and maps are now accessed through unique IDs (e.g., `ResourceManager.getTexture("nassim")`).
- **`ScreenManager`**: Handles screen transitions and scene management, decoupling UI from the main game class.
- **`EventBus`**: A lightweight messaging system for decoupled communication between modules (e.g., Quests listening for Combat events).

### 2. `game` (The Logic)
- **`combat`**: Isolated battle logic and systems.
- **`entities`**: Data models for Heroes, Enemies, NPCs, and Items.
- **`world`**: Map systems and world-state management.
- **`save`**: Robust persistence layer including `GameStateManager` (replaces `GameState`) and Repository/DAO logic.

### 3. `ui` (The Interface)
- **`screens`**: All game screens (`ExplorationScreen`, `BattleScreen`, etc.) consolidated in one package.
- **`components`**: Future home for reusable UI widgets.

## Key Changes & Refactors

- **`AstralYaGame.kt`**: Now a thin entry point that initializes managers and delegates to `ScreenManager`.
- **`GameState` -> `GameStateManager`**: Refactored to act as a proper state controller within the `game/save` domain.
- **Centralized Asset Paths**: Removed hardcoded strings across screens; everything now uses the `ResourceManager` aliases.
- **Package Integrity**: Updated over 20 files to use correct imports and removed legacy folders.

## Verification Results

### Automated Tests
- Ran `gradlew assemble` for the entire project.
- **Status**: SUCCESS (Core, Android, and Desktop/Lwjgl3 modules build correctly).

### Manual Verification Path
1. **Startup**: Observe the new `LoadingScreen` utilizing `ResourceManager.getProgress()`.
2. **Transitions**: Verify that switching between Exploration and Battle is handled via `ScreenManager`.
3. **Save/Load**: Confirm that the new `GameStateManager` correctly persists data through the moved Repository layer.
