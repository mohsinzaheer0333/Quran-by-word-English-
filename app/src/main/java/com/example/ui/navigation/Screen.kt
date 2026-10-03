package com.example.ui.navigation

sealed class Screen(val route: String) {
  object Splash : Screen("splash")
  object Home : Screen("home")
  object Bookmarks : Screen("bookmarks")
  object Settings : Screen("settings")
  object About : Screen("about")

  object Reader : Screen("reader/{juzNumber}/{ayahIndex}") {
    fun createRoute(juzNumber: Int, ayahIndex: Int = 0): String =
      "reader/$juzNumber/$ayahIndex"
  }
}
