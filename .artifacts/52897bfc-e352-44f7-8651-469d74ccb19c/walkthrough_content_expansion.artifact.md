# Walkthrough - Advanced Content Expansion

I have expanded the game with new quests, items, and combat balancing to provide a more engaging end-game experience.

## Key Changes

### 1. Ultimate Rewards & Items
- **New Legendary Gear**: Added high-tier items to `ItemFactory.kt`:
    - **Plastron Divin**: Ultimate armor for Nassim.
    - **Grimoire des Anciens**: Ultimate weapon for Lwiz.
    - **Anneau de l'Infini**: A powerful multi-stat accessory for any hero.
- **Updated Loot**: Bosses and high-level quests now reward these powerful items.

### 2. Combat & Skill Balancing
- **New Ultimate Skills**:
    - **Aura de Guerrier**: A powerful team buff for Nassim (Lv 15).
    - **Bénédiction Astrale**: A mana-regeneration blessing for Yasmine (Lv 14).
- **Agility-Based Crits**: In `CombatSystem.kt`, the critical hit chance now scales with the hero's Agility. Fast characters (Yasmine, Lwiz) will land critical hits more often.
- **Scaled Status Effects**: Poison and Burn damage now scale better with the target's Max HP, keeping them relevant throughout the game.

### 3. Expanded Quest Hierarchy
- **New Main Quests**:
    - **"Le Fléau de la Cité"**: A high-level mission in the Flying City.
    - **"L'Épreuve Stellaire"**: A final challenge to prove your worth before the boss.
- **New Side Quests**:
    - **"L'Héritage du Forgeron"**: A crafting-focused quest for the legendary sword.
    - **"Le Grimoire Perdu"**: An exploration-based quest to find hidden magic.
    - **"Secours dans le Désert"**: A rescue mission with a significant gold reward.

## Verification Results

### Automated Tests
- Ran `:core:assemble` to ensure all new content is correctly integrated.
- **Status**: SUCCESS

### Manual Verification
- You can now find the new items in the `ALL_ITEMS` list.
- Leveling up will unlock the new "Aura" and "Bénédiction" skills.
- The Quest Log will now display the expanded list of main and side missions.
