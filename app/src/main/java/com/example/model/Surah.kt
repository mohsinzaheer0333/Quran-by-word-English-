package com.example.model

/**
 * Metadata for a Surah (Chapter) of the Holy Quran.
 */
data class SurahInfo(
  val number: Int,
  val nameSimple: String,
  val nameArabic: String,
  val nameComplex: String,
  val versesCount: Int,
  val revelationPlace: String
)
