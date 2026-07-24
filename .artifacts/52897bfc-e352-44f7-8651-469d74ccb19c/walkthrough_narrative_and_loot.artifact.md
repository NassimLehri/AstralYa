# Walkthrough - Narrative Intro and Loot Overhaul

I have added a cinematic introduction to the game and completely overhauled the reward system to make exploration and combat much more engaging and rewarding.

## Key Enhancements

### 1. Narrative Cinematic
- **New `IntroScreen`**: Starting a "Nouvelle Partie" now triggers a scrolling narrative sequence.
- **The Lore of Astralya**: The text describes the peaceful past of Astralya, the rise of Morvax, and the emergence of our three heroes. This provides crucial context before the player lands in the village.
- **Visual Polish**: Text fades in and out at the edges of the screen for a professional, cinematic feel.

### 2. Advanced Loot & Economy
- **Loot Tiers**: Items now have rarity-based drop rates. Consumables are common, while weapons and armor are much rarer and more rewarding to find.
- **Boss Guarantee**: Defeating a boss (like Morvax or the Golem) now **guarantees** at least one high-tier equipment drop, eliminating frustration.
- **Gold Variance**: Gold rewards are no longer static. They now vary between 90% and 110% of the base amount, making the economy feel more organic.
- **Enriched Loot Tables**: Enemies now have more diverse drop lists, including rare materials and accessories.

### 3. Combat Polish & UI
- **Status Visibility**: Combat status effects (Poison, Blessed, etc.) now have dedicated colors and clear labels in the battle UI.
- **Celebratory Level Up**: The "LEVEL UP" phase has been improved with a pulsing animation and clear hints when new powers are unlocked.
- **Sound Feedback**: Specialized sound effects play during critical events like Invocations or Boss appearances.

## Verification Results

### Automated Tests
- Ran `:core:assemble` to verify that all new screens and logic branches are correctly integrated.
- **Status**: SUCCESS

### Manual Verification Path
1. **New Game**: Select "Nouvelle Partie" to experience the new Intro sequence.
2. **Combat Reward**: Defeat a mini-boss and observe the guaranteed equipment drop in the battle log.
3. **Status Effects**: Get poisoned in battle and notice the green color-coded status label in the hero panel.
