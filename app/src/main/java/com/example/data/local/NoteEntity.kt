package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val content: String,
  val surahNumber: Int? = null,
  val surahNameEnglish: String? = null,
  val ayahNumber: Int? = null,
  val timestamp: Long = System.currentTimeMillis()
)
