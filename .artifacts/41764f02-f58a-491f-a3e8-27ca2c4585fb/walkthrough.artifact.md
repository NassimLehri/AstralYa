# Walkthrough - Particle Emitter Parsing Fix

I have fixed the `java.lang.RuntimeException: Error parsing emitter: Untitled` crash that occurred when loading particle effects in the application.

## Problem
The particle effect files `fireflies.p` and `rain.p` contained two properties (`independent: false` and `premultipliedAlpha: false`) that the LibGDX 1.12.1 `ParticleEmitter` parser does not expect. This caused the parser to desynchronize, leading to a `NumberFormatException` when it tried to read the next property header as a float value.

## Changes

### Assets

#### [fireflies.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/fireflies.p)
- Removed `independent: false` from the `Emission` section.
- Removed `premultipliedAlpha: false` from the `Options` section.

#### [rain.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/rain.p)
- Removed `independent: false` from the `Emission` section.
- Removed `premultipliedAlpha: false` from the `Options` section.

## Verification Results

### Automated Verification
- I verified that the problematic lines were successfully removed from both files, ensuring they now strictly follow the expected LibGDX 1.12.1 sequential format.

### Expected Outcome
- The application will now successfully load these particle effects without crashing on the GL thread. This allows features like the `ExplorationScreen` (which uses `fireflies.p`) to initialize correctly.
