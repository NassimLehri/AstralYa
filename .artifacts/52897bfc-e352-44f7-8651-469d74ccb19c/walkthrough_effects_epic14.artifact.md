# Walkthrough - Epic 14: Weather, Atmosphere & Advanced Effects

I have brought a new level of visual immersion and environmental storytelling to AstralYa by implementing a dynamic weather engine, professional screen-shake effects, and advanced post-processing.

## Key Visual & Gameplay Enhancements

### 🌦️ 1. Dynamic Weather Engine
- **Living World**: The game now features real-time weather cycles (Clear, Rain, Snow, Storm).
- **Atmospheric Tinting**: Exploration maps now automatically tint their colors to match the weather (e.g., darker blue during Rain, brighter white during Snow).
- **Automated Particles**: Integrated the `WeatherSystem` with the `ParticleManager`.
    - When it starts raining, droplets will automatically fall across the screen.
- **Gameplay Impact**: The weather now influences combat. During a **STORM**, Cosmic and Stellar magical skills receive a **25% damage bonus**.

### 🫨 2. Standardized Screen Shake Manager
- **Centralized Vibrations**: Created `ScreenShakeManager.kt` to handle all camera trauma.
- **Variable Intensity**: Supports different shake profiles: `LOW` (clics), `MEDIUM` (critical hits), `HIGH` (boss intros/ultimate skills).
- **Combat Impact**: Executing a "Lame Stellaire" or entering a hidden boss battle now triggers precise, impactful screen vibrations.

### ✨ 3. Professional Post-Processing (Bloom)
- **Glow Threshold**: Updated the `post_process.frag` shader to include a luminance threshold.
- **Stellar Brilliance**: Highlights and magical effects now emit a subtle "glow" (Bloom), making the Stellar crystals and special abilities pop with modern visual quality.

## Verification Results

### Automated Tests
- Created `WeatherSystemTest.kt` to verify transition logic and color math.
- Updated `CombatSystemTest.kt` to ensure damage multipliers are correctly applied during storms.
- **Status**: SUCCESS (All 50 project tests passed).

### Manual Verification Path
1. **Wait for Transition**: Stay in the Village for a minute. Observe the sky darkening and rain particles appearing.
2. **Battle in the Storm**: Find an encounter while it's raining/storming. Use Lwiz's spells and verify the damage boost in the combat log.
3. **Impact Feedback**: Land a critical hit. Verify the screen shakes with `MEDIUM` intensity for better tactical feedback.
