package com.example.data.repository

import android.content.Context
import com.example.model.Ayah
import com.example.model.JuzInfo
import com.example.model.SurahInfo
import com.example.model.TranslationLanguage
import com.example.model.WordTranslation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

class QuranRepositoryImpl(private val context: Context) : QuranRepository {

  private val juzCache = ConcurrentHashMap<Int, List<Ayah>>()
  private var cachedSurahs: List<SurahInfo>? = null

  override fun getAllJuz(): List<JuzInfo> = all30JuzList

  override fun getJuzByNumber(number: Int): JuzInfo? =
    all30JuzList.find { it.number == number }

  override suspend fun getAyahsForJuz(juzNumber: Int, languageCode: String): List<Ayah> {
    val clampedJuz = juzNumber.coerceIn(1, 30)
    
    // Check in-memory cache first for instant retrieval
    juzCache[clampedJuz]?.let { return it }

    return withContext(Dispatchers.IO) {
      val ayahs = loadJuzFromAssets(clampedJuz)
      juzCache[clampedJuz] = ayahs
      ayahs
    }
  }

  override suspend fun getAyah(surahNumber: Int, ayahNumber: Int, languageCode: String): Ayah? {
    // Find which Juz this surah and ayah belong to
    val targetJuz = all30JuzList.find { juz ->
      if (surahNumber in juz.startSurahNumber..juz.endSurahNumber) {
        when {
          surahNumber == juz.startSurahNumber && surahNumber == juz.endSurahNumber ->
            ayahNumber in juz.startAyah..juz.endAyah
          surahNumber == juz.startSurahNumber -> ayahNumber >= juz.startAyah
          surahNumber == juz.endSurahNumber -> ayahNumber <= juz.endAyah
          else -> true
        }
      } else false
    } ?: return null

    val juzAyahs = getAyahsForJuz(targetJuz.number, languageCode)
    return juzAyahs.find { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
  }

  override suspend fun getAllSurahs(): List<SurahInfo> {
    cachedSurahs?.let { return it }
    return withContext(Dispatchers.IO) {
      val surahs = loadSurahsFromAssets()
      cachedSurahs = surahs
      surahs
    }
  }

  override fun getSupportedLanguages(): List<TranslationLanguage> {
    return listOf(
      TranslationLanguage("en", "English", "English", isAvailable = true),
      TranslationLanguage("ur", "Urdu", "اردو", isAvailable = false),
      TranslationLanguage("fr", "French", "Français", isAvailable = false),
      TranslationLanguage("es", "Spanish", "Español", isAvailable = false),
      TranslationLanguage("id", "Indonesian", "Bahasa Indonesia", isAvailable = false),
      TranslationLanguage("tr", "Turkish", "Türkçe", isAvailable = false)
    )
  }

  private fun loadJuzFromAssets(juzNumber: Int): List<Ayah> {
    val fileName = "quran/juz_$juzNumber.json"
    val jsonString = context.assets.open(fileName).use { inputStream ->
      BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
    }

    val jsonArray = JSONArray(jsonString)
    val result = ArrayList<Ayah>(jsonArray.length())

    for (i in 0 until jsonArray.length()) {
      val obj = jsonArray.getJSONObject(i)
      val wordsArray = obj.getJSONArray("words")
      val words = ArrayList<WordTranslation>(wordsArray.length())

      for (w in 0 until wordsArray.length()) {
        val wordObj = wordsArray.getJSONObject(w)
        words.add(
          WordTranslation(
            id = wordObj.optInt("id", w + 1),
            position = wordObj.optInt("position", w + 1),
            arabic = wordObj.optString("arabic", ""),
            english = wordObj.optString("english", ""),
            transliteration = wordObj.optString("transliteration", null)
          )
        )
      }

      result.add(
        Ayah(
          surahNumber = obj.getInt("surahNumber"),
          ayahNumber = obj.getInt("ayahNumber"),
          surahNameArabic = obj.getString("surahNameArabic"),
          surahNameEnglish = obj.getString("surahNameEnglish"),
          juzNumber = obj.getInt("juzNumber"),
          arabicText = obj.getString("arabicText"),
          englishTranslation = obj.getString("englishTranslation"),
          words = words
        )
      )
    }

    return result
  }

  private fun loadSurahsFromAssets(): List<SurahInfo> {
    val jsonString = context.assets.open("quran/surahs.json").use { inputStream ->
      BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
    }
    val jsonArray = JSONArray(jsonString)
    val result = ArrayList<SurahInfo>(jsonArray.length())
    for (i in 0 until jsonArray.length()) {
      val obj = jsonArray.getJSONObject(i)
      result.add(
        SurahInfo(
          number = obj.getInt("number"),
          nameSimple = obj.getString("nameSimple"),
          nameArabic = obj.getString("nameArabic"),
          nameComplex = obj.getString("nameComplex"),
          versesCount = obj.getInt("versesCount"),
          revelationPlace = obj.getString("revelationPlace")
        )
      )
    }
    return result
  }

  companion object {
    // Official canonical mapping of all 30 Juz with exact Surah boundaries and verse counts
    val all30JuzList = listOf(
      JuzInfo(1, "الم", "Alif Lam Meem", "Al-Fatihah", 1, 1, "Al-Baqarah", 2, 141, 148, "Al-Fatihah 1 - Al-Baqarah 141"),
      JuzInfo(2, "سَيَقُولُ", "Sayaqool", "Al-Baqarah", 2, 142, "Al-Baqarah", 2, 252, 111, "Al-Baqarah 142 - 252"),
      JuzInfo(3, "تِلْكَ الرُّسُلُ", "Tilka-r-Rusul", "Al-Baqarah", 2, 253, "Ali 'Imran", 3, 92, 126, "Al-Baqarah 253 - Ali 'Imran 92"),
      JuzInfo(4, "لَنْ تَنَالُوا", "Lan Tanaloo", "Ali 'Imran", 3, 93, "An-Nisa", 4, 23, 131, "Ali 'Imran 93 - An-Nisa 23"),
      JuzInfo(5, "وَالْمُحْصَنَاتُ", "Wal Mohsanat", "An-Nisa", 4, 24, "An-Nisa", 4, 147, 124, "An-Nisa 24 - 147"),
      JuzInfo(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", "An-Nisa", 4, 148, "Al-Ma'idah", 5, 81, 110, "An-Nisa 148 - Al-Ma'idah 81"),
      JuzInfo(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", "Al-Ma'idah", 5, 82, "Al-An'am", 6, 110, 149, "Al-Ma'idah 82 - Al-An'am 110"),
      JuzInfo(8, "وَلَوْ أَنَّنَا", "Wa Lau Annana", "Al-An'am", 6, 111, "Al-A'raf", 7, 87, 142, "Al-An'am 111 - Al-A'raf 87"),
      JuzInfo(9, "قَالَ الْمَلَأُ", "Qalal Mala'o", "Al-A'raf", 7, 88, "Al-Anfal", 8, 40, 159, "Al-A'raf 88 - Al-Anfal 40"),
      JuzInfo(10, "وَاعْلَمُوا", "Wa'lamoo", "Al-Anfal", 8, 41, "At-Tawbah", 9, 92, 127, "Al-Anfal 41 - At-Tawbah 92"),
      JuzInfo(11, "يَعْتَذِرُونَ", "Ya'taziroon", "At-Tawbah", 9, 93, "Hud", 11, 5, 151, "At-Tawbah 93 - Hud 5"),
      JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbah", "Hud", 11, 6, "Yusuf", 12, 52, 170, "Hud 6 - Yusuf 52"),
      JuzInfo(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", "Yusuf", 12, 53, "Ibrahim", 14, 52, 154, "Yusuf 53 - Ibrahim 52"),
      JuzInfo(14, "رُبَمَا", "Rubama", "Al-Hijr", 15, 1, "An-Nahl", 16, 128, 227, "Al-Hijr 1 - An-Nahl 128"),
      JuzInfo(15, "سُبْحَانَ الَّذِي", "Subhanallazi", "Al-Isra", 17, 1, "Al-Kahf", 18, 74, 185, "Al-Isra 1 - Al-Kahf 74"),
      JuzInfo(16, "قَالَ أَلَمْ", "Qala Alam", "Al-Kahf", 18, 75, "Ta-Ha", 20, 135, 269, "Al-Kahf 75 - Ta-Ha 135"),
      JuzInfo(17, "اقْتَرَبَ", "Iqtaraba", "Al-Anbiya", 21, 1, "Al-Hajj", 22, 78, 190, "Al-Anbiya 1 - Al-Hajj 78"),
      JuzInfo(18, "قَدْ أَفْلَحَ", "Qad Aflaha", "Al-Mu'minun", 23, 1, "Al-Furqan", 25, 20, 202, "Al-Mu'minun 1 - Al-Furqan 20"),
      JuzInfo(19, "وَقَالَ الَّذِينَ", "Wa Qalal Lazina", "Al-Furqan", 25, 21, "An-Naml", 27, 55, 339, "Al-Furqan 21 - An-Naml 55"),
      JuzInfo(20, "أَمَّنْ خَلَقَ", "Amman Khalaqa", "An-Naml", 27, 56, "Al-Ankabut", 29, 45, 171, "An-Naml 56 - Al-Ankabut 45"),
      JuzInfo(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", "Al-Ankabut", 29, 46, "Al-Ahzab", 33, 30, 178, "Al-Ankabut 46 - Al-Ahzab 30"),
      JuzInfo(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", "Al-Ahzab", 33, 31, "Ya-Sin", 36, 27, 169, "Al-Ahzab 31 - Ya-Sin 27"),
      JuzInfo(23, "وَمَا لِيَ", "Wa Maliya", "Ya-Sin", 36, 28, "Az-Zumar", 39, 31, 357, "Ya-Sin 28 - Az-Zumar 31"),
      JuzInfo(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", "Az-Zumar", 39, 32, "Fussilat", 41, 46, 175, "Az-Zumar 32 - Fussilat 46"),
      JuzInfo(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", "Fussilat", 41, 47, "Al-Jathiyah", 45, 37, 246, "Fussilat 47 - Al-Jathiyah 37"),
      JuzInfo(26, "حم", "Ha-Meem", "Al-Ahqaf", 46, 1, "Adh-Dhariyat", 51, 30, 195, "Al-Ahqaf 1 - Adh-Dhariyat 30"),
      JuzInfo(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", "Adh-Dhariyat", 51, 31, "Al-Hadid", 57, 29, 399, "Adh-Dhariyat 31 - Al-Hadid 29"),
      JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami' Allah", "Al-Mujadila", 58, 1, "At-Tahrim", 66, 12, 137, "Al-Mujadila 1 - At-Tahrim 12"),
      JuzInfo(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", "Al-Mulk", 67, 1, "Al-Mursalat", 77, 50, 431, "Al-Mulk 1 - Al-Mursalat 50"),
      JuzInfo(30, "عَمَّ يَتَسَاءَلُونَ", "Amma Yatasa'aloon", "An-Naba", 78, 1, "An-Nas", 114, 6, 564, "An-Naba 1 - An-Nas 6")
    )
  }
}
