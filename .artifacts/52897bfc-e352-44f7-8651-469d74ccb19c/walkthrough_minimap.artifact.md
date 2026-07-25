# Walkthrough - Mini-map System Implementation

I have added a real-time mini-map to the exploration screen to assist with navigation in the larger maps and dungeons of Astralya.

## Key Features

### 1. Real-time Navigation
- **Top-Right Display**: A semi-transparent mini-map now appears in the top-right corner during exploration.
- **Dynamic Scaling**: The mini-map automatically adjusts its aspect ratio based on the current map's dimensions (e.g., vertical for the Grotto, square for the Village).
- **Toggle Visibility**: You can press the **'M' key** to show or hide the mini-map.

### 2. Map Markers (Points of Interest)
- **Player**: A pulsing white dot representing Nassim's current position.
- **PNJs**: Yellow dots to quickly locate characters to talk to.
- **Chests**: Small squares that change from **Gold** (Closed) to **Gray** (Opened) once looted.
- **Portals**: Cyan squares showing the exits to other zones.
- **Mechanisms**: Circular markers for levers that change from **Red** (Off) to **Green** (On).

### 3. Dungeon Classification
- Added an `isDungeon` flag to the `GameMap` data structure.
- Marked the **Grotto** and both floors of the **Castle** as dungeons to ensure navigation is prioritized in these areas.

## Verification Results

### Automated Tests
- Ran `:core:assemble` to verify the new rendering logic.
- **Status**: SUCCESS

### Manual Verification Path
1. **Start in Village**: Observe the mini-map showing the island layout and NPC locations.
2. **Move Around**: Verify the white dot follows your movements accurately.
3. **Toggle**: Press 'M' to hide the map if you want a cleaner view.
4. **Enter Grotto**: Notice the mini-map changes shape to reflect the deeper, vertical layout of the cave.
