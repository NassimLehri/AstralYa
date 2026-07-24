# Visual Polish & Portal Loop Fix

I have improved the exploration visuals by adding support for sprites and background images, and fixed the infinite teleport loop that was blocking player movement between maps.

## Changes

### [Map System](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/map/MapSystem.kt)
- **Background Support**: Added `visualBg` to `GameMap` to allow full-image backgrounds.
- **NPC Variety**: Added `spritePath` to `NPC` for custom villager appearances.
- **Loop Fix**: Adjusted all portal target coordinates. Nassim now lands safely away from the return portal, preventing the "stuck in portal" loop.

### [Asset Loading](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/AssetLoader.kt)
- **New Sprites**: Added loading for `portal.png`, `chest_closed.png`, and `chest_open.png`.
- **Backgrounds**: Added placeholder loads for `map_village_bg.png` and `map_foret_bg.png`.

### [Exploration UI](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)
- **Background Rendering**: If a `visualBg` is defined for a map, it is now rendered behind the tilemap.
- **Multi-Layer Rendering**: Added support for **Foreground layers**. Any Tiled layer starting with `Over`, `Top`, `Roof`, or `Foreground` will be rendered *on top* of Nassim and NPCs.
- **Interactive Sprites**:
    - Portals are now drawn using `portal.png` (replacing the blue square).
    - Chests now use `chest_closed.png` and `chest_open.png` to visually reflect their state.
- **Safe Teleportation**: Confirmed target offsets are applied correctly.

## Verification Results

### Manual Verification Required
> [!IMPORTANT]
> To use the new layer system:
> 1. Open your map in Tiled.
> 2. Create a new Tile Layer.
> 3. Name it **`Over_Trees`** or **`Top_Layer`**.
> 4. Place tiles (like tree canopies) on this layer. They will now appear above Nassim!

> [!IMPORTANT]
> Please add the following images to `android/src/main/assets/sprites/` to see the full effect:
> - `portal.png` (64x64)
> - `chest_closed.png` (48x48)
> - `chest_open.png` (48x48)
> - `map_village_bg.png` (Full map size)
> - `map_foret_bg.png` (Full map size)

- **Portal Loop**: Walk from the Village to the Forest. You should land in the forest and be able to walk around freely without being sent back immediately.
- **Visuals**: Observe the portals and chests; they should no longer be simple colored rectangles.
