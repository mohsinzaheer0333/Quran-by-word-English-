package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [BookmarkEntity::class, NoteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun noteDao(): NoteDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE bookmarks ADD COLUMN isMushafPage INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE bookmarks ADD COLUMN mushafPageNumber INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS `notes` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `title` TEXT NOT NULL,
            `content` TEXT NOT NULL,
            `surahNumber` INTEGER,
            `surahNameEnglish` TEXT,
            `ayahNumber` INTEGER,
            `timestamp` INTEGER NOT NULL
          )
          """.trimIndent()
        )
      }
    }

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "noor_quran_database"
        )
          .addMigrations(MIGRATION_1_2)
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
