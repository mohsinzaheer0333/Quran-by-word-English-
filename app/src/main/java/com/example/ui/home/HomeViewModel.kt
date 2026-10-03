package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.QuranRepository
import com.example.model.JuzInfo
import com.example.model.LastReadPosition
import com.example.model.ThemeMode
import com.example.model.TranslationLanguage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val juzList: List<JuzInfo> = emptyList(),
  val availableLanguages: List<TranslationLanguage> = emptyList(),
  val selectedLanguageCode: String = "en",
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val lastReadPosition: LastReadPosition = LastReadPosition()
)

class HomeViewModel(
  private val quranRepository: QuranRepository,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {

  val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

  val selectedLanguage: StateFlow<String> = preferencesRepository.selectedLanguage
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

  val lastRead: StateFlow<LastReadPosition> = preferencesRepository.lastReadPosition
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LastReadPosition())

  val juzList: List<JuzInfo> = quranRepository.getAllJuz()
  val availableLanguages: List<TranslationLanguage> = quranRepository.getSupportedLanguages()

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
}
