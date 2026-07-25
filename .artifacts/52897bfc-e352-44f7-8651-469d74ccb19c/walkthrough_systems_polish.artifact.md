# Walkthrough - Combat Systems and World Interactivity Polish

I have introduced several advanced systems to deepen the combat strategy, improve enemy behaviors, and add interactive puzzles to the world exploration.

## Key Enhancements

### 1. Elemental Affinity System
- **Strategic Weaknesses**: Elements now have a rock-paper-scissors relationship.
    - *Example*: `LIGHT` and `STELLAR` are 1.5x more effective against `DARK` enemies.
- **Combat Feedback**: The battle log now displays "✨ FAIBLE !" (Weak) or "🛡️ RÉSISTÉ !" (Resisted) when elemental multipliers are applied.
- **Resistance**: Some elements now resist themselves or others (0.5x damage), forcing players to switch skills tactically.

### 2. Smarter Combat AI
- **Reactive Healing**: Enemies are no longer just random attackers. If an enemy's HP drops below 35%, they will prioritize using a healing skill if they have one.
- **Improved Skill Selection**: Enemies have a more balanced logic between physical attacks and powerful special skills.
- **New Capability**: Enemies can now heal themselves in battle, making high-HP targets or mini-bosses more challenging.

### 3. Environmental Puzzles & Persistence
- **Levers & Doors**: Added a new system for interactive objects.
    - *Example*: In the Grotto, you can now find a **Levier** (Switch) that opens a **Porte Verrouillée** (Locked Door) elsewhere on the map.
- **Map State Persistence**: The state of these objects (on/off, open/closed) is now stored in the `GameStateManager` and persists through save files.
- **Visual Cues**: Interactive switches change color (Red to Green) and display a label when approached.

## Verification Results

### Automated Tests
- Updated and ran the full suite of unit tests in the new architecture.
- **Status**: SUCCESS (30 tests passed).

### Manual Verification Path
1. **Elemental Strategy**: Use a Light spell on a Dark enemy. Observe the 1.5x damage bonus and the "FAIBLE" message in the log.
2. **AI Challenge**: Battle a Golem and lower its health. Notice it might try to stabilize its HP.
3. **Exploration**: Find the lever in the Grotto and toggle it to unlock the path.
