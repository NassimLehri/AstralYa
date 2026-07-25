# Walkthrough - Advanced Combat and World Mechanics

I have introduced elemental strategy, improved enemy AI, and interactive map puzzles to enhance the gameplay depth of Astralya.

## Key Enhancements

### 1. Elemental Strategy System
- **Elemental Affinities**: Elements now have specific strengths and weaknesses:
    - `STELLAR` and `LIGHT` deal **1.5x damage** to `DARK` enemies.
    - `COSMIC` deals **1.5x damage** to `STELLAR` enemies.
- **Combat Feedback**: The battle log now clearly indicates elemental effectiveness:
    - **"✨ FAIBLE !"** (Weak) appears when you hit a vulnerability.
    - **"🛡️ RÉSISTÉ !"** (Resisted) appears against resistances.

### 2. Smarter Combat AI
- **Strategic Healing**: Enemies are no longer purely offensive. If an enemy's health drops below **35%**, they will prioritize using a healing skill (if available).
- **Behavioral Logic**: Enemies can now heal themselves during their turn, making certain mini-bosses and monsters much more tactical targets.

### 3. Interactive Map Puzzles
- **Levers & Locked Doors**: Introduced a state-driven interaction system.
    - *Example*: In the **Grotto**, you can now find a switch that toggles a locked door elsewhere on the map.
- **Visual Feedback**: Switches change color (Red to Green) when activated and provide a descriptive message.
- **Collision Updates**: Doors dynamically enable or disable their collision based on the switch state.
- **State Persistence**: The state of every switch and door is stored in the `mapState` and persists through saves.

## Verification Results

### Automated Tests
- Updated logic and ran `gradlew :core:test`.
- **Status**: SUCCESS (30 tests passed).

### Manual Verification Path
1. **Combat**: Battle a Dark enemy with Nassim's "Coup Stellaire" and observe the "FAIBLE" message and damage bonus.
2. **AI**: Lower a Golem's health in the Grotto and watch it attempt to heal itself.
3. **Exploration**: Locate the lever in the Grotto and use it to unlock the path to the Crystal Golem.
