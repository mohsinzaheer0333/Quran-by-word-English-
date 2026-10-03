package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.PreferencesRepository
import com.example.model.ReadingMode
import com.example.model.ThemeMode
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  preferencesRepository: PreferencesRepository,
  onNavigateBack: () -> Unit,
  onOpenAbout: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onNavigateBack()
  }

  val themeMode by preferencesRepository.themeMode.collectAsStateWithLifecycle()
  val readingMode by preferencesRepository.readingMode.collectAsStateWithLifecycle()
  val arabicFontSize by preferencesRepository.arabicFontSize.collectAsStateWithLifecycle()
  val translationFontSize by preferencesRepository.translationFontSize.collectAsStateWithLifecycle()
  val autoScrollSpeed by preferencesRepository.autoScrollSpeed.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("settings_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Theme Preference Card
      SettingsGroup(
        title = "APPEARANCE",
        icon = Icons.Default.LightMode
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ThemeMode.values().forEach { mode ->
            FilterChip(
              selected = themeMode == mode,
              onClick = { preferencesRepository.setThemeMode(mode) },
              label = {
                Text(
                  text = when (mode) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System"
                  }
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("theme_chip_${mode.name.lowercase()}")
            )
          }
        }
      }

      // Reading Mode Preference Card
      SettingsGroup(
        title = "DEFAULT READING MODE",
        icon = Icons.Default.ViewStream
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          com.example.ui.reader.components.ReadingModeSegmentedToggle(
            selectedMode = readingMode,
            onModeSelected = { mode -> preferencesRepository.setReadingMode(mode) }
          )
          Text(
            text = if (readingMode == ReadingMode.CONTINUOUS) {
              "Continuous Scroll: Smooth vertical scrolling through all ayahs with word cards and auto-scroll."
            } else {
              "Page-Turn / Swipe: Focused single-verse reading. Turn pages horizontally or with buttons."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Typography Sizing Card
      SettingsGroup(
        title = "TYPOGRAPHY & SIZING",
        icon = Icons.Default.FormatSize
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          // Arabic Size
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Arabic Quran Text",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${arabicFontSize.toInt()} sp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = arabicFontSize,
              onValueChange = { preferencesRepository.setArabicFontSize(it) },
              valueRange = 22f..42f,
              modifier = Modifier.testTag("settings_arabic_slider")
            )
            // Live Preview
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                fontFamily = AmiriFontFamily,
                fontSize = arabicFontSize.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
            }
          }

          // Translation Size
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "English Translation",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${translationFontSize.toInt()} sp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = translationFontSize,
              onValueChange = { preferencesRepository.setTranslationFontSize(it) },
              valueRange = 13f..24f,
              modifier = Modifier.testTag("settings_trans_slider")
            )
          }
        }
      }

      // Auto Scroll Default Speed
      SettingsGroup(
        title = "AUTO-SCROLL SPEED",
        icon = Icons.Default.Speed
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Default Scrolling Pace",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = String.format("%.1fx", autoScrollSpeed),
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }
          Slider(
            value = autoScrollSpeed,
            onValueChange = { preferencesRepository.setAutoScrollSpeed(it) },
            valueRange = 0.5f..3.0f,
            steps = 9,
            modifier = Modifier.testTag("settings_auto_scroll_slider")
          )
        }
      }

      // About & Brand Card
      SettingsGroup(
        title = "ABOUT QURAN BY WORD ENGLISH",
        icon = Icons.Default.Info
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Quran by Word English — Read. Understand. Reflect.",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Complete 114 Surahs and 30 Juz with authentic Uthmani text, word-by-word breakdowns, Sahih International translation, and 100% offline access.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          androidx.compose.material3.OutlinedButton(
            onClick = onOpenAbout,
            modifier = Modifier.fillMaxWidth().testTag("open_about_button")
          ) {
            Text("View Brand Identity, Features & Icon Quality Check")
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun SettingsGroup(
  title: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(18.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant,
          shape = RoundedCornerShape(18.dp)
        )
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
              letterSpacing = 1.1.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        content()
      }
    }
  }
}
