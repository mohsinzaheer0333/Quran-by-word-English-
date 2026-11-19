package com.example.model

enum class ThemeMode {
  LIGHT,
  DARK,
  SYSTEM
}

enum class ReadingMode {
  CONTINUOUS,
  SWIPE
}

enum class MushafScript(val label: String) {
  UTHMANI("Madinah (Uthmani)"),
  INDOPAK("Indo-Pak Script")
}

enum class MushafPaperMode(val label: String) {
  CLASSIC_CREAM("Classic Cream"),
  MIDNIGHT_NAVY("Midnight Navy")
}

data class TranslationLanguage(
  val code: String,
  val displayName: String,
  val nativeName: String,
  val isAvailable: Boolean = true
)

data class LastReadPosition(
  val juzNumber: Int = 1,
  val surahNumber: Int = 1,
  val surahNameArabic: String = "الفاتحة",
  val surahNameEnglish: String = "Al-Fatihah",
  val ayahNumber: Int = 1,
  val readingMode: ReadingMode = ReadingMode.CONTINUOUS,
  val timestamp: Long = System.currentTimeMillis()
)
