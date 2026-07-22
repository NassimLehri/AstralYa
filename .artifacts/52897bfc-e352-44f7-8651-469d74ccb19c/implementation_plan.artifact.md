# Implementation Plan - Dynamic Weather System

This plan introduces a dynamic weather system that adds rain, snow, and storms to the world. Weather will affect the ambient lighting and spawn atmospheric particles across the screen.

## User Review Required

> [!IMPORTANT]
> **Audio Assets**: I will implement the logic for weather sounds (rain falling, thunder), but you will need to download `sfx_rain.ogg` and `sfx_thunder.ogg` and place them in `assets/audio/` to hear them.
>
> **Performance**: Rain and snow use screen-space particles. I will keep the particle count low to maintain a high frame rate on mobile.

## Proposed Changes

### 1. Weather Logic
Create a system to manage weather transitions.

#### [NEW] [WeatherSystem.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/utils/WeatherSystem.kt)
- Define weather types: `CLEAR`, `RAIN`, `SNOW`, `STORM`.
- Handle transition timers (e.g., it stays rainy for 2-5 minutes).
- Calculate "Weather Tint" (e.g., gray/dark blue for rain).

### 2. Environmental Particles
Create the visual effects for weather.

#### [NEW] [rain.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/rain.p) & [snow.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/snow.p)
- Particle definitions for falling rain and drifting snow.

#### [MODIFY] [ExplorationScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/ExplorationScreen.kt)
- Integrate `WeatherSystem`.
- Spawn weather particles as "Screen Space" effects (they move with the player's screen, not the world).
- Combine `TimeSystem` tint with `WeatherSystem` tint for final lighting.

### 3. Combat Interoperability
Ensure weather carries over into battles.

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/BattleScreen.kt)
- Show weather particles in the background of combat if it's raining or snowing in the world.

## Verification Plan

### Automated Tests
- **Build**: Ensure the project compiles with the new `WeatherSystem`.
- **Sync**: Verify Gradle sync.

### Manual Verification
- **Visuals**: Enter a map and wait for a weather transition. Verify that the screen darkens and rain starts falling.
- **Audio**: Check if the code attempts to play weather sounds (monitor Logcat for missing file warnings).
- **Combat**: Trigger a battle while it's raining and verify the rain is still visible in the background.
