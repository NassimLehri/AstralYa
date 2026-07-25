import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    application
}

val gdxVersion = "1.12.1"

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("io.insert-koin:koin-core:4.0.0")
}

application {
    mainClass.set("com.astralya.lwjgl3.Lwjgl3LauncherKt")
}

sourceSets {
    main {
        resources.srcDirs("../android/src/main/assets")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.named<JavaExec>("run") {
    workingDir = file("../android/src/main/assets")
}
