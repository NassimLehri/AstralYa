# Implementation Plan - Fix 'kapt' Configuration Not Found

The project is using **Android Gradle Plugin (AGP) 9.3.0**, which introduces built-in Kotlin support enabled by default (`android.builtInKotlin=true` in `gradle.properties`). This change makes the standard `kotlin-android` plugin redundant and the standard `kotlin-kapt` plugin incompatible.

## Proposed Changes

### [Component Name] :android module

#### [MODIFY] [build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/build.gradle.kts)
- Remove `id("org.jetbrains.kotlin.android")`. Built-in Kotlin is now handled by AGP 9.0+.
- Replace `id("org.jetbrains.kotlin.kapt")` with `id("com.android.legacy-kapt")`. This is the bridge plugin required to use `kapt` with built-in Kotlin in AGP 9.0+.
- Keep the `add("kapt", ...)` dependency as is, as the `legacy-kapt` plugin will provide the necessary `kapt` configuration.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify the "Configuration with name 'kapt' not found" error is resolved.
- Run `./gradlew :android:assembleDebug` to ensure annotation processing (Room) works correctly.

### Manual Verification
- Verify that the Room DAOs and Entities are correctly processed by checking the generated code if possible, or simply by a successful build.
