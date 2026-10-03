package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val juzNumber: Int,
  val surahNumber: Int,
  val surahNameArabic: String,
  val surahNameEnglish: String,
  val ayahNumber: Int,
  val arabicTextPreview: String,
  val translationPreview: String,
  val timestamp: Long = System.currentTimeMillis()
)
