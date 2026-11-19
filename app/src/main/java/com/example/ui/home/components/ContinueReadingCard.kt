package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LastReadPosition
import com.example.model.ReadingMode
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors

@Composable
fun ContinueReadingCard(
  lastRead: LastReadPosition,
  onContinueClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer()
      .testTag("continue_reading_card")
      .clip(RoundedCornerShape(20.dp))
      .clickable { onContinueClick() },
    colors = CardDefaults.cardColors(
      containerColor = if (noorColors.isDark) {
        noorColors.outerContainerStart
      } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (noorColors.isDark) 0.dp else 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          if (noorColors.isDark) {
            noorColors.outerContainerBrush
          } else {
            Brush.horizontalGradient(
              colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
              )
            )
          }
        )
        .border(
          width = 1.dp,
          color = if (noorColors.isDark) noorColors.cardBorder else MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
          shape = RoundedCornerShape(20.dp)
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CONTINUE READING",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            verticalAlignment = Alignment.Bottom
          ) {
            Text(
              text = "${lastRead.surahNameEnglish}, Ayah ${lastRead.ayahNumber}",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = lastRead.surahNameArabic,
              fontFamily = AmiriFontFamily,
              fontSize = 20.sp,
              color = noorColors.surahArabicSubtitleText
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Juz ${lastRead.juzNumber}",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
            Text(
              text = " • ",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  if (noorColors.isDark) noorColors.badgeBackground else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
                .then(
                  if (noorColors.isDark) {
                    Modifier.border(1.dp, noorColors.badgeBorder, RoundedCornerShape(6.dp))
                  } else {
                    Modifier
                  }
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (lastRead.readingMode == ReadingMode.CONTINUOUS) "Continuous Scroll" else "Swipe Mode",
                style = MaterialTheme.typography.labelSmall,
                color = if (noorColors.isDark) noorColors.badgeText else MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("continue_reading_resume_button"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "Resume",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
          )
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Resume Reading",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
