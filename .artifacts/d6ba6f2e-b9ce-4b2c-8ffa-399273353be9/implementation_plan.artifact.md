# Multi-Layer Tiled Rendering Support

Enable rendering specific Tiled layers (e.g., tree canopies, roofs) on top of the background image and the player character.

## User Review Required

> [!NOTE]
> I will implement a **naming convention** for Tiled layers. Any tile layer starting with "Over", "Top", "Roof", or "Foreground" will automatically be rendered **on top** of everything (background image, player, NPCs). All other layers will be treated as the "Base" and will be hidden if a background image is used.

## Proposed Changes

### [Exploration Screen](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)

#### [MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)
- Add `baseLayerIndices` and `overLayerIndices` to track Tiled layers.
- Update `loadMap()` to categorize layers based on their names.
- Update `draw()` to render layers in the correct order:
    1.  Background Image (if any).
    2.  Base Tiled Layers (only if no background image).
    3.  NPCs, Portals, Chests, Player.
    4.  **Over Tiled Layers** (always rendered on top, even with a background image).

## Verification Plan

### Manual Verification
1.  **Test Naming Convention:** Open `village.tmx` in Tiled (user action) and rename a layer to "Over_Trees".
2.  **Visual Check:** Verify that the "Over_Trees" layer appears above Nassim and the background image.
3.  **Fallback Check:** Verify that maps without a background image still render all layers correctly.
