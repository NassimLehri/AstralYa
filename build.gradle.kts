plugins {
    id("com.android.application") version "9.3.0" apply false
    id("com.android.library") version "9.3.0" apply false
    id("com.android.legacy-kapt") version "9.3.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// TexturePacker task to generate atlases from assets/sprites -> assets/atlases
val gdxVersion: String by project

configurations.create("texturePacker")
dependencies {
    add("texturePacker", "com.badlogicgames.gdx:gdx-tools:$gdxVersion")
}

tasks.register<JavaExec>("texturePack") {
    group = "assets"
    description = "Pack textures into atlases (input: android/src/main/assets, output: android/src/main/assets/atlases)"
    classpath = configurations.getByName("texturePacker")
    mainClass.set("com.badlogic.gdx.tools.texturepacker.TexturePacker")
    // default args: inputDir outputDir packFileName
    args("--maxwidth=2048", "--maxheight=2048", "--multipack", "android/src/main/assets/sprites", "android/src/main/assets/atlases", "sprites")
}
