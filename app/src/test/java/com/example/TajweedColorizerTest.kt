package com.example

import com.example.model.MushafToken
import com.example.ui.mushaf.TajweedColorizer
import com.example.ui.mushaf.TajweedPalettes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TajweedColorizerTest {

  private val creamPalette = TajweedPalettes.ClassicCream
  private val navyPalette = TajweedPalettes.MidnightNavy

  @Test
  fun colorizeWord_preservesExactOriginalText_bothScripts() {
    val samples = listOf(
      "الضَّآلِّيۡنَ",      // Indo-Pak Madd + Lam Shamsiyyah + Tafkheem
      "ٱلضَّآلِّينَ",      // Madinah Uthmani
      "يُنۡفِقُوۡنَۙ",      // Indo-Pak Ikhfa + Madd Arid
      "يُنفِقُونَ",        // Madinah Ikhfa
      "رَزَقۡنٰهُمۡ",       // Indo-Pak Qalqalah ق + Dagger Alif
      "رَزَقْنَـٰهُمْ",      // Madinah Qalqalah ق + Dagger Alif
      "كَفَرُوا۟",         // Madinah Silent Alif rounded zero
      "كَفَرُوۡا"          // Indo-Pak Silent Alif al-Fariqah
    )
    samples.forEach { word ->
      val annotated = TajweedColorizer.colorizeWord(word, creamPalette)
      assertEquals("Exact text must never mutate", word, annotated.text)
    }
  }

  @Test
  fun detectsMaddRed_inBothIndoPakAndMadinah() {
    val indopakMadd = TajweedColorizer.colorizeWord("الضَّآلِّيۡنَ", creamPalette, isBeforeAyahEnd = true)
    assertTrue(
      "Indo-Pak maddah word should contain Red Madd span",
      indopakMadd.spanStyles.any { it.item.color == creamPalette.madd }
    )

    val madinahMadd = TajweedColorizer.colorizeWord("ٱلضَّآلِّينَ", navyPalette, isBeforeAyahEnd = true)
    assertTrue(
      "Madinah maddah word should contain Navy Red Madd span",
      madinahMadd.spanStyles.any { it.item.color == navyPalette.madd }
    )
  }

  @Test
  fun detectsGhunnahAndIkhfaGreen_inBothScripts() {
    // Noon Mushaddadah
    val inna = TajweedColorizer.colorizeWord("إِنَّ", creamPalette)
    assertTrue(inna.spanStyles.any { it.item.color == creamPalette.ghunnah })

    // Indo-Pak Ikhfa (يُنۡفِقُوۡنَ)
    val indopakIkhfa = TajweedColorizer.colorizeWord("يُنۡفِقُوۡنَ", creamPalette)
    assertTrue(indopakIkhfa.spanStyles.any { it.item.color == creamPalette.ghunnah })

    // Cross-word Meem Sakinah Ikhfa Shafawi (هُم بِمُؤْمِنِينَ)
    val lineTokens = listOf(
      MushafToken.Word("هُم"),
      MushafToken.Word("بِمُؤْمِنِينَ"),
      MushafToken.AyahEnd(8)
    )
    val coloredLine = TajweedColorizer.colorizeLine(lineTokens, navyPalette)
    val hum = coloredLine[0]!!
    assertTrue(
      "Meem Sakinah before Ba across words should be Green",
      hum.spanStyles.any { it.item.color == navyPalette.ghunnah }
    )
  }

  @Test
  fun detectsQalqalahBlue_andPrioritizesOverTafkheemOnSukun() {
    // Indo-Pak قَبۡلِكَ (ب with jazm U+06E1) and رَزَقۡنٰهُمۡ (ق with jazm U+06E1)
    val qablika = TajweedColorizer.colorizeWord("قَبۡلِكَ", creamPalette)
    assertTrue("ب with jazm should be Qalqalah Blue", qablika.spanStyles.any { it.item.color == creamPalette.qalqalah })
    assertTrue("ق with fatha should be Tafkheem Purple", qablika.spanStyles.any { it.item.color == creamPalette.tafkheem })

    val razaqna = TajweedColorizer.colorizeWord("رَزَقۡنٰهُمۡ", navyPalette)
    assertTrue("ق with jazm should be Qalqalah Blue", razaqna.spanStyles.any { it.item.color == navyPalette.qalqalah })
  }

  @Test
  fun detectsSilentLettersGrey_inBothScripts() {
    // Madinah Hamzatul Wasl + Lam Shamsiyyah (ٱلصِّرَٰطَ)
    val madinahSilent = TajweedColorizer.colorizeWord("ٱلصِّرَٰطَ", creamPalette)
    assertTrue(madinahSilent.spanStyles.any { it.item.color == creamPalette.silent })

    // Madinah rounded zero Alif (كَفَرُوا۟)
    val kafaruMadinah = TajweedColorizer.colorizeWord("كَفَرُوا۟", creamPalette)
    assertTrue(kafaruMadinah.spanStyles.any { it.item.color == creamPalette.silent })

    // Indo-Pak silent Alif al-Fariqah (كَفَرُوۡا)
    val kafaruIndopak = TajweedColorizer.colorizeWord("كَفَرُوۡا", navyPalette)
    assertTrue(kafaruIndopak.spanStyles.any { it.item.color == navyPalette.silent })
  }
}
