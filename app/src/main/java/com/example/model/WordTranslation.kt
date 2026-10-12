package com.example.model

import androidx.compose.runtime.Immutable

/**
 * Represents a single word of a Quranic Ayah with its Arabic text and translation.
 * Marked as @Immutable to enable Compose skipping optimizations.
 */
@Immutable
data class WordTranslation(
  val id: Int,
  val position: Int,
  val arabic: String,
  val english: String,
  val transliteration: String? = null
)
