package com.example.ui.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.model.MushafScript
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.lineHeightMultiplier
import com.example.ui.theme.quranFontFamily

private val QuickSearchSuggestions = listOf("Mercy", "Patience", "Guidance", "Forgiveness", "الرحمن", "الحمد")
private val ResultCardShape = RoundedCornerShape(16.dp)

@Composable
fun SearchTabContent(
  query: String,
  results: List<Ayah>,
  isSearching: Boolean,
  mushafScript: MushafScript,
  onQueryChange: (String) -> Unit,
  onOpenAyah: (surahNumber: Int, ayahIndex: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("search_tab_content")
  ) {
    // Search Bar & Suggestions Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Text(
        text = "Search the Holy Quran",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Search across all 6,236 verses in Arabic or English",
        style = MaterialTheme.typography.bodySmall,
        color = noorColors.mutedText
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Type an English or Arabic keyword (e.g. Mercy, الرحمن)...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(
              onClick = { onQueryChange("") },
              modifier = Modifier.testTag("search_clear_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search",
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
          .testTag("quran_keyword_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Quick keyword chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickSearchSuggestions.forEach { chip ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(noorColors.badgeBackground)
              .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(10.dp))
              .clickable { onQueryChange(chip) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = chip,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = noorColors.badgeText
            )
          }
        }
      }
    }

    // Results Body
    when {
      isSearching -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
              color = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Searching 6,236 verses...",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
          }
        }
      }

      query.trim().length < 2 -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Instant Offline Quran Search",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Enter at least 2 characters in English or Arabic, or tap a suggested topic above.",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      results.isEmpty() -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No verses matched \"$query\". Try another English or Arabic keyword.",
            style = MaterialTheme.typography.bodyMedium,
            color = noorColors.mutedText,
            textAlign = TextAlign.Center
          )
        }
      }

      else -> {
        Text(
          text = "Showing ${results.size}${if (results.size >= 80) "+" else ""} matching verses",
          style = MaterialTheme.typography.labelMedium,
          color = noorColors.mutedText,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("search_results_list"),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(results, key = { it.verseKey }) { ayah ->
            SearchResultCard(
              ayah = ayah,
              mushafScript = mushafScript,
              onClick = { onOpenAyah(ayah.surahNumber, (ayah.ayahNumber - 1).coerceAtLeast(0)) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SearchResultCard(
  ayah: Ayah,
  mushafScript: MushafScript,
  onClick: () -> Unit
) {
  val noorColors = LocalNoorColors.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(ResultCardShape)
      .background(noorColors.innerCardBackground)
      .border(1.dp, noorColors.cardBorder, ResultCardShape)
      .clickable(onClick = onClick)
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(noorColors.badgeBackground)
            .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = ayah.verseKey,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = noorColors.badgeText
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${ayah.surahNameEnglish} (${ayah.surahNameArabic})",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Text(
        text = "Juz ${ayah.juzNumber}",
        style = MaterialTheme.typography.labelSmall,
        color = noorColors.mutedText
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Text(
        text = ayah.arabicTextFor(mushafScript),
        fontFamily = mushafScript.quranFontFamily(),
        fontSize = 21.sp,
        lineHeight = (21f * mushafScript.lineHeightMultiplier()).sp,
        color = noorColors.fullAyahArabicText,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = ayah.englishTranslation,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      maxLines = 3,
      overflow = TextOverflow.Ellipsis
    )
  }
}
