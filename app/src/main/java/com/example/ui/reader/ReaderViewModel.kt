package com.example.ui.reader

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReaderUiState(
  val currentJuz: JuzInfo? = null,
  val ayahs: List<Ayah> = emptyList(),
  val bookmarkedVerseKeys: Set<String> = emptySet(),
  val readingMode: ReadingMode = ReadingMode.CONTINUOUS,
  val isAutoScrollActive: Boolean = false,
  val isAutoScrollPlaying: Boolean = true,
  val autoScrollSpeed: Float = 1.0f,
  val arabicFontSize: Float = 28f,
  val translationFontSize: Float = 15f,
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

  init {
    viewModelScope.launch {
      combine(
        preferencesRepository.readingMode,
        preferencesRepository.arabicFontSize,
        preferencesRepository.translationFontSize,
        preferencesRepository.autoScrollSpeed,
        bookmarkRepository.allBookmarks
      ) { mode, arSize, trSize, scrollSpeed, bookmarks ->
        val bookmarkKeys = bookmarks.map { "${it.surahNumber}:${it.ayahNumber}" }.toSet()
        _uiState.value.copy(
          readingMode = mode,
          arabicFontSize = arSize,
          translationFontSize = trSize,
          autoScrollSpeed = scrollSpeed,
          bookmarkedVerseKeys = bookmarkKeys
        )
      }.collect { updated ->
        _uiState.value = updated
      }
    }
  }

  fun loadJuz(juzNumber: Int, initialAyahIndex: Int = 0) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true)
      val juz = quranRepository.getJuzByNumber(juzNumber)
      val ayahs = quranRepository.getAyahsForJuz(juzNumber)
      val targetIndex = initialAyahIndex.coerceIn(0, (ayahs.size - 1).coerceAtLeast(0))

      _uiState.value = _uiState.value.copy(
        currentJuz = juz,
        ayahs = ayahs,
        currentAyahIndex = targetIndex,
        isLoading = false
      )

      if (ayahs.isNotEmpty()) {
        val currentAyah = ayahs[targetIndex]
        updateLastRead(juzNumber, currentAyah)
      }
    }
  }

  fun setReadingMode(mode: ReadingMode) {
    viewModelScope.launch {
      preferencesRepository.setReadingMode(mode)
      _uiState.value = _uiState.value.copy(readingMode = mode)
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

  fun decreaseAutoScrollSpeed() {
    val newSpeed = (_uiState.value.autoScrollSpeed - 0.25f).coerceAtLeast(0.5f)
    preferencesRepository.setAutoScrollSpeed(newSpeed)
    _uiState.value = _uiState.value.copy(autoScrollSpeed = newSpeed)
  }

  fun increaseAutoScrollSpeed() {
    val newSpeed = (_uiState.value.autoScrollSpeed + 0.25f).coerceAtMost(3.0f)
    preferencesRepository.setAutoScrollSpeed(newSpeed)
    _uiState.value = _uiState.value.copy(autoScrollSpeed = newSpeed)
  }

  fun closeAutoScroll() {
    _uiState.value = _uiState.value.copy(
      isAutoScrollActive = false,
      isAutoScrollPlaying = false
    )
  }

  fun toggleBookmark(ayah: Ayah) {
    viewModelScope.launch {
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
      val ayah = ayahs[index]
      updateLastRead(_uiState.value.currentJuz?.number ?: 1, ayah)
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
