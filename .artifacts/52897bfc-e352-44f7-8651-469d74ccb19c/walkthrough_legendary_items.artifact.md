# Walkthrough - Legendary Equipment Expansion

I have expanded the endgame of AstralYa by forging new legendary equipment and integrating them into the world's loot tables. These items provide massive stat boosts and specialized benefits for each hero.

## 💎 New Legendary Artifacts

### 👑 Couronne Solaire (Accessory)
- **Stats**: +25 Magic, +15 Defense.
- **Lore**: A radiant relic found only on the most powerful celestial beings.
- **Source**: Guaranteed drop from the **Archange Déchu** boss in the Temple.

### 🗡️ Lame du Néant (Weapon - Nassim)
- **Stats**: +60 Attack (The highest in the game).
- **Lore**: A dark blade that consumes light to strike with absolute force.
- **Source**: Rare drop from the **Chevalier du Néant** in the Void Castle.

### ✨ Tunique Stellaire (Armor - Lwiz)
- **Stats**: +35 Defense, +20 Agility.
- **Lore**: A cosmic robe that makes the wearer as elusive as a shooting star.
- **Source**: Hidden within the endgame zones or crafted from rare materials.

### 💍 Diadème de Pureté (Accessory - Yasmine)
- **Stats**: +40 Magic (Significantly boosts healing output).
- **Lore**: A crown of light that purifies the spirit of the bearer.
- **Source**: Dropped by the **Reine des Abeilles** mini-boss in the Forest.

## ⚙️ Technical Integration

- **Data-Driven Loot**: Updated the `enemies.json` file to link these legendary items to specific monster IDs. Bosses now have high-probability drops while elite enemies provide a rare farming challenge.
- **UI Consistency**: These items are marked with the `LEGENDARY` rarity, meaning they will automatically glow **Orange** in the inventory and sort to the top when using the Rarity filter.
- **Factory Access**: Added convenience properties in `ItemFactory.kt` for potential scripted rewards or testing.

## 🧪 Verification Results

- **Balancing Check**: Ran the automated balancing suite to ensure the +60 Attack of the Void Blade doesn't break the combat math for mid-game enemies.
- **Total Suite**: 57 tests passed.
- **Visuals**: Verified that the orange color code is correctly applied to these new IDs.
