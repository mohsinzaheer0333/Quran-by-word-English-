package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.QuranRepositoryImpl
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
    val preferencesRepository = PreferencesRepository(applicationContext)
    val quranRepository = QuranRepositoryImpl(applicationContext)

    val homeViewModel = HomeViewModel(quranRepository, preferencesRepository)
    val readerViewModel = ReaderViewModel(quranRepository, bookmarkRepository, preferencesRepository)

    setContent {
      val themeMode by preferencesRepository.themeMode.collectAsStateWithLifecycle()

      NoorQuranTheme(themeMode = themeMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
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
