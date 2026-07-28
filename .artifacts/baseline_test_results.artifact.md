# Baseline Test Results - Vanilla LibGDX Rendering

This test was performed using a 100% vanilla `OrthogonalTiledMapRenderer` with no custom logic, bypasssing the AstralYa engine.

## Test Environment
- **Loader**: `TmxMapLoader` (default)
- **Renderer**: `OrthogonalTiledMapRenderer` (vanilla)
- **Camera**: centered on map, no zoom.
- **Assets**: Directly from `android/src/main/assets/maps/`.

## Logs & Audit
| Map | Dimensions | Tile Size | Tilesets |
| :--- | :--- | :--- | :--- |
| `village.tmx` | 30x20 | 32x32 | `overworld` |
| `maison_interieur.tmx` | 15x12 | 32x32 | `tileset` |
| `grotte.tmx` | 30x50 | 32x32 | `grotte`, `lava` |
| `cite_volante.tmx` | 45x35 | 32x32 | `treetop`, `water` |

## Generated Screenshots
The following baseline captures are available in the assets folder for comparison:
- [baseline_village.png](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/baseline_village.png)
- [baseline_maison_interieur.png](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/baseline_maison_interieur.png)
- [baseline_grotte.png](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/baseline_grotte.png)
- [baseline_cite_volante.png](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/baseline_cite_volante.png)

## Next Steps
Please compare `baseline_village.png` with the rendering seen in the game.
1. **If Baseline is Correct**: The issue is confirmed to be in our custom `ExplorationScreen` rendering loop (Pass BELOW/ABOVE or filtering).
2. **If Baseline is Incorrect**: The issue is in the TMX/TSX data or the `TmxMapLoader` interaction with external tilesets.
