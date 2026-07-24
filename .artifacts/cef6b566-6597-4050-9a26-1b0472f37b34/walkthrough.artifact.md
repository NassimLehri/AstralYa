# Walkthrough - Fix ParticleEmitter SpawnShape Crash

I fixed the `java.lang.IllegalArgumentException: No enum constant ... SpawnShape.rectangle` crash that occurred when loading particle effects.

## Changes Made

### Particle Effects
I updated the following particle definition files to use the correct LibGDX enum constant `square` instead of `rectangle` for the `SpawnShape`.

- [fireflies.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/fireflies.p)
- [rain.p](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/src/main/assets/particles/rain.p)

## Verification Results

### Automated Tests
- Ran `grep` to ensure no other occurrences of `shape: rectangle` exist in the project assets.

### Manual Verification
- The crash was caused by `Enum.valueOf` failing to find `rectangle` in `ParticleEmitter.SpawnShape`. By changing it to `square` (which is the valid LibGDX value for a rectangular/square area), the `load` method will now succeed.
