package com.example.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WordTranslation
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.OutfitFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordByWordSection(
  words: List<WordTranslation>,
  arabicFontSize: Float = 24f,
  translationFontSize: Float = 13f,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Section Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 12.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = "WORD BY WORD",
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.secondary,
          fontSize = 10.sp
        )
      }
    }

    // Word cards wrapping naturally in RTL reading direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        words.forEach { word ->
          WordCard(
            word = word,
            arabicFontSize = arabicFontSize,
            translationFontSize = translationFontSize
          )
        }
      }
    }
  }
}

@Composable
fun WordCard(
  word: WordTranslation,
  arabicFontSize: Float,
  translationFontSize: Float,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .widthIn(min = 68.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 10.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Arabic Word on top
      Text(
        text = word.arabic,
        fontFamily = AmiriFontFamily,
        fontSize = arabicFontSize.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        lineHeight = (arabicFontSize * 1.5f).sp
      )

      Spacer(modifier = Modifier.height(3.dp))

      // English Meaning underneath
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
          text = word.english,
          fontFamily = OutfitFontFamily,
          fontSize = translationFontSize.sp,
          fontWeight = FontWeight.Normal,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          lineHeight = (translationFontSize * 1.3f).sp
        )
      }
    }
  }
}
