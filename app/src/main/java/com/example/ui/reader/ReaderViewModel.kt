package com.example.ui.reader

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.QuranRepository
import com.example.model.Ayah
import com.example.model.JuzInfo
import com.example.model.LastReadPosition
import com.example.model.ReadingMode
import com.example.model.TranslationLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class ReaderUiState(
  val currentJuz: JuzInfo? = null,
  val ayahs: List<Ayah> = emptyList(),
  val bookmarkedVerseKeys: Set<String> = emptySet(),
  val readingMode: ReadingMode = ReadingMode.CONTINUOUS,
  val isAutoScrollActive: Boolean = false,
  val isAutoScrollPlaying: Boolean = true,
  val autoScrollSpeed: Float = 5.0f,
  val arabicFontSize: Float = 28f,
  val translationFontSize: Float = 15f,
  val selectedLanguage: String = "en",
  val currentAyahIndex: Int = 0,
  val isLoading: Boolean = true
)

class ReaderViewModel(
  private val quranRepository: QuranRepository,
  private val bookmarkRepository: BookmarkRepository,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(ReaderUiState())
  val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

  val allBookmarks: StateFlow<List<BookmarkEntity>> = bookmarkRepository.allBookmarks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val availableLanguages: List<TranslationLanguage> = quranRepository.getSupportedLanguages()

  private var lastReadJob: Job? = null

  init {
    viewModelScope.launch {
      combine(
        preferencesRepository.readingMode,
        preferencesRepository.arabicFontSize,
        preferencesRepository.translationFontSize,
        preferencesRepository.autoScrollSpeed,
        preferencesRepository.selectedLanguage
      ) { mode, arSize, trSize, scrollSpeed, lang ->
        _uiState.update { current ->
          current.copy(
            readingMode = mode,
            arabicFontSize = arSize,
            translationFontSize = trSize,
            autoScrollSpeed = scrollSpeed,
            selectedLanguage = lang
          )
        }
      }.collect {}
    }

    viewModelScope.launch {
      bookmarkRepository.allBookmarks.collect { bookmarks ->
        val bookmarkKeys = bookmarks.map { "${it.surahNumber}:${it.ayahNumber}" }.toSet()
        _uiState.update { current ->
          current.copy(bookmarkedVerseKeys = bookmarkKeys)
        }
      }
    }
  }

  fun loadJuz(juzNumber: Int, initialAyahIndex: Int = 0) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      val currentLang = preferencesRepository.selectedLanguage.value
      
      // Load data strictly off the main UI thread with active language
      val juz = withContext(Dispatchers.IO) {
        quranRepository.getJuzByNumber(juzNumber)
      }
      val ayahs = withContext(Dispatchers.IO) {
        quranRepository.getAyahsForJuz(juzNumber, currentLang)
      }
      val targetIndex = initialAyahIndex.coerceIn(0, (ayahs.size - 1).coerceAtLeast(0))

      _uiState.update {
        it.copy(
          currentJuz = juz,
          ayahs = ayahs,
          selectedLanguage = currentLang,
          currentAyahIndex = targetIndex,
          isLoading = false
        )
      }

      if (ayahs.isNotEmpty()) {
        val currentAyah = ayahs[targetIndex]
        viewModelScope.launch(Dispatchers.IO) {
          updateLastRead(juzNumber, currentAyah)
        }
      }
    }
  }

  fun setReadingMode(mode: ReadingMode) {
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setReadingMode(mode)
    }
    _uiState.update { it.copy(readingMode = mode) }
  }

  fun setArabicFontSize(size: Float) {
    val clamped = size.coerceIn(20f, 44f)
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setArabicFontSize(clamped)
    }
    _uiState.update { it.copy(arabicFontSize = clamped) }
  }

  fun setTranslationFontSize(size: Float) {
    val clamped = size.coerceIn(12f, 26f)
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setTranslationFontSize(clamped)
    }
    _uiState.update { it.copy(translationFontSize = clamped) }
  }

  fun setLanguage(languageCode: String) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        preferencesRepository.setSelectedLanguage(languageCode)
      }
      _uiState.update { it.copy(selectedLanguage = languageCode) }
      
      // Refresh currently loaded Juz in the new language immediately
      val juzNum = _uiState.value.currentJuz?.number ?: 1
      val updatedAyahs = withContext(Dispatchers.IO) {
        quranRepository.getAyahsForJuz(juzNum, languageCode)
      }
      _uiState.update { it.copy(ayahs = updatedAyahs) }
    }
  }

  fun toggleAutoScroll() {
    val next = !_uiState.value.isAutoScrollActive
    _uiState.value = _uiState.value.copy(
      isAutoScrollActive = next,
      isAutoScrollPlaying = true
    )
  }

  fun toggleAutoScrollPlay() {
    _uiState.value = _uiState.value.copy(
      isAutoScrollPlaying = !_uiState.value.isAutoScrollPlaying
    )
  }

  fun pauseAutoScroll() {
    _uiState.update { it.copy(isAutoScrollPlaying = false) }
  }

  fun resumeAutoScroll() {
    _uiState.update { it.copy(isAutoScrollPlaying = true) }
  }

  fun decreaseAutoScrollSpeed() {
    val newSpeed = (_uiState.value.autoScrollSpeed - 1.0f).coerceAtLeast(1.0f)
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setAutoScrollSpeed(newSpeed)
    }
    _uiState.update { it.copy(autoScrollSpeed = newSpeed) }
  }

  fun increaseAutoScrollSpeed() {
    val newSpeed = (_uiState.value.autoScrollSpeed + 1.0f).coerceAtMost(10.0f)
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setAutoScrollSpeed(newSpeed)
    }
    _uiState.update { it.copy(autoScrollSpeed = newSpeed) }
  }

  fun setAutoScrollSpeed(speed: Float) {
    val clamped = speed.coerceIn(1.0f, 10.0f)
    viewModelScope.launch(Dispatchers.IO) {
      preferencesRepository.setAutoScrollSpeed(clamped)
    }
    _uiState.update { it.copy(autoScrollSpeed = clamped) }
  }

  fun closeAutoScroll() {
    _uiState.update {
      it.copy(
        isAutoScrollActive = false,
        isAutoScrollPlaying = false
      )
    }
  }

  fun toggleBookmark(ayah: Ayah) {
    viewModelScope.launch(Dispatchers.IO) {
      val isCurrentlyBookmarked = _uiState.value.bookmarkedVerseKeys.contains(ayah.verseKey)
      val entity = BookmarkEntity(
        juzNumber = ayah.juzNumber,
        surahNumber = ayah.surahNumber,
        surahNameArabic = ayah.surahNameArabic,
        surahNameEnglish = ayah.surahNameEnglish,
        ayahNumber = ayah.ayahNumber,
        arabicTextPreview = ayah.arabicText,
        translationPreview = ayah.englishTranslation
      )
      bookmarkRepository.toggleBookmark(entity, isCurrentlyBookmarked)
    }
  }

  fun onAyahVisible(index: Int) {
    val ayahs = _uiState.value.ayahs
    if (index in ayahs.indices && index != _uiState.value.currentAyahIndex) {
      _uiState.value = _uiState.value.copy(currentAyahIndex = index)
      
      // Debounce saving last read position to IO thread to prevent UI thread stutter while scrolling
      lastReadJob?.cancel()
      lastReadJob = viewModelScope.launch(Dispatchers.IO) {
        delay(600L) // Wait until scroll motion settles
        val ayah = ayahs.getOrNull(index) ?: return@launch
        updateLastRead(_uiState.value.currentJuz?.number ?: 1, ayah)
      }
    }
  }

  private fun updateLastRead(juzNumber: Int, ayah: Ayah) {
    preferencesRepository.saveLastRead(
      LastReadPosition(
        juzNumber = juzNumber,
        surahNumber = ayah.surahNumber,
        surahNameArabic = ayah.surahNameArabic,
        surahNameEnglish = ayah.surahNameEnglish,
        ayahNumber = ayah.ayahNumber,
        readingMode = _uiState.value.readingMode,
        timestamp = System.currentTimeMillis()
      )
    )
  }
}
