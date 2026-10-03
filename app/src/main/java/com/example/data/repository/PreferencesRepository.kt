package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LastReadPosition
import com.example.model.ReadingMode
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("noor_quran_prefs", Context.MODE_PRIVATE)

  private val _themeMode = MutableStateFlow(getStoredThemeMode())
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  private val _selectedLanguage = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "en") ?: "en")
  val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

  private val _readingMode = MutableStateFlow(getStoredReadingMode())
  val readingMode: StateFlow<ReadingMode> = _readingMode.asStateFlow()

  private val _arabicFontSize = MutableStateFlow(prefs.getFloat(KEY_ARABIC_FONT_SIZE, 28f))
  val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

  private val _translationFontSize = MutableStateFlow(prefs.getFloat(KEY_TRANS_FONT_SIZE, 16f))
  val translationFontSize: StateFlow<Float> = _translationFontSize.asStateFlow()

  private val _autoScrollSpeed = MutableStateFlow(prefs.getFloat(KEY_AUTO_SCROLL_SPEED, 1.0f))
  val autoScrollSpeed: StateFlow<Float> = _autoScrollSpeed.asStateFlow()

  private val _lastReadPosition = MutableStateFlow(getStoredLastRead())
  val lastReadPosition: StateFlow<LastReadPosition> = _lastReadPosition.asStateFlow()

  private fun getStoredThemeMode(): ThemeMode {
    val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
    return try {
      ThemeMode.valueOf(name)
    } catch (_: Exception) {
      ThemeMode.SYSTEM
    }
  }

  private fun getStoredReadingMode(): ReadingMode {
    val name = prefs.getString(KEY_READING_MODE, ReadingMode.CONTINUOUS.name) ?: ReadingMode.CONTINUOUS.name
    return try {
      ReadingMode.valueOf(name)
    } catch (_: Exception) {
      ReadingMode.CONTINUOUS
    }
  }

  private fun getStoredLastRead(): LastReadPosition {
    return LastReadPosition(
      juzNumber = prefs.getInt(KEY_LAST_JUZ, 1),
      surahNumber = prefs.getInt(KEY_LAST_SURAH, 1),
      surahNameArabic = prefs.getString(KEY_LAST_SURAH_ARABIC, "الفاتحة") ?: "الفاتحة",
      surahNameEnglish = prefs.getString(KEY_LAST_SURAH_ENGLISH, "Al-Fatihah") ?: "Al-Fatihah",
      ayahNumber = prefs.getInt(KEY_LAST_AYAH, 1),
      readingMode = getStoredReadingMode(),
      timestamp = prefs.getLong(KEY_LAST_TIME, System.currentTimeMillis())
    )
  }

  fun setThemeMode(mode: ThemeMode) {
    prefs.edit().putString(KEY_THEME, mode.name).apply()
    _themeMode.value = mode
  }

  fun setSelectedLanguage(languageCode: String) {
    prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    _selectedLanguage.value = languageCode
  }

  fun setReadingMode(mode: ReadingMode) {
    prefs.edit().putString(KEY_READING_MODE, mode.name).apply()
    _readingMode.value = mode
  }

  fun setArabicFontSize(size: Float) {
    prefs.edit().putFloat(KEY_ARABIC_FONT_SIZE, size).apply()
    _arabicFontSize.value = size
  }

  fun setTranslationFontSize(size: Float) {
    prefs.edit().putFloat(KEY_TRANS_FONT_SIZE, size).apply()
    _translationFontSize.value = size
  }

  fun setAutoScrollSpeed(speed: Float) {
    val clamped = speed.coerceIn(0.5f, 3.0f)
    prefs.edit().putFloat(KEY_AUTO_SCROLL_SPEED, clamped).apply()
    _autoScrollSpeed.value = clamped
  }

  fun saveLastRead(position: LastReadPosition) {
    prefs.edit()
      .putInt(KEY_LAST_JUZ, position.juzNumber)
      .putInt(KEY_LAST_SURAH, position.surahNumber)
      .putString(KEY_LAST_SURAH_ARABIC, position.surahNameArabic)
      .putString(KEY_LAST_SURAH_ENGLISH, position.surahNameEnglish)
      .putInt(KEY_LAST_AYAH, position.ayahNumber)
      .putLong(KEY_LAST_TIME, position.timestamp)
      .apply()
    _lastReadPosition.value = position
  }

  companion object {
    private const val KEY_THEME = "key_theme_mode"
    private const val KEY_LANGUAGE = "key_language"
    private const val KEY_READING_MODE = "key_reading_mode"
    private const val KEY_ARABIC_FONT_SIZE = "key_arabic_font_size"
    private const val KEY_TRANS_FONT_SIZE = "key_trans_font_size"
    private const val KEY_AUTO_SCROLL_SPEED = "key_auto_scroll_speed"
    private const val KEY_LAST_JUZ = "key_last_juz"
    private const val KEY_LAST_SURAH = "key_last_surah"
    private const val KEY_LAST_SURAH_ARABIC = "key_last_surah_arabic"
    private const val KEY_LAST_SURAH_ENGLISH = "key_last_surah_english"
    private const val KEY_LAST_AYAH = "key_last_ayah"
    private const val KEY_LAST_TIME = "key_last_time"
  }
}
