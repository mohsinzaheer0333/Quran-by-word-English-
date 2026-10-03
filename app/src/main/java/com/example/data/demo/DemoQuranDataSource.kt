package com.example.data.demo

import com.example.model.Ayah
import com.example.model.WordTranslation

/**
 * Isolated development/preview sample data ONLY.
 * This is NEVER referenced by the production Quran reading path or repository.
 */
object DemoQuranDataSource {
  val samplePreviewAyah = Ayah(
    surahNumber = 1,
    ayahNumber = 1,
    surahNameArabic = "الفاتحة",
    surahNameEnglish = "Al-Fatihah",
    juzNumber = 1,
    arabicText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
    englishTranslation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
    words = listOf(
      WordTranslation(1, 1, "بِسْمِ", "In (the) name", "bis'mi"),
      WordTranslation(2, 2, "ٱللَّهِ", "(of) Allah", "l-lahi"),
      WordTranslation(3, 3, "ٱلرَّحْمَـٰنِ", "the Entirely Merciful", "l-raḥmāni"),
      WordTranslation(4, 4, "ٱلرَّحِيمِ", "the Especially Merciful", "l-raḥīmi")
    )
  )
}
