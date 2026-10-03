package com.example.model

/**
 * Represents a single word of a Quranic Ayah with its Arabic text and translation.
 */
data class WordTranslation(
  val id: Int,
  val position: Int,
  val arabic: String,
  val english: String,
  val transliteration: String? = null
)
