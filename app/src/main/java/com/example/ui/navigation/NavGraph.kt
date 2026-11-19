package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.PreferencesRepository
import com.example.ui.about.AboutScreen
import com.example.ui.bookmarks.BookmarksScreen
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.notes.NotesScreen
import com.example.ui.reader.QuranReaderScreen
import com.example.ui.reader.ReaderViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
  navController: NavHostController,
  homeViewModel: HomeViewModel,
  readerViewModel: ReaderViewModel,
  bookmarkRepository: BookmarkRepository,
  preferencesRepository: PreferencesRepository,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val allBookmarks by readerViewModel.allBookmarks.collectAsStateWithLifecycle()
  val allNotes by homeViewModel.allNotes.collectAsStateWithLifecycle()
  val surahList by homeViewModel.surahList.collectAsStateWithLifecycle()
  val mushafScript by preferencesRepository.mushafScript.collectAsStateWithLifecycle()

  NavHost(
    navController = navController,
    startDestination = Screen.Splash.route,
    modifier = modifier
  ) {
    composable(Screen.Splash.route) {
      SplashScreen(
        onSplashFinished = {
          navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
          }
        }
      )
    }

    composable(Screen.Home.route) {
      HomeScreen(
        viewModel = homeViewModel,
        onOpenSurah = { surahNumber, ayahIndex ->
          navController.navigate(Screen.SurahReader.createRoute(surahNumber, ayahIndex))
        },
        onOpenJuz = { juzNumber, ayahIndex ->
          navController.navigate(Screen.Reader.createRoute(juzNumber, ayahIndex))
        },
        onOpenNotes = {
          navController.navigate(Screen.Notes.route)
        },
        onOpenSettings = {
          navController.navigate(Screen.Settings.route)
        }
      )
    }

    composable(
      route = Screen.Reader.route,
      arguments = listOf(
        navArgument("juzNumber") { type = NavType.IntType; defaultValue = 1 },
        navArgument("ayahIndex") { type = NavType.IntType; defaultValue = 0 }
      )
    ) { backStackEntry ->
      val juzNumber = backStackEntry.arguments?.getInt("juzNumber") ?: 1
      val ayahIndex = backStackEntry.arguments?.getInt("ayahIndex") ?: 0

      QuranReaderScreen(
        viewModel = readerViewModel,
        preferencesRepository = preferencesRepository,
        juzNumber = juzNumber,
        surahNumber = 0,
        initialAyahIndex = ayahIndex,
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }

    composable(
      route = Screen.SurahReader.route,
      arguments = listOf(
        navArgument("surahNumber") { type = NavType.IntType; defaultValue = 1 },
        navArgument("ayahIndex") { type = NavType.IntType; defaultValue = 0 }
      )
    ) { backStackEntry ->
      val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
      val ayahIndex = backStackEntry.arguments?.getInt("ayahIndex") ?: 0

      QuranReaderScreen(
        viewModel = readerViewModel,
        preferencesRepository = preferencesRepository,
        juzNumber = 0,
        surahNumber = surahNumber,
        initialAyahIndex = ayahIndex,
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }

    composable(Screen.Notes.route) {
      NotesScreen(
        notes = allNotes,
        surahs = surahList,
        onSaveNote = { id, title, content, surahNum, surahName, ayahNum ->
          homeViewModel.saveNote(id, title, content, surahNum, surahName, ayahNum)
        },
        onDeleteNote = { id ->
          homeViewModel.deleteNote(id)
        },
        onOpenSurahAyah = { surahNumber, ayahIndex ->
          navController.navigate(Screen.SurahReader.createRoute(surahNumber, ayahIndex))
        },
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }

    composable(Screen.Bookmarks.route) {
      BookmarksScreen(
        bookmarks = allBookmarks,
        mushafScript = mushafScript,
        onOpenAyah = { juzNumber, ayahIndex ->
          navController.navigate(Screen.Reader.createRoute(juzNumber, ayahIndex))
        },
        onDeleteBookmark = { id ->
          scope.launch {
            bookmarkRepository.removeBookmark(id)
          }
        },
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }

    composable(Screen.Settings.route) {
      SettingsScreen(
        preferencesRepository = preferencesRepository,
        onNavigateBack = {
          navController.popBackStack()
        },
        onOpenAbout = {
          navController.navigate(Screen.About.route)
        }
      )
    }

    composable(Screen.About.route) {
      AboutScreen(
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }
  }
}
