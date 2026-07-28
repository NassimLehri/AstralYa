plugins {
    kotlin("jvm")
}

dependencies {
    val gdxVer = "1.14.2"
    val roomVersion = "2.8.4"
    api("com.badlogicgames.gdx:gdx:$gdxVer")
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVer")
    api("com.badlogicgames.gdx:gdx-freetype:$gdxVer")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

    // Room annotations pour que Core puisse définir les DAOs et Entities
    implementation("androidx.room:room-common:$roomVersion")

    // Injection de dépendances
    val koinVersion = "4.0.0"
    implementation("io.insert-koin:koin-core:$koinVersion")

    // Tests unitaires (pas de dépendance Android)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.0")

    // Headless backend for unit tests to avoid native LWJGL dependencies
    testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVer")

    // Add desktop native libraries at test runtime so texture/pixmap code can run during unit tests
    // Include per-platform native jars so CI runners can load native methods at test runtime
    testRuntimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVer:natives-desktop")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVer:natives-windows")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVer:natives-linux")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVer:natives-macos")

    testRuntimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVer:natives-desktop")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVer:natives-windows")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVer:natives-linux")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVer:natives-macos")

    testRuntimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVer:natives-desktop")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVer:natives-windows")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVer:natives-linux")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVer:natives-macos")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Lancer les tests : ./gradlew :core:test
tasks.test {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showStackTraces = true
    }
}
