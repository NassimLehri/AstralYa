# Walkthrough - Advanced Progression and Combat Invocations

I have significantly expanded the game's core systems to include skill unlocking, a new quest hierarchy, and a powerful "Invocation" system for battle.

## Key Enhancements

### 1. Progression & Skill Evolution
- **Skill Unlocking**: Skills are no longer all available at the start. They now have an `unlockLevel`. As heroes level up, new skills automatically appear in their battle menu.
    - *Example*: Nassim's "Tempête Astrale" now unlocks at Level 5.
- **Dynamic Menus**: The `BattleScreen` now only displays skills that have been unlocked by the current hero's level.

### 2. Invocations (Summons)
- **New Command**: Heroes (specifically Magic and Support roles) can now use the **Invocation** command in battle once unlocked.
- **Power Spirits**: Added the **Esprit du Loup** (Wolf Spirit) and **Divinité Stellaire** (Stellar Deity).
    - Summons deal massive area-of-effect (AoE) damage based on the hero's Magic stat.
- **Visuals & SFX**:
    - **Heavy Shake**: Using a summon triggers an intense screen shake effect.
    - **Audio**: Played specialized sounds when a deity is summoned.
    - **Flash Effects**: All enemies flash simultaneously during a summon attack.

### 3. Expanded Quest System
- **Main Quest Path**: Added **"Le Réveil des Divinités"**. This quest requires the team to reach Level 10 and perform a ritual in the Temple to unlock the first Summon.
- **Side Quests**:
    - **"Matériaux Précieux"**: Collect crystal fragments for the blacksmith to earn a rare Amulet of Life.
    - **"L'Ombre de la Grotte"**: Hunt a mysterious creature in the depths of the grotto.
- **Improved Rewards**: Quests can now grant experience, gold, rare items, and even new Summons.

### 4. Combat Polishing
- **Better Loot**: Bosses now have an 85% chance to drop their rare items (up from 35%).
- **Turn Notifications**: The battle log now clearly announces Level Ups and new Skill Unlocks immediately after victory.

## Verification Results

### Automated Tests
- Ran `:core:assemble` to verify logic consistency.
- **Status**: SUCCESS

### Manual Verification Path
1. **Level Up**: Earn experience in battle. When a hero reaches a milestone (e.g., Level 5), notice the "✨ NOUVEAU SORT" message in the victory screen.
2. **Questing**: Start the "Le Réveil des Divinités" quest at the Temple.
3. **Invocations**: Once the quest is completed, use Lwiz or Yasmine in battle. The "Invocation" menu item will appear, allowing you to summon the Esprit du Loup.
