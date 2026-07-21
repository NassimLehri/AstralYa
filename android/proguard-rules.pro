# LibGDX
-keep class com.badlogic.gdx.** { *; }
-keep class com.badlogic.gdx.backends.android.** { *; }
-dontwarn com.badlogic.gdx.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# AstralYa game classes
-keep class com.astralya.** { *; }

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Général
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-dontobfuscate
