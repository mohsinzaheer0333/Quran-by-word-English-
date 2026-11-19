package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.NoteEntity
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.MushafRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.QuranRepository
import com.example.model.Ayah
import com.example.model.JuzInfo
import com.example.model.LastReadPosition
import com.example.model.MushafEditionData
import com.example.model.MushafLineType
import com.example.model.MushafPageData
import com.example.model.MushafPaperMode
import com.example.model.MushafScript
import com.example.model.MushafToken
import com.example.model.SurahInfo
import com.example.model.ThemeMode
import com.example.model.TranslationLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HomeTab(val label: String) {
  SURAH("Surah"),
  PARAH("Parah"),
  SEARCH("Search"),
  MUSHAF("Mushaf"),
  BOOKMARKS("Bookmarks")
}

class HomeViewModel(
  private val quranRepository: QuranRepository,
  private val mushafRepository: MushafRepository,
  private val preferencesRepository: PreferencesRepository,
  private val bookmarkRepository: BookmarkRepository,
  private val noteRepository: NoteRepository
) : ViewModel() {

  val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

  val selectedLanguage: StateFlow<String> = preferencesRepository.selectedLanguage
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

  val lastRead: StateFlow<LastReadPosition> = preferencesRepository.lastReadPosition
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LastReadPosition())

  val mushafScript: StateFlow<MushafScript> = preferencesRepository.mushafScript
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), preferencesRepository.mushafScript.value)

  val mushafPaperMode: StateFlow<MushafPaperMode> = preferencesRepository.mushafPaperMode
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), preferencesRepository.mushafPaperMode.value)

  val tajweedEnabled: StateFlow<Boolean> = preferencesRepository.tajweedEnabled
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), preferencesRepository.tajweedEnabled.value)

  val arabicFontSize: StateFlow<Float> = preferencesRepository.arabicFontSize
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 28f)

  val allBookmarks: StateFlow<List<BookmarkEntity>> = bookmarkRepository.allBookmarks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allNotes: StateFlow<List<NoteEntity>> = noteRepository.allNotes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val juzList: List<JuzInfo> = quranRepository.getAllJuz()
  val availableLanguages: List<TranslationLanguage> = quranRepository.getSupportedLanguages()

  private val _selectedTab = MutableStateFlow(HomeTab.SURAH)
  val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()

  private val _surahList = MutableStateFlow<List<SurahInfo>>(emptyList())
  val surahList: StateFlow<List<SurahInfo>> = _surahList.asStateFlow()

  private val _filterQuery = MutableStateFlow("")
  val filterQuery: StateFlow<String> = _filterQuery.asStateFlow()

  // Full Quran keyword search state (Search tab)
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _searchResults = MutableStateFlow<List<Ayah>>(emptyList())
  val searchResults: StateFlow<List<Ayah>> = _searchResults.asStateFlow()

  private val _isSearching = MutableStateFlow(false)
  val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

  // Dedicated Arabic-only Mushaf page-by-page state (Mushaf tab)
  private val _mushafEditionData = MutableStateFlow<MushafEditionData?>(null)
  val mushafEditionData: StateFlow<MushafEditionData?> = _mushafEditionData.asStateFlow()

  private val _currentMushafPage = MutableStateFlow(savedPageFor(preferencesRepository.mushafScript.value))
  val currentMushafPage: StateFlow<Int> = _currentMushafPage.asStateFlow()

  private val _isMushafLoading = MutableStateFlow(false)
  val isMushafLoading: StateFlow<Boolean> = _isMushafLoading.asStateFlow()

  private var searchJob: Job? = null
  private var mushafLoadJob: Job? = null

  init {
    viewModelScope.launch {
      _surahList.value = quranRepository.getAllSurahs()
    }
    viewModelScope.launch {
      preferencesRepository.mushafScript.collect { script ->
        loadMushafEdition(script)
      }
    }
  }

  private fun savedPageFor(script: MushafScript): Int {
    return when (script) {
      MushafScript.INDOPAK -> preferencesRepository.mushafPageIndopak.value
      MushafScript.UTHMANI -> preferencesRepository.mushafPageMadinah.value
    }
  }

  private fun loadMushafEdition(script: MushafScript, overridePage: Int? = null) {
    mushafLoadJob?.cancel()
    mushafLoadJob = viewModelScope.launch {
      _isMushafLoading.value = true
      val edition = mushafRepository.getEdition(script)
      val targetPage = (overridePage ?: savedPageFor(script)).coerceIn(1, edition.totalPages)
      _mushafEditionData.value = edition
      _currentMushafPage.value = targetPage
      _isMushafLoading.value = false
    }
  }

  fun selectTab(tab: HomeTab) {
    _selectedTab.value = tab
    if (tab == HomeTab.MUSHAF && _mushafEditionData.value == null && !_isMushafLoading.value) {
      loadMushafEdition(preferencesRepository.mushafScript.value)
    }
  }

  fun updateFilterQuery(query: String) {
    _filterQuery.value = query
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
    searchJob?.cancel()
    val trimmed = query.trim()
    if (trimmed.length < 2) {
      _searchResults.value = emptyList()
      _isSearching.value = false
      return
    }
    _isSearching.value = true
    searchJob = viewModelScope.launch {
      delay(240L)
      val results = quranRepository.searchQuran(trimmed, limit = 80)
      _searchResults.value = results
      _isSearching.value = false
    }
  }

  fun setMushafPage(pageNumber: Int) {
    val activeScript = preferencesRepository.mushafScript.value
    val maxPages = _mushafEditionData.value?.totalPages
      ?: if (activeScript == MushafScript.INDOPAK) 548 else 604
    val clamped = pageNumber.coerceIn(1, maxPages)
    _currentMushafPage.value = clamped
    preferencesRepository.setMushafPage(activeScript, clamped)
  }

  fun setMushafPaperMode(mode: MushafPaperMode) {
    preferencesRepository.setMushafPaperMode(mode)
  }

  fun toggleTajweedEnabled() {
    preferencesRepository.setTajweedEnabled(!preferencesRepository.tajweedEnabled.value)
  }

  fun openMushafBookmark(pageNumber: Int, surahNumber: Int) {
    _selectedTab.value = HomeTab.MUSHAF
    val activeScript = preferencesRepository.mushafScript.value
    val loadedEdition = _mushafEditionData.value
    if (loadedEdition != null && loadedEdition.script == activeScript) {
      val resolvedPage = if (pageNumber in 1..loadedEdition.totalPages) {
        pageNumber
      } else {
        loadedEdition.surahFirstPage[surahNumber] ?: 1
      }
      setMushafPage(resolvedPage)
    } else {
      mushafLoadJob?.cancel()
      mushafLoadJob = viewModelScope.launch {
        _isMushafLoading.value = true
        val edition = mushafRepository.getEdition(activeScript)
        val resolvedPage = if (pageNumber in 1..edition.totalPages) {
          pageNumber
        } else {
          edition.surahFirstPage[surahNumber] ?: 1
        }
        _mushafEditionData.value = edition
        _currentMushafPage.value = resolvedPage
        preferencesRepository.setMushafPage(activeScript, resolvedPage)
        _isMushafLoading.value = false
      }
    }
  }

  fun toggleMushafPageBookmark(page: MushafPageData, surah: SurahInfo?) {
    viewModelScope.launch(Dispatchers.IO) {
      val existing = allBookmarks.value.find {
        it.isMushafPage && it.mushafPageNumber == page.pageNumber
      }
      if (existing != null) {
        bookmarkRepository.removeBookmark(existing.id)
      } else {
        val activeScript = preferencesRepository.mushafScript.value
        val editionLabel = if (activeScript == MushafScript.INDOPAK) {
          "Indo-Pak 16-Line"
        } else {
          "Madinah 15-Line"
        }
        val firstTextPreview = page.lines
          .firstOrNull { it.type == MushafLineType.TEXT && it.tokens.isNotEmpty() }
          ?.tokens
          ?.mapNotNull { token ->
            when (token) {
              is MushafToken.Word -> token.text
              is MushafToken.AyahEnd -> null
            }
          }
          ?.joinToString(" ")
          ?.takeIf { it.isNotBlank() }
          ?: surah?.nameArabic
          ?: "القرآن الكريم"

        val entity = BookmarkEntity(
          juzNumber = page.juzNumber,
          surahNumber = page.primarySurahNumber,
          surahNameArabic = surah?.nameArabic ?: "",
          surahNameEnglish = surah?.nameSimple ?: "Surah ${page.primarySurahNumber}",
          ayahNumber = 1,
          arabicTextPreview = firstTextPreview,
          translationPreview = "Mushaf Page ${page.pageNumber} ($editionLabel) • Juz ${page.juzNumber}",
          isMushafPage = true,
          mushafPageNumber = page.pageNumber
        )
        bookmarkRepository.toggleBookmark(entity, currentlyBookmarked = false)
      }
    }
  }

  fun deleteBookmark(id: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      bookmarkRepository.removeBookmark(id)
    }
  }

  fun saveNote(
    id: Long = 0L,
    title: String,
    content: String,
    surahNumber: Int? = null,
    surahNameEnglish: String? = null,
    ayahNumber: Int? = null
  ) {
    if (title.isBlank() && content.isBlank()) return
    viewModelScope.launch(Dispatchers.IO) {
      noteRepository.saveNote(
        NoteEntity(
          id = id,
          title = title.trim().ifBlank { "Untitled Reflection" },
          content = content.trim(),
          surahNumber = surahNumber,
          surahNameEnglish = surahNameEnglish,
          ayahNumber = ayahNumber,
          timestamp = System.currentTimeMillis()
        )
      )
    }
  }

  fun deleteNote(id: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      noteRepository.deleteNote(id)
    }
  }

  fun toggleTheme() {
    viewModelScope.launch {
      val next = when (themeMode.value) {
        ThemeMode.LIGHT -> ThemeMode.DARK
        ThemeMode.DARK -> ThemeMode.LIGHT
        ThemeMode.SYSTEM -> ThemeMode.DARK
      }
      preferencesRepository.setThemeMode(next)
    }
  }

  fun setLanguage(languageCode: String) {
    preferencesRepository.setSelectedLanguage(languageCode)
  }

  fun setMushafScript(script: MushafScript) {
    preferencesRepository.setMushafScript(script)
  }

  fun adjustArabicFontSize(delta: Float) {
    val next = (arabicFontSize.value + delta).coerceIn(20f, 44f)
    preferencesRepository.setArabicFontSize(next)
  }
}
