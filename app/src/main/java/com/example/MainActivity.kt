package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.MushafRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.QuranRepositoryImpl
import com.example.model.ThemeMode
import com.example.ui.home.HomeViewModel
import com.example.ui.navigation.AppNavGraph
import com.example.ui.reader.ReaderViewModel
import com.example.ui.theme.NoorQuranTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext)
    val bookmarkRepository = BookmarkRepository(database.bookmarkDao())
    val noteRepository = NoteRepository(database.noteDao())
    val preferencesRepository = PreferencesRepository(applicationContext)
    val quranRepository = QuranRepositoryImpl(applicationContext)
    val mushafRepository = MushafRepository(applicationContext)

    val homeViewModel = HomeViewModel(
      quranRepository,
      mushafRepository,
      preferencesRepository,
      bookmarkRepository,
      noteRepository
    )
    val readerViewModel = ReaderViewModel(quranRepository, bookmarkRepository, preferencesRepository)

    setContent {
      val themeMode by preferencesRepository.themeMode.collectAsStateWithLifecycle()
      val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
      }
      val view = LocalView.current
      if (!view.isInEditMode) {
        SideEffect {
          val controller = WindowCompat.getInsetsController(window, view)
          controller.isAppearanceLightStatusBars = !isDark
          controller.isAppearanceLightNavigationBars = !isDark
        }
      }

      NoorQuranTheme(themeMode = themeMode) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val navController = rememberNavController()
          AppNavGraph(
            navController = navController,
            homeViewModel = homeViewModel,
            readerViewModel = readerViewModel,
            bookmarkRepository = bookmarkRepository,
            preferencesRepository = preferencesRepository
          )
        }
      }
    }
  }
}
