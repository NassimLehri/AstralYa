# Walkthrough - Visual Polish & Dynamic World

I have completed the visual overhaul of **AstralYa**, introducing a dynamic day/night cycle, environmental particles, and high-fidelity combat visuals.

## Changes Made

### 1. Dynamic Day/Night Cycle
- **[NEW] [TimeSystem.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/TimeSystem.kt)**: Manages a 12-minute in-game day. It calculates the world's ambient color by interpolating between Night, Dawn, Day, and Dusk.
- **[MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)**: The world now gradually changes color as time passes. A clock has been added below the gold display to show the current in-game time.

### 2. Environmental Particles
- **[NEW] [ParticleManager.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/ParticleManager.kt)**: A system to handle LibGDX `ParticleEffect` objects efficiently.
- **[NEW] Fireflies**: Added a `fireflies.p` effect that spawns magical glowing particles in the **Forêt Enchantée**.
- **[MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)**: Particles are now loaded and rendered in the game world, adding life to the environments.

### 3. Modern Combat Visuals
- **[MODIFY] [BattleScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/BattleScreen.kt)**:
    - **Shaders**: Integrated the post-processing shader (Vignette + Color Grading) into the combat screen.
    - **Ambient Lighting**: Battles now inherit the "mood" of the map where they started. A fight in a crystal cave will have a cool blue tint, while a desert fight will feel hot and bright.

## Verification Results

### Automated Tests
- **Build**: Successfully ran `:android:assembleDebug`.
- **Resources**: Verified all shader and particle files are correctly located in the assets folder.

### Visual Guide
- **Daylight**: Normal bright colors.
- **Dusk**: Warm orange/red tint.
- **Night**: Dark blue tint with visible vignette effect.
- **Particles**: Look for yellow fireflies when entering the forest area.

> [!TIP]
> **Customizing Time**: You can change the speed of the day/night cycle by adjusting `cycleDuration` in [TimeSystem.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/TimeSystem.kt).
