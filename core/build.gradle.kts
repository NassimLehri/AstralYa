plugins {
    kotlin("jvm")
}

dependencies {
    val gdxVer = "1.12.1"
    val roomVersion = "2.8.4"
    api("com.badlogicgames.gdx:gdx:$gdxVer")
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVer")
    api("com.badlogicgames.gdx:gdx-freetype:$gdxVer")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

    // Room annotations pour que Core puisse définir les DAOs et Entities
    implementation("androidx.room:room-common:$roomVersion")

    // Tests unitaires (pas de dépendance Android)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.0")
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
