package com.example.model

import androidx.compose.runtime.Immutable

/**
 * Metadata for a Quranic Juz (Para).
 */
@Immutable
data class JuzInfo(
  val number: Int,
  val arabicName: String,
  val transliteration: String,
  val startSurahName: String,
  val startSurahNumber: Int,
  val startAyah: Int,
  val endSurahName: String,
  val endSurahNumber: Int,
  val endAyah: Int,
  val totalVerses: Int,
  val surahsIncluded: String
)
