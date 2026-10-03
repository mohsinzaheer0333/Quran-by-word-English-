package com.example.model

/**
 * Represents a complete Ayah (verse) of the Quran.
 */
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
