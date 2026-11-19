package com.example.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.OutfitFontFamily

private val TranslationBoxShape = RoundedCornerShape(14.dp)
private val AccentBarShape = RoundedCornerShape(2.dp)

@Composable
fun TranslationSection(
  translation: String,
  fontSize: Float = 16f,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer()
      .clip(TranslationBoxShape)
      .background(noorColors.translationBoxBackground)
      .border(
        width = 1.dp,
        color = noorColors.cardBorder,
        shape = TranslationBoxShape
      )
      .padding(14.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      // Elegant Left Accent Bar (Vertical gradient #F2D98D -> #B88929 in Dark Mode)
      Box(
        modifier = Modifier
          .width(3.5.dp)
          .height(34.dp)
          .clip(AccentBarShape)
          .background(noorColors.translationAccentBrush)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
              append("TRANSLATION ")
            }
            withStyle(
              SpanStyle(
                color = if (noorColors.isDark) noorColors.mutedText else MaterialTheme.colorScheme.secondary,
                fontWeight = if (noorColors.isDark) FontWeight.Medium else FontWeight.Bold
              )
            ) {
              append("(Sahih International)")
            }
          },
          fontFamily = OutfitFontFamily,
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          ),
          fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "\"$translation\"",
          fontFamily = OutfitFontFamily,
          fontSize = fontSize.sp,
          fontWeight = FontWeight.Normal,
          lineHeight = (fontSize * 1.55f).sp,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Left,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

