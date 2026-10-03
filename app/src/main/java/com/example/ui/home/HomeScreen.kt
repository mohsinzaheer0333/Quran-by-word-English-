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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ThemeMode
import com.example.ui.home.components.ContinueReadingCard
import com.example.ui.home.components.JuzCard
import com.example.ui.home.components.LanguageDropdown
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  onOpenJuz: (Int, Int) -> Unit,
  onOpenBookmarks: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
  val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
  val lastRead by viewModel.lastRead.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          // Centered / Balanced Brand
          Row(verticalAlignment = Alignment.CenterVertically) {
            com.example.ui.brand.AppIconView(size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Quran by Word English",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
              Text(
                text = "القرآن الكريم • Quran by Word English",
                fontFamily = AmiriFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
              )
            }
          }
        },
        navigationIcon = {
          // Left side: Language Selector as requested
          Box(modifier = Modifier.padding(start = 12.dp)) {
            LanguageDropdown(
              selectedLanguageCode = selectedLanguage,
              availableLanguages = viewModel.availableLanguages,
              onLanguageSelected = { langCode -> viewModel.setLanguage(langCode) }
            )
          }
        },
        actions = {
          // Right side: Light/Dark Mode Control
          IconButton(
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier.testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (themeMode == ThemeMode.DARK) "Switch to Light Mode" else "Switch to Dark Mode",
              tint = MaterialTheme.colorScheme.primary
            )
          }

          // Bookmarks Button
          IconButton(
            onClick = onOpenBookmarks,
            modifier = Modifier.testTag("home_bookmarks_button")
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = "Saved Bookmarks",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
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
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .testTag("home_juz_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Continue Reading Banner Card
      item {
        ContinueReadingCard(
          lastRead = lastRead,
          onContinueClick = {
            onOpenJuz(lastRead.juzNumber, (lastRead.ayahNumber - 1).coerceAtLeast(0))
          }
        )
      }

      // Section Header: 30 Juz
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "The 30 Juz",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Explore the Holy Quran section by section",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "30 Total",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }

      // 30 Juz Cards
      items(
        items = viewModel.juzList,
        key = { it.number }
      ) { juz ->
        JuzCard(
          juz = juz,
          onClick = { onOpenJuz(juz.number, 0) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
