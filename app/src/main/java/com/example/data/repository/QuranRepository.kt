package com.example.data.repository

import com.example.model.Ayah
import com.example.model.JuzInfo
import com.example.model.SurahInfo
import com.example.model.TranslationLanguage

interface QuranRepository {
  fun getAllJuz(): List<JuzInfo>
  fun getJuzByNumber(number: Int): JuzInfo?
  suspend fun getAyahsForJuz(juzNumber: Int, languageCode: String = "en"): List<Ayah>
  suspend fun getAyah(surahNumber: Int, ayahNumber: Int, languageCode: String = "en"): Ayah?
  suspend fun getAllSurahs(): List<SurahInfo>
  fun getSupportedLanguages(): List<TranslationLanguage>
}
