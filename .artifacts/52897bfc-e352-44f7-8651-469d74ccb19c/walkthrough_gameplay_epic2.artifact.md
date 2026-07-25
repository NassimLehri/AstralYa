# Walkthrough - Epic 2: Advanced Gameplay & Combat Mechanics

I have introduced significant depth to the combat system, focusing on tactical buffs, debuffs, and specialized enemy intelligence.

## Key Gameplay Enhancements

### 🧪 1. Professional Buff/Debuff System
- **Timed Effects**: All status effects (Poison, Stun, ATK Up, etc.) now have a defined **duration** in turns.
- **Stat Modifiers**: Implementation of `ActiveEffect` allows for percentage-based stat changes.
    - *Example*: Nassim's "Aura de Guerrier" now grants a real **+20% Attack bonus** for 3 turns.
- **Visual Feedback**: The battle UI now displays the remaining duration of the primary effect on each hero (e.g., `[POISON:2]`).
- **EndOfTurn Logic**: Effects are now correctly decremented and removed once their duration hits zero, with a log message notifying the player.

### 🧠 2. Specialized Enemy AI (Strategies)
- **AiStrategy Pattern**: Replaced the random skill selection with a robust strategy-based architecture.
    - **AGGRESSIVE**: Focuses on raw damage and AoE skills.
    - **SUPPORT**: Prioritizes healing and buffing allies.
    - **TACTICAL**: Uses status ailments (Stun, Poison) to disable the party.
    - **BOSS**: Features unique phases (e.g., uses ultimate skills when health is low).
- **Behavior Mapping**: Every enemy in `enemies.json` now has a specific `aiType`, making encounters feel unique and challenging.

### ⚡ 3. Advanced Combat Mechanics
- **Turn Interruption**: Stunned (`STUN`) enemies now correctly skip their turns.
- **Life Drain**: Added a specialized mechanic where certain skills (like "Drain de Vie") restore the user's HP based on the damage dealt.
- **Critical Interruption**: Critical hits now have a 30% chance to momentarily stun the target, adding a layer of luck and reward to high-agility builds.

## Verification Results

### Automated Tests
- Created `AiStrategyTest.kt` to verify healing and aggressive behaviors.
- Updated `CombatSystemTest.kt` to verify stat boosts and effect dissipation.
- **Status**: SUCCESS (35 tests passed).

### Manual Verification Path
1. **Strategic Buffing**: Use a buff skill and verify the "Total Attack" increase in the logs.
2. **AI Challenge**: Fight a Guardian enemy (SUPPORT) and observe it healing its allies when they are wounded.
3. **Turn Management**: Stun an enemy and verify they pass their turn with a visual "étourdi" message.
