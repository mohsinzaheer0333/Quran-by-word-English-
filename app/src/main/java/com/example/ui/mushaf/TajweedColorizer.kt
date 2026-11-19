package com.example.ui.mushaf

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.model.MushafPaperMode
import com.example.model.MushafToken

/**
 * Five standard Tajweed color categories used across both Indo-Pak (16-Line)
 * and Madinah (15-Line Uthmani) Mushaf views.
 */
enum class TajweedRule {
  MADD,      // Red: Elongation (2-6 counts)
  GHUNNAH,   // Green: Nasalization, Ikhfa & Idgham
  QALQALAH,  // Blue: Echoing bounce (ق ط ب ج د)
  TAFKHEEM,  // Purple: Heavy & Bold letters (خ ص ض غ ط ق ظ)
  SILENT     // Muted Grey: Unpronounced letters (Hamzatul Wasl, silent Alif/Lam)
}

data class TajweedPalette(
  val madd: Color,
  val ghunnah: Color,
  val qalqalah: Color,
  val tafkheem: Color,
  val silent: Color
) {
  fun colorFor(rule: TajweedRule): Color = when (rule) {
    TajweedRule.MADD -> madd
    TajweedRule.GHUNNAH -> ghunnah
    TajweedRule.QALQALAH -> qalqalah
    TajweedRule.TAFKHEEM -> tafkheem
    TajweedRule.SILENT -> silent
  }
}

object TajweedPalettes {
  // Classic Cream Paper (#FFFDF7) palette
  val ClassicCream = TajweedPalette(
    madd = Color(0xFFDC2626),     // Red
    ghunnah = Color(0xFF15803D),  // Green
    qalqalah = Color(0xFF2563EB), // Blue
    tafkheem = Color(0xFF9333EA), // Purple
    silent = Color(0xFF94A3B8)    // Muted Grey
  )

  // Midnight Navy (#071222) palette
  val MidnightNavy = TajweedPalette(
    madd = Color(0xFFF87171),     // Red
    ghunnah = Color(0xFF4ADE80),  // Green
    qalqalah = Color(0xFF60A5FA), // Blue
    tafkheem = Color(0xFFC084FC), // Purple
    silent = Color(0xFF64748B)    // Muted Grey
  )

  fun forPaperMode(mode: MushafPaperMode): TajweedPalette = when (mode) {
    MushafPaperMode.CLASSIC_CREAM -> ClassicCream
    MushafPaperMode.MIDNIGHT_NAVY -> MidnightNavy
  }
}

/**
 * Character & diacritic-level Tajweed rule engine supporting both Indo-Pak (text_indopak)
 * and Madinah (text_uthmani) orthographies.
 *
 * Strictly applies foreground color SpanStyles ONLY to complete base-letter + diacritic clusters,
 * preserving the exact original Unicode string and glyph metrics so line breaks, spacing,
 * and 16-line / 15-line page boundaries never shift.
 */
object TajweedColorizer {

  // Arabic diacritics & Quranic annotation combining marks
  private const val FATHATAN = '\u064B'
  private const val DAMMATAN = '\u064C'
  private const val KASRATAN = '\u064D'
  private const val FATHA = '\u064E'
  private const val DAMMA = '\u064F'
  private const val KASRA = '\u0650'
  private const val SHADDAH = '\u0651'
  private const val SUKUN = '\u0652'
  private const val MADDAH_ABOVE = '\u0653'
  private const val SUBSCRIPT_ALEF = '\u0656'
  private const val INVERTED_DAMMA = '\u0657'
  private const val DAGGER_ALEF = '\u0670'
  private const val HAMZATUL_WASL = '\u0671'
  private const val SMALL_HIGH_ROUNDED_ZERO = '\u06DF'
  private const val SMALL_HIGH_UPRIGHT_RECT_ZERO = '\u06E0'
  private const val SMALL_HIGH_KHAH_SUKUN = '\u06E1' // Indo-Pak jazm / sukun
  private const val SMALL_HIGH_MEEM_IQLAB = '\u06E2'
  private const val SMALL_HIGH_MADDA = '\u06E4'      // Indo-Pak maddah sign
  private const val SMALL_WAW = '\u06E5'
  private const val SMALL_YEH = '\u06E6'
  private const val SMALL_LOW_MEEM = '\u06ED'
  private const val OPEN_FATHATAN = '\u08F0'
  private const val OPEN_DAMMATAN = '\u08F1'
  private const val OPEN_KASRATAN = '\u08F2'
  private const val TATWEEL = '\u0640'

