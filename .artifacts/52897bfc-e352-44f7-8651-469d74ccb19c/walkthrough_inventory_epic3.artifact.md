# Walkthrough - Epic 3: Advanced Inventory System

I have completely overhauled the inventory management system to introduce item rarity, realistic weight limits, and advanced user interface features for sorting and filtering.

## Key Inventory Features

### 🎒 1. Realism: Weight & Load Management
- **Item Weights**: Every item now has a specific weight (e.g., 2.5kg for an "Épée Aurore", 0.2kg for "Herbe de Soin").
- **Inventory Limit**: The player now has a maximum carry capacity of **50.0 kg**.
- **Overweight Prevention**: If an item or chest would put the player over the limit, a message appears: *"Votre inventaire est trop lourd pour ramasser ça !"*.
- **Dynamic Calculation**: A dedicated weight indicator is visible at the bottom of the inventory screen.

### ✨ 2. Progression: Rarity & Materials
- **Item Rarity**: Items are now categorized into 5 tiers:
    - **COMMON** (White)
    - **UNCOMMON** (Green)
    - **RARE** (Cyan)
    - **EPIC** (Purple)
    - **LEGENDARY** (Orange)
- **Craft Materials**: Added a new item type `CRAFT_MATERIAL` and new items like "Fragment de Fer" and "Éclat de Cristal" to prepare for the crafting system.

### 🔍 3. Management: Sorting & Filtering
- **Smart Filtering**: The inventory is now divided into more precise categories: `Tout`, `Soins`, `Équipement`, `Matériaux`, and `Quêtes`.
- **Sorting Toggle**: You can now cycle through sorting modes by pressing the **'S' key**. Supported modes:
    - **NAME**: Alphabetical order.
    - **RARITY**: Highest rarity first.
    - **VALUE**: Most expensive items first.
    - **WEIGHT**: Heaviest items first.

## Verification Results

### Automated Tests
- Created `InventoryTest.kt` to verify weight math and sorting algorithms.
- **Status**: SUCCESS (All 38 tests in the suite passed).

### Manual Verification Path
1. **Loot a Chest**: Find an item and notice the "Charge" bar increase at the bottom of the screen.
2. **Sort the Bag**: Press 'S' to see your legendary "Épée Aurore" jump to the top of the list when sorting by Rarity.
3. **Change Filters**: Navigate between "Soins" and "Matériaux" to see the list update instantly.
