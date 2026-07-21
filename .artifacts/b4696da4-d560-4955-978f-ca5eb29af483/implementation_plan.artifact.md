# Implementation Plan - Fix App Installation and Build Configuration

The "Application non installée" error is likely caused by a conflict with an existing version of the app on the device or by Play Protect blocking the installation. Additionally, the `android/build.gradle.kts` file is missing critical configurations.

## Proposed Changes

### Android Module

#### [MODIFY] [build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/build.gradle.kts)
- Add `id("org.jetbrains.kotlin.android")` to the `plugins` block.
- Restore `signingConfigs` and associate it with the `release` build type.
- Set `compileSdk` and `targetSdk` to 35.

## User Actions Required

### 1. Uninstall Existing App
Before installing the new APK, **uninstall** any previous version of "Les Gardiens d'Astralya" from your device.

### 2. Bypass Play Protect
When installing, click **"Plus de détails"** then **"Installer quand même"** (Install anyway) in the Play Protect dialog. **Do not click OK**.

## Verification Plan

### Automated Tests
- Gradle Sync and `:android:assembleDebug`.
