package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ThemeMode
import com.example.ui.brand.AppIconView
import com.example.ui.home.components.ContinueReadingCard
import com.example.ui.home.components.FindAyahDialog
import com.example.ui.home.components.HomeBottomNavBar
import com.example.ui.home.components.JuzCard
import com.example.ui.home.components.MushafScriptSwitcher
import com.example.ui.home.components.QuickActionRow
import com.example.ui.home.components.SurahCard
import com.example.ui.home.tabs.BookmarksTabContent
import com.example.ui.home.tabs.MushafTabContent
import com.example.ui.home.tabs.SearchTabContent
import com.example.ui.theme.LocalNoorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  onOpenSurah: (surahNumber: Int, ayahIndex: Int) -> Unit,
  onOpenJuz: (juzNumber: Int, ayahIndex: Int) -> Unit,
  onOpenNotes: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
  val lastRead by viewModel.lastRead.collectAsStateWithLifecycle()
  val mushafScript by viewModel.mushafScript.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val surahList by viewModel.surahList.collectAsStateWithLifecycle()
  val filterQuery by viewModel.filterQuery.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
  val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
  val mushafPaperMode by viewModel.mushafPaperMode.collectAsStateWithLifecycle()
  val tajweedEnabled by viewModel.tajweedEnabled.collectAsStateWithLifecycle()
  val mushafEditionData by viewModel.mushafEditionData.collectAsStateWithLifecycle()
  val currentMushafPage by viewModel.currentMushafPage.collectAsStateWithLifecycle()
  val isMushafLoading by viewModel.isMushafLoading.collectAsStateWithLifecycle()
  val allBookmarks by viewModel.allBookmarks.collectAsStateWithLifecycle()
  val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()

  val noorColors = LocalNoorColors.current
  var showFindAyahDialog by remember { mutableStateOf(false) }

  val filteredSurahs = remember(surahList, filterQuery) {
    val q = filterQuery.trim().lowercase()
    if (q.isEmpty()) surahList
    else surahList.filter {
      it.nameSimple.lowercase().contains(q) ||
        it.nameArabic.contains(q) ||
        it.number.toString() == q ||
        it.revelationPlace.lowercase().contains(q)
    }
  }

  val filteredJuzList = remember(viewModel.juzList, filterQuery) {
    val q = filterQuery.trim().lowercase()
    if (q.isEmpty()) viewModel.juzList
    else viewModel.juzList.filter {
      it.transliteration.lowercase().contains(q) ||
        it.arabicName.contains(q) ||
        it.startSurahName.lowercase().contains(q) ||
        it.endSurahName.lowercase().contains(q) ||
        it.number.toString() == q
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(noorColors.topBarBrush)
      ) {
        TopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              AppIconView(size = 34.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Quran Word by Word",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1
                )
                Text(
                  text = "English Translation",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 11.sp,
                  color = if (noorColors.isDark) noorColors.surahArabicSubtitleText else MaterialTheme.colorScheme.secondary,
                  maxLines = 1
                )
              }
            }
          },
          actions = {
            // Dark / Light Theme Toggle
            IconButton(
              onClick = { viewModel.toggleTheme() },
              modifier = Modifier.testTag("theme_toggle_button")
            ) {
              Icon(
                imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (themeMode == ThemeMode.DARK) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
              )
            }

            // Settings Button
            IconButton(
              onClick = onOpenSettings,
              modifier = Modifier.testTag("home_settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = noorColors.topBarActionIconTint
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
          )
        )
        if (noorColors.isDark) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(noorColors.cardBorder)
          )
        }
      }
    },
    bottomBar = {
      HomeBottomNavBar(
        selectedTab = selectedTab,
        onTabSelected = { tab -> viewModel.selectTab(tab) }
      )
    },
    modifier = modifier
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        HomeTab.SURAH, HomeTab.PARAH -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag(if (selectedTab == HomeTab.SURAH) "home_surah_list" else "home_juz_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // 1. Continue Reading Hero Card with Gold Resume button
            item(key = "continue_reading_card") {
              ContinueReadingCard(
                lastRead = lastRead,
                onContinueClick = {
                  onOpenSurah(lastRead.surahNumber, (lastRead.ayahNumber - 1).coerceAtLeast(0))
                }
              )
            }

            // 2. 2-Column Quick Action Row: Find Ayah & My Notes
            item(key = "quick_action_row") {
              QuickActionRow(
                notesCount = allNotes.size,
                onFindAyahClick = { showFindAyahDialog = true },
                onMyNotesClick = onOpenNotes
              )
            }

            // 3. Single Global 2-Tab Mushaf Script Switcher (Main Home Screen ONLY)
            item(key = "mushaf_script_switcher") {
              MushafScriptSwitcher(
                selectedScript = mushafScript,
                onSelectScript = { script -> viewModel.setMushafScript(script) }
              )
            }

            // 4. Search / Filter Bar
            item(key = "home_filter_bar") {
              OutlinedTextField(
                value = filterQuery,
                onValueChange = { viewModel.updateFilterQuery(it) },
                placeholder = {
                  Text(
                    text = if (selectedTab == HomeTab.SURAH) {
                      "Filter Surahs by name or number..."
                    } else {
                      "Filter Parah (Juz) by name or number..."
                    },
                    style = MaterialTheme.typography.bodyMedium
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
                  )
                },
                trailingIcon = {
                  if (filterQuery.isNotEmpty()) {
                    IconButton(
                      onClick = { viewModel.updateFilterQuery("") },
                      modifier = Modifier.testTag("home_filter_clear_button")
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
                  unfocusedBorderColor = noorColors.cardBorder,
                  focusedContainerColor = noorColors.innerCardBackground,
                  unfocusedContainerColor = noorColors.innerCardBackground
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("home_filter_input")
              )
            }

            // 5. Section Header
            item(key = "list_section_header") {
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = if (selectedTab == HomeTab.SURAH) "All 114 Surahs" else "The 30 Parah (Juz)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                  )
                  Text(
                    text = if (selectedTab == HomeTab.SURAH) {
                      "Read word-by-word English & full translation"
                    } else {
                      "Explore the Holy Quran section by section"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = noorColors.mutedText
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(noorColors.badgeBackground)
                    .then(
                      if (noorColors.isDark) {
                        Modifier.border(1.dp, noorColors.badgeBorder, RoundedCornerShape(8.dp))
                      } else {
                        Modifier
                      }
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = if (selectedTab == HomeTab.SURAH) {
                      "${filteredSurahs.size} Surahs"
                    } else {
                      "${filteredJuzList.size} Parah"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = noorColors.badgeText
                  )
                }
              }
            }

            // 6. Primary List: 114 Surahs OR 30 Parah (Juz)
            if (selectedTab == HomeTab.SURAH) {
              items(
                items = filteredSurahs,
                key = { "surah_${it.number}" },
                contentType = { "surah_card" }
              ) { surah ->
                SurahCard(
                  surah = surah,
                  onClick = { onOpenSurah(surah.number, 0) }
                )
              }
            } else {
              items(
                items = filteredJuzList,
                key = { "juz_${it.number}" },
                contentType = { "juz_card" }
              ) { juz ->
                JuzCard(
                  juz = juz,
                  onClick = { onOpenJuz(juz.number, 0) }
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(12.dp))
            }
          }
        }

        HomeTab.SEARCH -> {
          SearchTabContent(
            query = searchQuery,
            results = searchResults,
            isSearching = isSearching,
            mushafScript = mushafScript,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onOpenAyah = { surahNum, ayahIdx -> onOpenSurah(surahNum, ayahIdx) }
          )
        }

        HomeTab.MUSHAF -> {
          val isCurrentMushafBookmarked = allBookmarks.any {
            it.isMushafPage && it.mushafPageNumber == currentMushafPage
          }
          MushafTabContent(
            edition = mushafEditionData,
            isLoading = isMushafLoading,
            currentPage = currentMushafPage,
            selectedScript = mushafScript,
            paperMode = mushafPaperMode,
            tajweedEnabled = tajweedEnabled,
            surahs = surahList,
            juzList = viewModel.juzList,
            isCurrentPageBookmarked = isCurrentMushafBookmarked,
            onSelectScript = { script -> viewModel.setMushafScript(script) },
            onSelectPaperMode = { mode -> viewModel.setMushafPaperMode(mode) },
            onToggleTajweed = { viewModel.toggleTajweedEnabled() },
            onPageChanged = { page -> viewModel.setMushafPage(page) },
            onToggleBookmark = { pageData, surah -> viewModel.toggleMushafPageBookmark(pageData, surah) }
          )
        }

        HomeTab.BOOKMARKS -> {
          BookmarksTabContent(
            bookmarks = allBookmarks,
            mushafScript = mushafScript,
            onOpenAyah = { surahNum, ayahIdx -> onOpenSurah(surahNum, ayahIdx) },
            onOpenMushafPage = { pageNum, surahNum -> viewModel.openMushafBookmark(pageNum, surahNum) },
            onDeleteBookmark = { id -> viewModel.deleteBookmark(id) }
          )
        }
      }
    }
  }

  if (showFindAyahDialog) {
    FindAyahDialog(
      surahs = surahList,
      onDismiss = { showFindAyahDialog = false },
      onJumpToAyah = { surahNumber, ayahIndex ->
        showFindAyahDialog = false
        onOpenSurah(surahNumber, ayahIndex)
      }
    )
  }
}
