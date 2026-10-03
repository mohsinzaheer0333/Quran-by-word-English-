package com.example.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.ui.theme.AmiriFontFamily

@Composable
fun AyahCard(
  ayah: Ayah,
  isBookmarked: Boolean,
  onToggleBookmark: () -> Unit,
  arabicFontSize: Float = 30f,
  translationFontSize: Float = 16f,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ayah_card_${ayah.surahNumber}_${ayah.ayahNumber}")
      .clip(RoundedCornerShape(22.dp)),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant,
          shape = RoundedCornerShape(22.dp)
        )
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Ayah Header: Ayah badge, Surah title, and Bookmark action
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                  shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "AYAH ${ayah.ayahNumber}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
              text = "${ayah.surahNameEnglish} (${ayah.surahNameArabic})",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier
              .testTag("bookmark_button_${ayah.ayahNumber}")
              .size(40.dp)
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Ayah",
              tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Complete Arabic Ayah Text rendered in Amiri font with Harakat
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Text(
              text = ayah.arabicText,
              fontFamily = AmiriFontFamily,
              fontSize = arabicFontSize.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = (arabicFontSize * 1.75f).sp,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Right,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Word-by-Word Translation Section
        if (ayah.words.isNotEmpty()) {
          WordByWordSection(
            words = ayah.words,
            arabicFontSize = (arabicFontSize * 0.75f).coerceAtLeast(18f),
            translationFontSize = (translationFontSize * 0.82f).coerceAtLeast(12f)
          )
          Spacer(modifier = Modifier.height(18.dp))
        }

        // Complete Translation Section
        TranslationSection(
          translation = ayah.englishTranslation,
          fontSize = translationFontSize
        )
      }
    }
  }
}
