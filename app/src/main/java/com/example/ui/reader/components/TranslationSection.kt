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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutfitFontFamily

@Composable
fun TranslationSection(
  translation: String,
  fontSize: Float = 16f,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(14.dp)
      )
      .padding(16.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      // Elegant Left Accent Bar
      Box(
        modifier = Modifier
          .width(3.5.dp)
          .height(36.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(MaterialTheme.colorScheme.secondary)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "TRANSLATION (Sahih International)",
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.secondary,
          fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "\"$translation\"",
          fontFamily = OutfitFontFamily,
          fontSize = fontSize.sp,
          fontWeight = FontWeight.Normal,
          lineHeight = (fontSize * 1.55f).sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
