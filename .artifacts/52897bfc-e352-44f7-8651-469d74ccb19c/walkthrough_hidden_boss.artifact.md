# Walkthrough - Hidden Boss & Specialized Combat Mechanics

I have added an optional ultimate challenge to the world of Astralya: the **Gardien Antique**, a boss with unique defensive mechanics that force the player to master the new Combo system.

## Key Content & Mechanics

### 1. The Hidden Boss: Gardien Antique
- **Location**: Found in the **Sanctuaire de l'Ancien Gardien**, a hidden map accessible from the Desert.
- **Unique Defense**: The boss is immune to all standard physical attacks and basic magic. Standard hits will result in 0 damage and a message: *"L'armure de la créature ne réagit qu'aux attaques combinées !"*.
- **Vulnerability**: Only **Combo** attacks can penetrate its ancient shield.
- **High Rewards**: Defeating this boss guarantees the **Anneau de l'Infini**, the most powerful accessory in the game.

### 2. Secret Sanctum & Quest
- **New Map**: `sanctuaire_secret.tmx` - A small, atmospheric chamber filled with ancient energy.
- **Hidden Entrance**: A portal has been added to the **Désert Oublié**, hidden behind mountain peaks.
- **Story Hook**: Added the side quest **"Le Secret des Sables"**. It starts when the player finds a **Vieille Tablette** (located in a hidden chest in the desert), leading them to the sanctum.

### 3. Engine & Logic Updates
- **`comboOnly` Flag**: Enemies can now be marked as "Combo-Only" in `Entities.kt`, allowing for specialized tactical encounters.
- **Combat Logic**: Updated `CombatSystem.kt` to respect this immunity, providing immediate feedback to the player when a standard attack fails.

## Verification Results

### Automated Tests
- Added specific test cases to `CombatSystemTest.kt` to verify boss immunity and combo effectiveness.
- **Status**: SUCCESS (31 tests passed).

### Manual Verification Path
1. **Desert Exploration**: Find the hidden chest containing the "Vieille Tablette".
2. **Sanctum Entry**: Locate the portal at the edge of the desert mountains.
3. **The Duel**: Engage the Gardien Antique. Notice your normal attacks do nothing. Build your Combo Gauge and unleash a team attack to claim victory and the legendary ring.
