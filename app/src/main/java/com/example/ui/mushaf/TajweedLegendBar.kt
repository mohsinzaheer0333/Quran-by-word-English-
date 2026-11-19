package com.example.ui.mushaf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MushafPaperMode
import com.example.ui.theme.LocalNoorColors

private data class TajweedLegendEntry(
  val color: Color,
  val colorName: String,
  val description: String
)

/**
 * Compact, collapsible English-only Tajweed Color Guide displayed below the Mushaf page frame.
 * Swatch colors automatically match the active paper mode (Classic Cream vs Midnight Navy).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TajweedLegendBar(
  paperMode: MushafPaperMode,
  tajweedEnabled: Boolean,
  expanded: Boolean,
  onToggleExpanded: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  val palette = TajweedPalettes.forPaperMode(paperMode)
  val entries = listOf(
    TajweedLegendEntry(palette.madd, "Red", "Elongation (Madd)"),
    TajweedLegendEntry(palette.ghunnah, "Green", "Nasalization (Ghunnah / Ikhfa)"),
    TajweedLegendEntry(palette.qalqalah, "Blue", "Echoing Sound (Qalqalah)"),
    TajweedLegendEntry(palette.tafkheem, "Purple", "Heavy Letters (Tafkheem)"),
    TajweedLegendEntry(palette.silent, "Grey", "Silent Letters (Not Pronounced)")
  )

  val containerShape = RoundedCornerShape(12.dp)
  val borderColor = if (noorColors.isDark) noorColors.badgeBorder else noorColors.cardBorder

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
      .clip(containerShape)
      .background(noorColors.innerCardBackground)
      .border(1.dp, borderColor, containerShape)
      .testTag("tajweed_legend_bar")
  ) {
    // Collapsible Header Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 36.dp)
        .clickable(onClick = onToggleExpanded)
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .testTag("tajweed_guide_toggle"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        // 5-dot Tajweed color preview strip
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
          entries.forEach { entry ->
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (tajweedEnabled) entry.color else entry.color.copy(alpha = 0.4f))
            )
          }
        }
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "Tajweed Color Guide",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          ),
          color = if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onSurface
        )
        if (!tajweedEnabled) {
          Text(
            text = "(Off)",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = noorColors.mutedText
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (expanded) "Hide" else "Guide",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.5.sp
          ),
          color = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
        )
        Icon(
          imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
          contentDescription = if (expanded) "Collapse Tajweed Color Guide" else "Expand Tajweed Color Guide",
          tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Expanded English-only 5-rule Legend
    AnimatedVisibility(
      visible = expanded,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 10.dp, end = 10.dp, bottom = 8.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .height(1.dp)
            .background(noorColors.cardBorder)
        )
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          entries.forEach { entry ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(9.dp)
                  .clip(CircleShape)
                  .background(entry.color)
              )
              Text(
                text = "${entry.colorName} — ${entry.description}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
