# Walkthrough - Tile-Based Collision Detection

I have implemented collision detection for the exploration screen. The game now recognizes a "Collisions" layer in your Tiled maps and prevents the player from passing through occupied tiles.

## Changes Made

### 1. Collision Logic in ExplorationScreen
Updated [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt):
- **Collision Layer**: The screen now looks for a layer named `"Collisions"` when loading a Tiled map.
- **Sliding Movement**: Refactored `handleMovement` to check X and Y axes independently. If the player walks diagonally into a wall, they will now slide along it rather than stopping completely.
- **Bounding Box**: Implemented a `canMoveTo` helper that checks the four corners of a small 20x20 collision box centered on the player's position.

## Verification Results

### Automated Tests
- **Build**: Successfully ran `:android:assembleDebug`.

### How to use it in Tiled:
1.  Open your `.tmx` file in **Tiled Map Editor**.
2.  Add a new **Tile Layer** and name it exactly `Collisions`.
3.  Paint tiles on this layer where you want to block the player (e.g., on top of walls, trees, or water).
4.  Save the map. The game will automatically enforce these boundaries next time you play.

> [!TIP]
> You can make the "Collisions" layer invisible in Tiled if you don't want to see the collision tiles over your artwork, or use a specific "invisible collision" tileset. The code will still detect the tiles even if the layer is hidden in Tiled.
