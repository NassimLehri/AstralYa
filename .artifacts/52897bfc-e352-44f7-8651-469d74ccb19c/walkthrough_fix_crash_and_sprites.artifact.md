# Walkthrough - Fix Startup Crash & Graphics Integration

I have resolved the startup crash issues and fully integrated the pixel art sprites into both exploration and combat.

## Changes Made

### build-system

#### [MODIFY] [android/build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/build.gradle.kts)
- Added native libraries for **x86** and **x86_64** architectures. This fixed the immediate crash on Android emulators which couldn't load the FreeType and Box2D components.

### utils

#### [MODIFY] [AnimationComponent.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/AnimationComponent.kt)
- Made the component **robust**: it now automatically detects the number of rows in a sprite sheet.
- If a specific animation row is missing (like in your current simple sprites), it gracefully falls back to the first available row instead of crashing.

#### [MODIFY] [AssetLoader.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/AssetLoader.kt)
- Added `getEnemyTexture(enemyId)` to map enemy IDs to their respective `.png` files.

### screens

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/BattleScreen.kt)
- Replaced the colored rectangle placeholders with actual textures:
    - **Background**: Now draws the environment-specific battle background.
    - **Enemies**: Now draws the generated pixel art for Slimes, Wolves, Golems, and Bosses.
    - **Heroes**: Now draws the hero sprites in the status panel.

### scripts

#### [MODIFY] [gen_sprites.py](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/gen_sprites.py)
- Updated the output path to be relative (`android/src/main/assets/sprites`), making it easier to run.
- Characters are now centered in **64x64 square frames**, ensuring they fit perfectly within the game's animation system.

## Verification Results

### Automated Tests
- Build successful: `:android:assembleDebug` completed.
- Core tests: `:core:test` passed.

### Manual Verification
- **Startup**: The app now reaches the Main Menu on emulators.
- **Exploration**: Nassim is correctly displayed as a 64x64 sprite on the map.
- **Battle**: Combat now feels much more complete with real backgrounds and monsters.

> [!TIP]
> To update your graphics, simply run `python gen_sprites.py` and the game will automatically use the new versions next time it starts!
