# Required Image Assets

This document lists all the image assets required for **AstralYa**, their expected filenames, and their locations within the project.

## 📂 Sprites & Backgrounds
**Location:** `android/src/main/assets/sprites/`

### 🛡️ Heroes (Sprite Sheets)
Standard LPC layout (832x1344 pixels, frames of 64x64).
- **`nassim.png`**: Main protagonist (Sword/Knight).
- **`yasmine.png`**: Priestess (Healing/Light).
- **`lwiz.png`**: Mage (Cosmic/Arcane).

### 👾 Enemies
- **`enemy_slime.png`**: Basic slime monster.
- **`enemy_loup.png`**: Dark forest wolf.
- **`enemy_golem.png`**: Crystal cave guardian.
- **`boss_morvax.png`**: The Lord of the Void (Large sprite).

### ⚔️ Battle Backgrounds
Resolution: 512x288 (scaled to fit) or higher.
- **`battle_bg_village.png`**: Peaceful village area.
- **`battle_bg_foret.png`**: Enchanted forest.
- **`battle_bg_grotte.png`**: Crystal cave.
- **`battle_bg_desert.png`**: Forgotten ruins.
- **`battle_bg_temple.png`**: Star temple.
- **`battle_bg_cite.png`**: Floating city.
- **`battle_bg_chateau.png`**: Morvax's fortress.

### 🖼️ Menus & Splash
- **`splash.png`**: Intro logo screen (1920x1080).
- **`title_bg.png`**: Main menu background.
- **`ui_frame.png`**: Window border/background for dialogue and menus.
- **`cursor.png`**: Menu selection cursor.
- **`effects.png`**: Combat animations and particles.

---

## 🗺️ Tiled Maps
**Location:** `android/src/main/assets/maps/`

### 🧩 Tilesets
- **`tileset.png`**: The main tileset used by all `.tmx` maps. This image contains all the ground, wall, and decoration tiles.

---

## 📝 Notes
> [!TIP]
> **Scaling**: LibGDX handles scaling, but it is recommended to keep pixel art consistent.
> **Sprite Sheets**: The `AnimationComponent` expects `nassim.png`, `yasmine.png`, and `lwiz.png` to have a specific grid (13 columns, 21 rows) for animations to work correctly.
