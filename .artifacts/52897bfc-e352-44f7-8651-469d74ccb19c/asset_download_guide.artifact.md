# 🎨 Asset Download Guide (Modern & Detailed)

Since you'll be downloading sprites and tilesets, here is a guide on how to pick assets that will look great with the new "Modern" engine I'm building.

## 🌟 Where to Find High-Quality Assets
1.  **[Itch.io (Game Assets)](https://itch.io/game-assets/free/tag-rpg)**: The best place for modern, detailed assets. Search for "detailed pixel art" or "HD RPG".
    *   *Recommended Creators*: Seliel the Shaper, LimeZu (Modern Interiors), Cainos.
2.  **[OpenGameArt.org](https://opengameart.org/)**: Good for free music and SFX. For sprites, look for "LPC" (Libre Pixel Cup) if you want to keep the current animation system.
3.  **[Unity Asset Store](https://assetstore.unity.com/)**: Even if you're not using Unity, you can often download 2D sprite packs from here (just check the license).

## 📏 What to Look For
### ⚔️ Heroes (Sprites)
- **Format**: Look for **Sprite Sheets** or **Atlases**.
- **Animation**: Ensure they have at least 4 directions (Up, Down, Left, Right).
- **Layout**: If you use "LPC" sprites, they will work with the code I wrote without any changes. If you use a different layout, let me know, and I will adjust the `AnimationComponent`.

### 🗺️ Tilesets (Maps)
- **Grid Size**: I recommend **32x32** or **48x48** for a "detailed" look.
- **Auto-tiles**: Look for tilesets that include "Auto-tiles" for paths, water, and walls—it makes designing maps in Tiled much faster.

### 🖼️ UI & Fonts
- **Resolution**: Look for "UI Kits" with a resolution of at least **1920x1080** icons/frames so they look crisp on modern phones.
- **Fonts**: Search for `.ttf` or `.otf` files. "Modern Serif" or "Clean Sans" fonts look very professional.

## 🛠️ Important Tip
When you download a `.zip` of assets:
1.  Put all `.png` files in `android/src/main/assets/sprites/`.
2.  Put any `.tmx` files and their tilesets in `android/src/main/assets/maps/`.
3.  Put any `.ttf` fonts in `android/src/main/assets/fonts/`.

**Tell me when you've downloaded something, and I'll help you integrate it!**
