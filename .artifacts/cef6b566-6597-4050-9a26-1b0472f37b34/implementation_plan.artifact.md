# Fix ParticleEmitter SpawnShape Exception

The application crashes with `java.lang.IllegalArgumentException: No enum constant com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShape.rectangle` when loading particle effects. This is because the LibGDX `SpawnShape` enum uses the constant `square` to represent rectangular spawn areas, but the particle files `fireflies.p` and `rain.p` contain `shape: rectangle`.

## Proposed Changes

### Assets

#### [MODIFY] [fireflies.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/fireflies.p)
- Change `shape: rectangle` to `shape: square` at line 41.

#### [MODIFY] [rain.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/rain.p)
- Change `shape: rectangle` to `shape: square` at line 37.

## Verification Plan

### Manual Verification
- Deploy the application to an Android device or emulator.
- Navigate to screens that use the `fireflies.p` or `rain.p` particle effects (likely `ExplorationScreen` or `BattleScreen`).
- Verify that the application no longer crashes and the particle effects are displayed correctly.
