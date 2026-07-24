# Implementation Plan - Total Graphical Overhaul of Maps

The goal is to replace the "repetitive grid" look with the high-quality assets already present in the project but currently ignored or unused.

## Proposed Changes

### [1. Engine Support for Full-Map Backgrounds]

#### [MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)
- Remove the logic that skips background images starting with `_`. This prefix was preventing `_map_village_bg.png` (the beautiful island village) from being displayed.
- Ensure that when a `mapBgTexture` is drawn, it uses the map's full pixel dimensions (`widthTiles * TILE_SIZE`).

### [2. Map Registry & Asset Linkage]

#### [MODIFY] [MapSystem.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/map/MapSystem.kt)
- Update `visualBg` for all maps:
    - `VILLAGE_DEPART`: Use `sprites/_map_village_bg.png`.
    - `FORET_ENCHANTEE`: Use `sprites/_map_foret_bg.png` (Note: If this is a tileset, we will paint the TMX instead).
- Verify and update `Portal` and `NPC` positions to match the visuals of the new background images (especially for the island village).

### [3. TMX Painting (For maps without full backgrounds)]

#### [MODIFY] [foret.tmx](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/maps/foret.tmx)
- Replace the solid grid with a varied layout using `tileset_foret.png`.
- Add clumps of trees using the `Trees` layer.

#### [MODIFY] [desert.tmx](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/maps/desert.tmx)
- Use different dirt/sand tiles to create a natural terrain.
- Add "Mountains" tiles (IDs 17-80) at the borders and as obstacles.

#### [MODIFY] [grotte.tmx](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/maps/grotte.tmx)
- Redesign the layout to feel like a cave (winding paths, dead ends).
- Use `lava.png` (Hazards layer) sparingly for visual interest.

## Verification Plan

### Automated Tests
- Build and run the project: `./gradlew :android:assembleDebug`.

### Manual Verification
- **Village**: Confirm that the beautiful island village background is now visible instead of the blue grid.
- **Forest**: Confirm that trees and grass look like a real forest.
- **Collisions**: Ensure that even with the new visuals, collisions still prevent the player from walking through water or walls.
