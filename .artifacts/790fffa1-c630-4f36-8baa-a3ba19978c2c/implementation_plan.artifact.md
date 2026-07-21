# Fix Gradle Sync Errors and Deprecations

The project is encountering two main issues during sync:
1.  **Duplicate 'run' task in `:lwjgl3`**: `Cannot add task 'run' as a task with that name already exists.`
2.  **Deprecated Kotlin plugin in `:android`**: The `org.jetbrains.kotlin.android` plugin is deprecated in AGP 9.0+, and the project is using legacy flags (`android.builtInKotlin=false`, `android.newDsl=false`) that should be removed.

## Proposed Changes

### [Global Configuration](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/gradle.properties)

#### [MODIFY] [gradle.properties](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/gradle.properties)
- Remove `android.builtInKotlin=false` and `android.newDsl=false` to enable AGP 9.0 defaults and built-in Kotlin support.

### [Android Module](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/android/build.gradle.kts)
- Remove `id("org.jetbrains.kotlin.android")` as it is now built into AGP.
- Replace `id("kotlin-kapt")` with `id("com.android.legacy-kapt")` as required for compatibility with built-in Kotlin when using Kapt.
- Update the `kotlin {}` block to use the latest DSL if necessary.

### [Desktop Module](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/lwjgl3)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/lwjgl3/build.gradle.kts)
- Ensure the `run` task is configured using `tasks.named<JavaExec>("run")` to avoid any attempt to re-register the task provided by the `application` plugin.
- Verify plugin application order: `kotlin("jvm")` should ideally be applied before `application`.

## Verification Plan

### Automated Tests
- Run `gradle_sync` to ensure the project syncs without errors.
- Run `./gradlew :android:assembleDebug` to verify the Android build still works with built-in Kotlin.
- Run `./gradlew :lwjgl3:run` (or assemble) to verify the desktop module.

### Manual Verification
- Check the "Build" output for any remaining deprecation warnings.
