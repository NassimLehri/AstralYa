# Walkthrough - Particle Emitter Parsing Fix

I have resolved the `FATAL EXCEPTION: GLThread` caused by an error parsing the particle emitter files.

## Changes Made

### Assets Component

#### [MODIFY] [fireflies.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/fireflies.p)
- **Standardized Format**: Reformatted the file to match the strict property order and naming conventions of LibGDX 1.12.1.
- **Fixed Particle Count**: Changed `min/max` to `minParticleCount/maxParticleCount`.
- **Corrected Tint Data**: Updated `colorsCount` to `9` to correctly represent the 9 float values (3 colors x RGB).
- **Cleaned Up "Always Active" Fields**: Removed redundant `active: true` lines for properties that are always active in LibGDX 1.12.1 (like `Transparency` and `Scale`), preventing the parser from skipping data lines.
- **Simplified Image Path**: Changed the image path to just `cursor.png` to work correctly with the asset directory configuration.

#### [MODIFY] [rain.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/rain.p)
- **Standardized Format**: Applied the same LibGDX 1.12.1 compatibility fixes as in `fireflies.p`.
- **Fixed Tint Data**: Updated `colorsCount` to `3` for the single color defined.

## Verification Results

### Code Review
- The reformatted files were verified against the `com.badlogic.gdx.graphics.g2d.ParticleEmitter#load` method source code for version 1.12.1.
- All property headers and value keys now align with what the `BufferedReader` expects during sequential parsing.

### Expected Outcome
- The application will now successfully load the `fireflies.p` and `rain.p` effects without throwing a `RuntimeException` on the GL thread. This allows the `ExplorationScreen` to initialize and render maps containing these effects (like the Forest map) without crashing.