  private val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')
  private val TAFKHEEM_LETTERS = setOf('خ', 'ص', 'ض', 'غ', 'ط', 'ق', 'ظ')
  private val IKHFA_LETTERS = setOf(
    'ت', 'ث', 'ج', 'د', 'ذ', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ف', 'ق', 'ك',
    '\u06A9', // Indo-Pak Keheh
    '\u06AA'  // Indo-Pak Swash Kaf
  )
  private val IDGHAM_GHUNNAH_LETTERS = setOf('ي', 'ن', 'م', 'و', 'ى', '\u06CC')
  private val IDGHAM_SILENT_LETTERS = setOf('ل', 'ر')
  private val SUN_LETTERS = setOf('ت', 'ث', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ل', 'ن')
  private val STOP_SIGNS = setOf(
    '\u06D6', '\u06D7', '\u06D8', '\u06D9', '\u06DA', '\u06DB', '\u06DC', '\u06DE', '\u06E9', '\u0615'
  )

  // Izhar Mutlaq exceptions (Noon Sakinah + Waw/Ya inside the same word are NOT merged)
  private val IZHAR_MUTLAQ_ROOTS = listOf("دنيا", "بنيان", "صنوان", "قنوان")

  private data class LetterCluster(
    val start: Int,
    val end: Int,
    val base: Char,
    val marks: String
  ) {
    val hasShaddah: Boolean get() = SHADDAH in marks
    val hasSukun: Boolean get() = SUKUN in marks || SMALL_HIGH_KHAH_SUKUN in marks
    val hasShortVowel: Boolean get() = FATHA in marks || DAMMA in marks || KASRA in marks
    val hasTanween: Boolean
      get() = FATHATAN in marks || DAMMATAN in marks || KASRATAN in marks ||
        OPEN_FATHATAN in marks || OPEN_DAMMATAN in marks || OPEN_KASRATAN in marks
    val hasMaddah: Boolean get() = MADDAH_ABOVE in marks || SMALL_HIGH_MADDA in marks || base == 'آ'
    val hasSpecialMaddMark: Boolean
      get() = DAGGER_ALEF in marks || SUBSCRIPT_ALEF in marks || INVERTED_DAMMA in marks ||
        base == SMALL_WAW || base == SMALL_YEH
    val hasIqlabMeem: Boolean get() = SMALL_HIGH_MEEM_IQLAB in marks || SMALL_LOW_MEEM in marks
    val hasSilentZero: Boolean
      get() = SMALL_HIGH_ROUNDED_ZERO in marks || SMALL_HIGH_UPRIGHT_RECT_ZERO in marks
    val isBareConsonant: Boolean
      get() = !hasShortVowel && !hasTanween && !hasShaddah && !hasSukun && !hasMaddah && !hasSpecialMaddMark
    val isNoonSakinah: Boolean
      get() = base == 'ن' && (hasSukun || isBareConsonant) && !hasShaddah && !hasShortVowel && !hasTanween
    val isMeemSakinah: Boolean
      get() = base == 'م' && (hasSukun || isBareConsonant) && !hasShaddah && !hasShortVowel && !hasTanween
    val isPronouncedArabicLetter: Boolean
      get() = base != HAMZATUL_WASL && base != TATWEEL && base !in STOP_SIGNS &&
        base != SMALL_WAW && base != SMALL_YEH && !base.isWhitespace() &&
        base in '\u0621'..'\u06D5'
  }

  /**
   * Colorizes every [MushafToken.Word] on a line while respecting cross-word Tajweed rules
   * (such as Noon Sakinah / Tanween + next word's initial letter, Meem Sakinah + next word,
   * and Madd Arid lil-Sukun at ayah endings).
   */
  fun colorizeLine(
    tokens: List<MushafToken>,
    palette: TajweedPalette
  ): Map<Int, AnnotatedString> {
    val result = HashMap<Int, AnnotatedString>(tokens.size)
    for (i in tokens.indices) {
      val token = tokens[i] as? MushafToken.Word ?: continue
      val nextToken = tokens.getOrNull(i + 1)
      val prevToken = tokens.getOrNull(i - 1)
      val isBeforeAyahEnd = nextToken is MushafToken.AyahEnd
      val isAfterAyahEnd = prevToken == null || prevToken is MushafToken.AyahEnd
      val nextWordText = (nextToken as? MushafToken.Word)?.text
      result[i] = colorizeWord(
        word = token.text,
        palette = palette,
        nextWord = nextWordText,
        isBeforeAyahEnd = isBeforeAyahEnd,
        isLineOrAyahStart = isAfterAyahEnd
      )
    }
    return result
  }

  /**
   * Colorizes a multi-word standalone Arabic phrase (such as a Bismillah line).
   */
  fun colorizePhrase(
    phrase: String,
    palette: TajweedPalette
  ): AnnotatedString {
    val words = phrase.split(" ")
    return buildAnnotatedString {
      words.forEachIndexed { idx, w ->
        if (idx > 0) append(" ")
        append(
          colorizeWord(
            word = w,
            palette = palette,
            nextWord = words.getOrNull(idx + 1),
            isBeforeAyahEnd = idx == words.lastIndex,
            isLineOrAyahStart = idx == 0
          )
        )
      }
    }
  }

  /**
   * Colorizes a single Arabic word using authentic Tajweed rules.
   * Returned [AnnotatedString.text] is strictly equal to [word].
   */
  fun colorizeWord(
    word: String,
    palette: TajweedPalette,
    nextWord: String? = null,
    isBeforeAyahEnd: Boolean = false,
    isLineOrAyahStart: Boolean = false
  ): AnnotatedString {
    if (word.isEmpty()) return AnnotatedString(word)
    val clusters = segmentClusters(word)
    if (clusters.isEmpty()) return AnnotatedString(word)

    val nextWordClusters = if (!nextWord.isNullOrEmpty()) segmentClusters(nextWord) else emptyList()
    val nextWordFirstPronounced = nextWordClusters.firstOrNull { it.isPronouncedArabicLetter }
    val strippedSkeleton = buildString(clusters.size) {
      for (c in clusters) {
        if (c.base in '\u0621'..'\u064A' || c.base == '\u0671' || c.base == '\u06A9' || c.base == '\u06AA') {
          append(
            when (c.base) {
              '\u06A9', '\u06AA' -> 'ك'
              '\u06CC', 'ى' -> 'ي'
              else -> c.base
            }
          )
        }
      }
    }
    val isIzharMutlaq = IZHAR_MUTLAQ_ROOTS.any { strippedSkeleton.contains(it) }

    // Identify Madd Arid lil-Sukun cluster index (long ا/و/ي immediately before the final letter of an ayah)
    val maddAridIndex = if (isBeforeAyahEnd) findMaddAridClusterIndex(clusters) else -1

    val rules = arrayOfNulls<TajweedRule>(clusters.size)

    for (i in clusters.indices) {
      val c = clusters[i]
      if (c.base.isWhitespace() || c.base in STOP_SIGNS) continue

      val prev = clusters.getOrNull(i - 1)
      val next = clusters.getOrNull(i + 1)
      val nextPronouncedInWord = findNextPronounced(clusters, i + 1)

      // 1. MADD (Red): Explicit Maddah, Dagger Alif, Small Waw/Yeh, or Madd Arid at verse end
      if (c.hasMaddah || c.hasSpecialMaddMark || i == maddAridIndex) {
        rules[i] = TajweedRule.MADD
        // If Tatweel carries Dagger Alif / Maddah right after a letter, keep the mark colored
        continue
      }
      // Tatweel holding Dagger Alif or Maddah (e.g. الرَّحْمَـٰنِ in Madinah Uthmani)
      if (c.base == TATWEEL && (c.hasMaddah || c.hasSpecialMaddMark)) {
        rules[i] = TajweedRule.MADD
        continue
      }

      // 2. SILENT LETTERS (Muted Grey)
      // 2a. Explicit rounded zero (U+06DF) or upright zero (U+06E0) in Uthmani (e.g., كَفَرُوا۟, أُو۟لَٰٓئِكَ)
      if (c.hasSilentZero) {
        rules[i] = TajweedRule.SILENT
        continue
      }

      // 2b. Hamzatul Wasl (ٱ) in Madinah Uthmani
      if (c.base == HAMZATUL_WASL) {
        rules[i] = TajweedRule.SILENT
        continue
      }

      // 2c. Silent Lam Shamsiyyah (bare ل before a Sun letter with Shaddah, e.g., الرَّحْمَٰنِ, الصِّرَاطَ, النَّاسِ)
      if (c.base == 'ل' && c.isBareConsonant && next != null && next.base in SUN_LETTERS && next.hasShaddah) {
        rules[i] = TajweedRule.SILENT
        continue
      }

      // 2d. Silent Alif of definite article 'ال' in Indo-Pak when preceded by prefix (بِالۡ, وَالۡ, فَالۡ, كَالۡ)
      //     or mid-sentence without hamza/vowel before ل
      if (c.base == 'ا' && c.isBareConsonant && next != null && next.base == 'ل') {
        val afterPrefix = prev != null && prev.base in setOf('و', 'ف', 'ب', 'ك', 'ل')
        val midSentenceDefinite = i == 0 && !isLineOrAyahStart && (next.hasSukun || next.isBareConsonant)
        if (afterPrefix || midSentenceDefinite) {
          rules[i] = TajweedRule.SILENT
          continue
        }
      }

      // 2e. Silent trailing Alif al-Fariqah after plural Waw in Indo-Pak (e.g., كَفَرُوۡا, اٰمَنُوۡا, عَمِلُوا)
      if (c.base == 'ا' && c.isBareConsonant && prev != null && prev.base == 'و' && isWordEnd(clusters, i)) {
        rules[i] = TajweedRule.SILENT
        continue
      }

      // 2f. Silent Waw in أُولَٰئِكَ / اُولٰٓٮِٕكَ (bare و after damma before ل)
      if (c.base == 'و' && c.isBareConsonant && prev != null && DAMMA in prev.marks &&
        next != null && next.base == 'ل' && (next.hasSpecialMaddMark || next.hasMaddah)
      ) {
        rules[i] = TajweedRule.SILENT
        continue
      }

      // 3. GHUNNAH / IKHFA / IDGHAM / IQLAB (Green)
      // 3a. Noon Mushaddadah (نّ) & Meem Mushaddadah (مّ)
      if ((c.base == 'ن' || c.base == 'م') && c.hasShaddah) {
        rules[i] = TajweedRule.GHUNNAH
        continue
      }

      // 3b. Explicit Iqlab marker (small meem ۢ or ۭ)
      if (c.hasIqlabMeem) {
        rules[i] = TajweedRule.GHUNNAH
        continue
      }

      // 3c. Noon Sakinah rules (within word or across to next word)
      if (c.isNoonSakinah) {
        val target = nextPronouncedInWord ?: nextWordFirstPronounced
        if (target != null) {
          val isCrossWord = nextPronouncedInWord == null
          when {
            target.base == 'ب' -> {
              rules[i] = TajweedRule.GHUNNAH
              continue
            }
            target.base in IKHFA_LETTERS -> {
              rules[i] = TajweedRule.GHUNNAH
              continue
            }
            target.base in IDGHAM_GHUNNAH_LETTERS && (isCrossWord || !isIzharMutlaq) -> {
              rules[i] = TajweedRule.GHUNNAH
              continue
            }
            target.base in IDGHAM_SILENT_LETTERS && isCrossWord -> {
              // Idgham bila Ghunnah (Noon assimilated silently into Lam or Ra)
              rules[i] = TajweedRule.SILENT
              continue
            }
          }
        }
      }

      // 3d. Tanween rules (looking ahead to next word's first pronounced letter)
      if (c.hasTanween) {
        val target = nextWordFirstPronounced
        if (target != null) {
          when {
            target.base == 'ب' || target.base in IKHFA_LETTERS || target.base in IDGHAM_GHUNNAH_LETTERS -> {
              rules[i] = TajweedRule.GHUNNAH
              continue
            }
            target.base in IDGHAM_SILENT_LETTERS -> {
              rules[i] = TajweedRule.GHUNNAH
              continue
            }
          }
        }
      }

      // 3e. Meem Sakinah rules: Ikhfa Shafawi (before ب) & Idgham Shafawi (before م)
      if (c.isMeemSakinah) {
        val target = nextPronouncedInWord ?: nextWordFirstPronounced
        if (target != null && (target.base == 'ب' || target.base == 'م')) {
          rules[i] = TajweedRule.GHUNNAH
          continue
        }
      }

      // 4. QALQALAH (Blue): ق ط ب ج د with Sukun (or silent at pause before AyahEnd)
      if (c.base in QALQALAH_LETTERS && (c.hasSukun || (c.isBareConsonant && isWordEnd(clusters, i)))) {
        // Avoid false Qalqalah when assimilated into identical following letter (e.g., اضْرِب بِّعَصَاكَ)
        val target = nextPronouncedInWord ?: nextWordFirstPronounced
        val isAssimilated = target != null && target.base == c.base && target.hasShaddah
        if (!isAssimilated) {
          rules[i] = TajweedRule.QALQALAH
          continue
        }
      }

      // 5. TAFKHEEM (Purple): Heavy letters (خ ص ض غ ط ق ظ)
      if (c.base in TAFKHEEM_LETTERS) {
        rules[i] = TajweedRule.TAFKHEEM
        continue
      }
    }

    return buildAnnotatedString {
      append(word)
      for (i in clusters.indices) {
        val rule = rules[i] ?: continue
        val cluster = clusters[i]
        addStyle(
          style = SpanStyle(color = palette.colorFor(rule)),
          start = cluster.start,
          end = cluster.end
        )
      }
    }
  }

  private fun isCombiningMark(ch: Char): Boolean {
    val type = Character.getType(ch)
    if (type == Character.NON_SPACING_MARK.toInt() ||
      type == Character.ENCLOSING_MARK.toInt() ||
      type == Character.COMBINING_SPACING_MARK.toInt()
    ) {
      return true
    }
    return ch in '\u064B'..'\u065F' ||
      ch == DAGGER_ALEF ||
      ch in '\u06DF'..'\u06E4' ||
      ch in '\u06E7'..'\u06ED' ||
      ch in '\u08F0'..'\u08F3'
  }

  private fun segmentClusters(text: String): List<LetterCluster> {
    val list = ArrayList<LetterCluster>(text.length)
    var i = 0
    while (i < text.length) {
      val base = text[i]
      val start = i
      i++
      val marksStart = i
      while (i < text.length && isCombiningMark(text[i])) {
        i++
      }
      val marks = if (i > marksStart) text.substring(marksStart, i) else ""
      list.add(LetterCluster(start = start, end = i, base = base, marks = marks))
    }
    return list
  }

  private fun findNextPronounced(clusters: List<LetterCluster>, fromIndex: Int): LetterCluster? {
    for (j in fromIndex until clusters.size) {
      val candidate = clusters[j]
      if (candidate.isPronouncedArabicLetter) return candidate
    }
    return null
  }

  private fun isWordEnd(clusters: List<LetterCluster>, index: Int): Boolean {
    for (j in (index + 1) until clusters.size) {
      val c = clusters[j]
      if (!c.base.isWhitespace() && c.base !in STOP_SIGNS) return false
    }
    return true
  }

  /**
   * Detects Madd Arid lil-Sukun (elongated ي / و / ا immediately preceding the final pronounced
   * consonant of a verse-ending word, e.g. الرَّحِيمِ, الْعَالَمِينَ, الْمُفْلِحُونَ, يُؤْمِنُونَ).
   */
  private fun findMaddAridClusterIndex(clusters: List<LetterCluster>): Int {
    // Find last pronounced letter
    var lastLetterIdx = -1
    for (j in clusters.lastIndex downTo 0) {
      if (clusters[j].isPronouncedArabicLetter) {
        lastLetterIdx = j
        break
      }
    }
    if (lastLetterIdx < 2) return -1

    val candidateIdx = lastLetterIdx - 1
    val candidate = clusters[candidateIdx]
    val before = clusters[candidateIdx - 1]

    val isLongYa = (candidate.base == 'ي' || candidate.base == 'ى' || candidate.base == '\u06CC') &&
      (candidate.isBareConsonant || candidate.hasSukun) &&
      KASRA in before.marks
    val isLongWaw = candidate.base == 'و' &&
      (candidate.isBareConsonant || candidate.hasSukun) &&
      DAMMA in before.marks
    val isLongAlif = candidate.base == 'ا' &&
      candidate.isBareConsonant &&
      FATHA in before.marks

    return if (isLongYa || isLongWaw || isLongAlif) candidateIdx else -1
  }
}
