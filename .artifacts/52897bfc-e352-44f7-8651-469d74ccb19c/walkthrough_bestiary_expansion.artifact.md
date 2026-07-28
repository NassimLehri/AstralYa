# Walkthrough - Bestiary Expansion

I have expanded the world of AstralYa with a set of new monsters, bringing more variety and strategic depth to every zone.

## New Additions to the Bestiary

### 🐝 1. Forest: Reine des Abeilles
- **Type**: Support / Mini-boss
- **Behavior**: Uses "Soin Royal" to restore health to her allies and "Dard Venimeux" to poison your party.
- **Loot**: Nectar Pur (Crafting material).

### 🔥 2. Grotto: Slime de Lave
- **Type**: Aggressive
- **Behavior**: This cosmic-aligned slime uses "Explosion de Feu" to inflict the **BURN** status effect on your heroes.
- **Challenge**: Requires careful health management due to damage-over-time.

### ⌛ 3. Desert: Spectre des Sables
- **Type**: Tactical
- **Behavior**: Master of the shifting sands, it uses "Tempête d'Âmes" to stun your entire party simultaneously.
- **Danger**: High agility makes it strike fast.

### 👼 4. Temple: Archange Déchu
- **Type**: Boss / High Magic
- **Behavior**: A powerful magical adversary using "Lance de Ténèbres" and "Dévastation" (AoE).
- **Encounter**: Rare and dangerous, it tests your magical defenses.

### 🛡️ 5. Castle: Chevalier du Néant
- **Type**: Aggressive / Tank
- **Behavior**: A heavy armored warrior with immense defense. It uses "Lame du Vide" for heavy single-target damage.
- **Strategy**: Requires high-power physical attacks or combos to break through.

## Technical Implementation
- **Data-Driven**: All stats and AI types were added directly to `enemies.json`.
- **Factory Registration**: `EnemyFactory.kt` has been updated with dedicated creation methods and these monsters are now part of the random encounter pools for their respective zones.
- **AI Synergy**: Utilizes the AI strategies implemented in Epic 2 (AGGRESSIVE, SUPPORT, TACTICAL).

## Verification
- **Test Suite**: Verified that the new monsters are loadable and statistically balanced via the automated balancing tests.
- **Status**: SUCCESS
