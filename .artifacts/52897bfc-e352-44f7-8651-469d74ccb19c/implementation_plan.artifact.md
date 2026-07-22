# Implementation Plan - UI Optimization & System Robustness

This plan focuses on improving the UI aesthetics across all menus and ensuring the game state and saving systems are robust against common mobile crashes.

## Proposed Changes

### UI Components Polish
Bring all sub-menus (Inventory, Party, Save, Options) to the same level of polish as the Main Menu and Exploration Screen.

#### [MODIFY] [UIScreens.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/UIScreens.kt)
- **Unified UI Framing**: Use the `ui_frame.png` asset to create a cohesive border around menu panels.
- **Enhanced Save Slots**:
    - Each slot will now display: **Map Name**, **Gold Count**, and **Playtime** (formatted as HH:MM).
    - If a slot is empty, it will clearly state "Emplacement Vide".
- **Dynamic Selection**:
    - Selection cursors and highlighted items will have a "pulsing" animation using a sine wave.
- **Party Screen Bars**:
    - Replace text-based HP/MP with graphical bars for a more modern look.
- **Scrolling Inventory**:
    - Add logic to scroll the item list if it exceeds the visible area, ensuring all items are accessible even with a large inventory.

---

### System Robustness & Crash Prevention
Address potential edge cases in the data layer.

#### [MODIFY] [UIScreens.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/UIScreens.kt) (SaveScreen)
- **Error Feedback**: Improve the error message handling when saving or loading fails (e.g., due to storage permissions or disk full).
- **Graceful Null Handling**: Ensure that trying to load an empty or corrupted slot doesn't lead to a crash.

#### [MODIFY] [AssetLoader.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/AssetLoader.kt)
- **Safe Retrieval**: Ensure that asset lookups (especially music) don't crash if called before the manager is fully updated (though already mostly handled).

## Verification Plan

### Manual Verification
- **Save/Load**: Create a save, then verify it appears with the correct Map Name and Gold count in the load menu.
- **Inventory**: Fill the inventory to verify scrolling.
- **Party Screen**: Check that HP/MP bars reflect the actual values accurately.
- **Animation**: Verify the smooth pulsing effect on menu selections.

### Performance Check
- Ensure that the graphical frames and bars don't cause significant overhead during menu transitions on mobile.
