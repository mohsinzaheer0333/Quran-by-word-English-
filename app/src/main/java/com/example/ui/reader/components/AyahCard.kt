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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.model.MushafScript
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.lineHeightMultiplier
import com.example.ui.theme.quranFontFamily

private val AyahCardShape = RoundedCornerShape(22.dp)
private val BadgeShape = RoundedCornerShape(10.dp)
private val ArabicTextBoxShape = RoundedCornerShape(14.dp)

/**
 * High-performance Ayah card with isolated graphicsLayer (RepaintBoundary equivalent)
 * and flattened layout tree to achieve silky-smooth 60/120 FPS scrolling.
 */
@Composable
fun AyahCard(
  ayah: Ayah,
  isBookmarked: Boolean,
  onToggleBookmark: () -> Unit,
  arabicFontSize: Float = 30f,
  translationFontSize: Float = 16f,
  mushafScript: MushafScript = MushafScript.UTHMANI,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer() // Isolates drawing layer on the GPU to prevent parent screen repaints
      .testTag("ayah_card_${ayah.surahNumber}_${ayah.ayahNumber}")
      .border(
        width = 1.dp,
        color = noorColors.cardBorder,
        shape = AyahCardShape
      ),
    shape = AyahCardShape,
    colors = CardDefaults.cardColors(
      containerColor = if (noorColors.isDark) noorColors.outerContainerStart else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (noorColors.isDark) 0.dp else 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .then(
          if (noorColors.isDark) Modifier.background(noorColors.outerContainerBrush) else Modifier
        )
        .padding(16.dp)
    ) {
      // Ayah Header: Ayah badge, Surah title, and Bookmark action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(BadgeShape)
              .background(noorColors.badgeBackground)
              .border(
                width = 1.dp,
                color = noorColors.badgeBorder,
                shape = BadgeShape
              )
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "AYAH ${ayah.ayahNumber}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = noorColors.badgeText
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = buildAnnotatedString {
              withStyle(SpanStyle(color = noorColors.surahTitleText)) {
                append(ayah.surahNameEnglish)
              }
              withStyle(
                SpanStyle(
                  color = if (noorColors.isDark) noorColors.surahArabicSubtitleText else MaterialTheme.colorScheme.onSurfaceVariant
                )
              ) {
                append(" (${ayah.surahNameArabic})")
              }
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
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

      Spacer(modifier = Modifier.height(14.dp))

      // Complete Arabic Ayah Text rendered in the active global Mushaf script & font
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(ArabicTextBoxShape)
            .background(noorColors.fullAyahBoxBackground)
            .then(
              if (noorColors.isDark) {
                Modifier.border(1.dp, noorColors.cardBorder, ArabicTextBoxShape)
              } else {
                Modifier
              }
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
          Text(
            text = ayah.arabicTextFor(mushafScript),
            fontFamily = mushafScript.quranFontFamily(),
            fontSize = arabicFontSize.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = (arabicFontSize * mushafScript.lineHeightMultiplier()).sp,
            color = noorColors.fullAyahArabicText,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Word-by-Word Translation Section
      if (ayah.words.isNotEmpty()) {
        WordByWordSection(
          words = ayah.words,
          arabicFontSize = (arabicFontSize * 0.75f).coerceAtLeast(18f),
          translationFontSize = (translationFontSize * 0.82f).coerceAtLeast(12f),
          mushafScript = mushafScript
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Complete Translation Section
      TranslationSection(
        translation = ayah.englishTranslation,
        fontSize = translationFontSize
      )
    }
  }
}
