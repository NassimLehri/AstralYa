import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("com.android.legacy-kapt")
}

val natives = configurations.create("natives")

android {
    namespace = "com.astralya"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.astralya.gardiens"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            assets.directories += "src/main/assets"
            jniLibs.directories += "libs"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val gdxVersion = "1.12.1"
    val roomVersion = "2.8.4"

    implementation(project(":core"))

    // LibGDX Android
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")

    // Box2D
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64")

    // FreeType (fonts)
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")

    // Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    add("kapt", "androidx.room:room-compiler:$roomVersion")

    // Kotlin & Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}

fun DependencyHandlerScope.natives(dependency: Any) {
    "natives"(dependency)
    implementation(dependency)
}

tasks.register("copyNatives") {
    doFirst {
        val libsDir = file("libs")
        if (libsDir.exists()) libsDir.deleteRecursively()
        libsDir.mkdirs()
        natives.files.forEach { jar ->
            val abi = when {
                jar.name.contains("arm64-v8a") || jar.name.contains("arm64") -> "arm64-v8a"
                jar.name.contains("armeabi-v7a") -> "armeabi-v7a"
                jar.name.contains("x86_64") -> "x86_64"
                jar.name.contains("x86") -> "x86"
                else -> null
            }
            if (abi != null) {
                val outputDir = file("libs/$abi")
                copy {
                    from(zipTree(jar))
                    into(outputDir)
                    include("*.so")
                }
            }
        }
    }
}

tasks.matching { it.name.contains("package") && it.name.contains("Debug") }.configureEach {
    dependsOn("copyNatives")
}

