# Implementation Plan - Fix Build Errors

The project currently fails to build due to SDK version mismatches and a Kotlin declaration clash in the `AudioManager` class.

## User Review Required

> [!IMPORTANT]
> I am proposing to downgrade `androidx.core:core-ktx` and other AndroidX libraries to versions compatible with `compileSdk 34`. If you prefer to upgrade the project to a newer SDK (e.g., 37), please let me know. However, SDK 37 is very recent/preview, so downgrading is recommended for stability.

## Proposed Changes

### Core Module

#### [MODIFY] [AudioManager.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/audio/AudioManager.kt)
- Convert `setMusicVolume(vol: Float)` logic into a custom setter for the `musicVolume` property.
- Remove the `setMusicVolume` function to resolve the JVM signature clash.

#### [MODIFY] [UIScreens.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/core/src/main/java/com/astralya/screens/UIScreens.kt)
- Replace calls to `setMusicVolume(value)` with property assignment `musicVolume = value`.

### Android Module

#### [MODIFY] [build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/build.gradle.kts)
- Downgrade `androidx.core:core-ktx` from `1.19.0` to `1.15.0`.
- Downgrade `androidx.lifecycle` libraries to `2.8.7` (compatible with SDK 34/35).
- Ensure consistency in other AndroidX dependencies if necessary.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify the project builds successfully.
- Run `:core:test` to ensure audio logic remains sound (if applicable).

### Manual Verification
- Deploy the app to a device/emulator to verify audio volume control still works in the UI.
