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
