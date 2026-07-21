# Improve Smartphone Experience & Visuals

The user wants to replace the placeholder blue square (hero) with a real image, change the starting map, and improve the smartphone UI by removing keyboard-specific instructions and adding touch interactions.

## Proposed Changes

### Core Logic & Data

#### [MODIFY] [GameState.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/data/GameState.kt)
- Update `newGame()` to make the starting map configurable via a constant, allowing easy changes.

### UI & UX Improvements

#### [MODIFY] [MenuScreens.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/MenuScreens.kt)
- Remove keyboard-specific hints ("↑↓ Naviguer | ENTRÉE Confirmer").
- Add a more intuitive "Tap to select, Tap again to confirm" interaction logic or simply rely on the existing touch detection which is already partially implemented.

#### [MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)
- **Hero Image**: Cache the hero texture in `show()` to ensure it's loaded and avoid `try-catch` overhead in `render()`. Ensure `nassim.png` is used correctly.
- **Action Button**: Add a visible "Action" button (on the right side of the screen) for interacting with NPCs and Chests.
- **Interaction Logic**: Update `checkNpcInteraction()` and `checkChests()` to trigger when the "Action" button is pressed or when a touch occurs in the action zone.
- **Menu Navigation**: Make the in-game menu touch-navigable.

#### [MODIFY] [UIScreens.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/UIScreens.kt)
- Update hints to remove keyboard references.
- Add touch detection for menu items in `InventoryScreen`, `PartyScreen`, `SaveScreen`, and `OptionsScreen`.

## Verification Plan

### Automated Tests
- None possible for UI/Rendering via Gradle, but I will ensure the code compiles.

### Manual Verification
- Deploy to the device and verify:
    - The hero is rendered as an image (Nassim).
    - The starting map is changed (if a change was requested/applied).
    - Menus are navigable via touch.
    - NPCs/Chests can be interacted with using the new on-screen "Action" button.
    - Keyboard hints are replaced or removed.
