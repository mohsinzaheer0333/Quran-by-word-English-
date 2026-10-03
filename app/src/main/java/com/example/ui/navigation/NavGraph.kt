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
        onOpenJuz = { juzNumber, ayahIndex ->
          navController.navigate(Screen.Reader.createRoute(juzNumber, ayahIndex))
        },
        onOpenBookmarks = {
          navController.navigate(Screen.Bookmarks.route)
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
        juzNumber = juzNumber,
        initialAyahIndex = ayahIndex,
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }

    composable(Screen.Bookmarks.route) {
      BookmarksScreen(
        bookmarks = allBookmarks,
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
