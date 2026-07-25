package com.astralya.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.astralya.game.save.dao.*
import com.astralya.game.save.entities.*

@Database(
    entities = [
        HeroEntity::class,
        InventoryItemEntity::class,
        QuestEntity::class,
        GameSaveEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AstralYaDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun questDao(): QuestDao
    abstract fun saveDao(): SaveDao

    companion object {
        @Volatile
        private var INSTANCE: AstralYaDatabase? = null

        fun getDatabase(context: Context): AstralYaDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    android.util.Log.d("AstralYa", "Building Room database...")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AstralYaDatabase::class.java,
                        "astralya_database"
                    )
                        .fallbackToDestructiveMigration(true)
                        .build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    android.util.Log.e("AstralYa", "Failed to build persistent database, falling back to in-memory", e)
                    // Fallback sur une base de données en mémoire si le fichier est inaccessible
                    val inMemory = Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        AstralYaDatabase::class.java
                    ).build()
                    INSTANCE = inMemory
                    inMemory
                }
            }
        }
    }
}
