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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutfitFontFamily

private val TranslationBoxShape = RoundedCornerShape(14.dp)
private val AccentBarShape = RoundedCornerShape(2.dp)

@Composable
fun TranslationSection(
  translation: String,
  fontSize: Float = 16f,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer()
      .clip(TranslationBoxShape)
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        shape = TranslationBoxShape
      )
      .padding(14.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      // Elegant Left Accent Bar
      Box(
        modifier = Modifier
          .width(3.5.dp)
          .height(34.dp)
          .clip(AccentBarShape)
          .background(MaterialTheme.colorScheme.secondary)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "TRANSLATION (Sahih International)",
          fontFamily = OutfitFontFamily,
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.secondary,
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

