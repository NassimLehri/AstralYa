# Walkthrough - Advanced Combo & Synergy System

I have implemented a dynamic cooperative attack system that allows heroes to combine their powers once they have built up enough battle energy.

## Key Features

### ⚡ Combo Gauge & Accumulation
- **Dynamic Energy**: Added a "Combo Gauge" (0..100) to the combat state.
- **Battle Momentum**:
    - **Offense**: Successful hits on enemies grant **+5 points**.
    - **Defense**: When a hero takes damage, the gauge increases by **+10 to +12 points**.
- **Visual Feedback**: A pulsing yellow gauge is now visible in the battle interface, showing exactly how much energy you've stored.

### 🤝 Strategic Cooperation
- **Menu Integration**: The "Combo" command only appears in the action menu when the gauge is at **50% or higher**.
- **Hero Synergy**: Each combo requires specific heroes (e.g., Nassim + Lwiz for "Lame Stellaire"). If a required hero is KO or not in the party, the combo is disabled.
- **Cost Management**: Using a combo consumes both MP from participants and points from the Combo Gauge.

### 💥 High-Impact Effects
- **Lame Stellaire**: Nassim and Lwiz combine for massive stellar damage.
- **Lumière Astrale**: Yasmine and Lwiz heal the entire party while damaging all enemies.
- **Rempart Sacré**: Nassim and Yasmine shield the entire team from incoming attacks.
- **Spectacle**: Executing a combo triggers a heavy screen shake and a special sound effect to mark the moment.

## Verification Results

### Automated Tests
- Updated and ran all core logic tests to ensure gauge accumulation and MP costs are handled correctly.
- **Status**: SUCCESS

### Manual Verification Path
1. **Build Energy**: Fight a group of slimes. Notice the combo bar filling as you attack and get hit.
2. **Unlock Command**: Once the bar reaches half-full, select the current hero. Observe the "Combo" option appearing in the menu.
3. **Execute**: Select "Lumière Astrale". Verify that your party recovers HP while the slimes take light damage.
