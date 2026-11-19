package com.example.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MushafScript
import com.example.model.WordTranslation
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.lineHeightMultiplier
import com.example.ui.theme.quranFontFamily

private val WordCardShape = RoundedCornerShape(12.dp)
private val WordHeaderBadgeShape = RoundedCornerShape(4.dp)

/**
 * Ultra-fast word-by-word layout with clean English translations and tap-to-highlight state.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordByWordSection(
  words: List<WordTranslation>,
  arabicFontSize: Float = 24f,
  translationFontSize: Float = 13f,
  mushafScript: MushafScript = MushafScript.UTHMANI,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  var selectedWordPosition by remember(words) { mutableStateOf<Int?>(null) }

  Column(modifier = modifier.fillMaxWidth()) {
    // Section Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 10.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(WordHeaderBadgeShape)
          .background(noorColors.sectionPillBackground)
          .then(
            if (noorColors.isDark) {
              Modifier.border(1.dp, noorColors.sectionPillBorder, WordHeaderBadgeShape)
            } else {
              Modifier
            }
          )
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = "WORD BY WORD",
          fontFamily = OutfitFontFamily,
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = noorColors.sectionPillText,
          fontSize = 10.sp
        )
      }
    }

    // Word cards wrapping naturally in RTL reading direction for Arabic order
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        words.forEach { word ->
          key(word.position) {
            val isSelected = selectedWordPosition == word.position
            WordCard(
              word = word,
              arabicFontSize = arabicFontSize,
              translationFontSize = translationFontSize,
              mushafScript = mushafScript,
              isSelected = isSelected,
              onClick = {
                selectedWordPosition = if (isSelected) null else word.position
              }
            )
          }
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
  mushafScript: MushafScript = MushafScript.UTHMANI,
  isSelected: Boolean = false,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Column(
    modifier = modifier
      .widthIn(min = 64.dp)
      .graphicsLayer() // GPU hardware layer isolation
      .clip(WordCardShape)
      .background(if (isSelected) noorColors.activeWordCardBackground else noorColors.wordCardBackground)
      .border(
        width = 1.dp,
        color = if (isSelected) noorColors.activeWordCardBorder else noorColors.cardBorder,
        shape = WordCardShape
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 7.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Arabic Word on top in active Mushaf script
    Text(
      text = word.arabicFor(mushafScript),
      fontFamily = mushafScript.quranFontFamily(),
      fontSize = arabicFontSize.sp,
      fontWeight = FontWeight.Normal,
      color = if (isSelected) noorColors.wordArabicActiveText else noorColors.wordArabicText,
      textAlign = TextAlign.Center,
      lineHeight = (arabicFontSize * mushafScript.lineHeightMultiplier()).sp
    )

    Spacer(modifier = Modifier.height(3.dp))

    // English Meaning underneath
    Text(
      text = word.english,
      fontFamily = OutfitFontFamily,
      fontSize = translationFontSize.sp,
      fontWeight = FontWeight.Normal,
      color = noorColors.wordEnglishText,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodySmall,
      lineHeight = (translationFontSize * 1.3f).sp
    )
  }
}

