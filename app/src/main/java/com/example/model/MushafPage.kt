package com.example.model

import androidx.compose.runtime.Immutable

enum class MushafLineType {
  TEXT,
  SURAH_HEADER,
  BISMILLAH,
  EMPTY
}

@Immutable
sealed interface MushafToken {
  @Immutable
  data class Word(val text: String) : MushafToken

  @Immutable
  data class AyahEnd(val ayahNumber: Int) : MushafToken
}

@Immutable
data class MushafLine(
  val lineNumber: Int,
  val type: MushafLineType,
  val tokens: List<MushafToken> = emptyList(),
  val surahNumber: Int = 0,
  val bannerSurahNumber: Int = 0,
  val hasRukuEnd: Boolean = false
)

@Immutable
data class MushafPageData(
  val pageNumber: Int,
  val juzNumber: Int,
  val primarySurahNumber: Int,
  val lines: List<MushafLine>
)

@Immutable
data class MushafEditionData(
  val script: MushafScript,
  val linesPerPage: Int,
  val totalPages: Int,
  val surahFirstPage: Map<Int, Int>,
  val juzFirstPage: Map<Int, Int>,
  val pages: List<MushafPageData>
) {
  fun getPage(pageNumber: Int): MushafPageData? {
    val idx = pageNumber - 1
    return pages.getOrNull(idx)
  }
}
