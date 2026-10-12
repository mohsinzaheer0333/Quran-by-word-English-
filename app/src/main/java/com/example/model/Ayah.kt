package com.example.model

import androidx.compose.runtime.Immutable

/**
 * Represents a complete Ayah (verse) of the Quran.
 * Marked as @Immutable to enable Compose compiler skipping optimizations
 * and prevent layout thrashing during list scrolling.
 */
@Immutable
data class Ayah(
  val surahNumber: Int,
  val ayahNumber: Int,
  val surahNameArabic: String,
  val surahNameEnglish: String,
  val juzNumber: Int,
  val arabicText: String,
  val englishTranslation: String,
  val words: List<WordTranslation>,
  val isBookmarked: Boolean = false
) {
  val verseKey: String get() = "$surahNumber:$ayahNumber"
}
