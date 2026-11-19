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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurahInfo
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors

private val SurahCardShape = RoundedCornerShape(18.dp)
private val SurahMedallionShape = RoundedCornerShape(12.dp)
private val SurahBadgeShape = RoundedCornerShape(4.dp)

@Composable
fun SurahCard(
  surah: SurahInfo,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  val placeLabel = if (surah.revelationPlace.equals("makkah", ignoreCase = true)) "Meccan" else "Medinan"

  Card(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer()
      .testTag("surah_card_${surah.number}")
      .clip(SurahCardShape)
      .clickable { onClick() },
    shape = SurahCardShape,
    colors = CardDefaults.cardColors(
      containerColor = noorColors.innerCardBackground
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (noorColors.isDark) 0.dp else 1.5.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = noorColors.cardBorder,
          shape = SurahCardShape
        )
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Surah Number Medallion
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(SurahMedallionShape)
            .background(noorColors.badgeBackground)
            .border(
              width = 1.dp,
              color = if (noorColors.isDark) noorColors.badgeBorder else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
              shape = SurahMedallionShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${surah.number}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = noorColors.badgeText
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Surah English Name & Metadata
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = surah.nameSimple,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = placeLabel,
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
            Text(
              text = "•",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
            Box(
              modifier = Modifier
                .clip(SurahBadgeShape)
                .background(
                  if (noorColors.isDark) noorColors.badgeBackground else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
                .then(
                  if (noorColors.isDark) {
                    Modifier.border(1.dp, noorColors.badgeBorder, SurahBadgeShape)
                  } else {
                    Modifier
                  }
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${surah.versesCount} Ayahs",
                style = MaterialTheme.typography.labelSmall,
                color = if (noorColors.isDark) noorColors.badgeText else MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Surah Arabic Name
        Text(
          text = surah.nameArabic,
          fontFamily = AmiriFontFamily,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = noorColors.juzArabicNameText,
          textAlign = TextAlign.End,
          modifier = Modifier.padding(end = 4.dp)
        )
      }
    }
  }
}
