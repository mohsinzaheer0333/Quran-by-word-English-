package com.example.ui.mushaf

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MushafPaperMode
import com.example.model.MushafScript
import com.example.ui.theme.LocalNoorColors

/**
 * Compact top toolbar inside the dedicated Mushaf tab:
 * - Edition Toggle: Indo-Pak (16-Line Taj Style) vs Madinah (15-Line Uthmani)
 * - Paper Mode Toggle: Classic Cream Paper (#FFFDF7) vs Midnight Navy (#071222)
 * - Page Navigator: Previous/Next page buttons, Go to Page/Surah/Juz selector, Bookmark Page
 */
@Composable
fun MushafToolbar(
  selectedScript: MushafScript,
  paperMode: MushafPaperMode,
  tajweedEnabled: Boolean,
  currentPage: Int,
  totalPages: Int,
  surahLabel: String,
  juzNumber: Int,
  isBookmarked: Boolean,
  onSelectScript: (MushafScript) -> Unit,
  onSelectPaperMode: (MushafPaperMode) -> Unit,
  onToggleTajweed: () -> Unit,
  onPrevPage: () -> Unit,
  onNextPage: () -> Unit,
  onOpenJumpDialog: () -> Unit,
  onToggleBookmark: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("mushaf_toolbar"),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Row 1: Edition Toggle (Indo-Pak 16-Line vs Madinah 15-Line) + Paper Mode Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Edition 2-tab pill
      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(noorColors.innerCardBackground)
          .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
          .padding(3.dp)
          .testTag("mushaf_edition_toggle"),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        EditionChip(
          label = "Indo-Pak 16L",
          isSelected = selectedScript == MushafScript.INDOPAK,
          onClick = { onSelectScript(MushafScript.INDOPAK) },
          modifier = Modifier
            .weight(1f)
            .testTag("mushaf_edition_indopak")
        )
        EditionChip(
          label = "Madinah 15L",
          isSelected = selectedScript == MushafScript.UTHMANI,
          onClick = { onSelectScript(MushafScript.UTHMANI) },
          modifier = Modifier
            .weight(1f)
            .testTag("mushaf_edition_madinah")
        )
      }

      // Paper Mode Toggle: Classic Cream (#FFFDF7) vs Midnight Navy (#071222)
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(noorColors.innerCardBackground)
          .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
          .padding(3.dp)
          .testTag("mushaf_paper_toggle"),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        PaperSwatchChip(
          label = "Cream",
          swatchColor = Color(0xFFFFFDF7),
          isSelected = paperMode == MushafPaperMode.CLASSIC_CREAM,
          onClick = { onSelectPaperMode(MushafPaperMode.CLASSIC_CREAM) },
          modifier = Modifier.testTag("mushaf_paper_cream")
        )
        PaperSwatchChip(
          label = "Navy",
          swatchColor = Color(0xFF071222),
          isSelected = paperMode == MushafPaperMode.MIDNIGHT_NAVY,
          onClick = { onSelectPaperMode(MushafPaperMode.MIDNIGHT_NAVY) },
          modifier = Modifier.testTag("mushaf_paper_navy")
        )
      }
    }

    // Row 2: Tajweed ON/OFF Pill | Prev | Go to Page/Surah/Juz | Next | Bookmark
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      TajweedTogglePill(
        enabled = tajweedEnabled,
        paperMode = paperMode,
        onClick = onToggleTajweed,
        modifier = Modifier.testTag("mushaf_tajweed_toggle")
      )

      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(noorColors.innerCardBackground)
          .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onPrevPage,
          enabled = currentPage > 1,
          modifier = Modifier
            .size(36.dp)
            .testTag("mushaf_prev_page_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Previous Page",
            tint = if (currentPage > 1) {
              if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.outline
            }
          )
        }

        // Interactive Go To Page / Surah / Juz pill
        Row(
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 34.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(noorColors.badgeBackground)
            .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(9.dp))
            .clickable(onClick = onOpenJumpDialog)
            .padding(horizontal = 7.dp, vertical = 4.dp)
            .testTag("mushaf_jump_selector_button"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Pg $currentPage/$totalPages • $surahLabel",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = noorColors.badgeText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.Default.UnfoldMore,
            contentDescription = "Go to Page, Surah, or Juz",
            tint = noorColors.badgeText,
            modifier = Modifier.size(14.dp)
          )
        }

        IconButton(
          onClick = onNextPage,
          enabled = currentPage < totalPages,
          modifier = Modifier
            .size(36.dp)
            .testTag("mushaf_next_page_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next Page",
            tint = if (currentPage < totalPages) {
              if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.outline
            }
          )
        }

        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier
            .size(36.dp)
            .testTag("mushaf_bookmark_button")
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = if (isBookmarked) "Remove Mushaf Page Bookmark" else "Bookmark Mushaf Page",
            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else noorColors.topBarActionIconTint,
            modifier = Modifier.size(19.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun TajweedTogglePill(
  enabled: Boolean,
  paperMode: MushafPaperMode,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  val palette = TajweedPalettes.forPaperMode(paperMode)
  val pillShape = RoundedCornerShape(12.dp)

  Row(
    modifier = modifier
      .heightIn(min = 40.dp)
      .clip(pillShape)
      .background(
        if (enabled) {
          if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer
        } else {
          noorColors.innerCardBackground
        }
      )
      .border(
        width = 1.dp,
        color = if (enabled) {
          if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.secondary
        } else {
          noorColors.cardBorder
        },
        shape = pillShape
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 9.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    // Mini 3-dot Tajweed color indicator
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
      listOf(palette.madd, palette.ghunnah, palette.qalqalah).forEach { dotColor ->
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (enabled) dotColor else dotColor.copy(alpha = 0.35f))
        )
      }
    }
    Text(
      text = if (enabled) "Tajweed: ON" else "Tajweed: OFF",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 10.5.sp
      ),
      color = if (enabled) {
        if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onPrimaryContainer
      } else {
        noorColors.mutedText
      },
      maxLines = 1
    )
  }
}

@Composable
private fun EditionChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  Box(
    modifier = modifier
      .heightIn(min = 34.dp)
      .clip(RoundedCornerShape(9.dp))
      .background(
        if (isSelected) {
          if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer
        } else {
          Color.Transparent
        }
      )
      .border(
        width = 1.dp,
        color = if (isSelected) {
          if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.secondary
        } else {
          Color.Transparent
        },
        shape = RoundedCornerShape(9.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 6.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 11.sp
      ),
      color = if (isSelected) {
        if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onPrimaryContainer
      } else {
        noorColors.mutedText
      },
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun PaperSwatchChip(
  label: String,
  swatchColor: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  Row(
    modifier = modifier
      .heightIn(min = 34.dp)
      .clip(RoundedCornerShape(9.dp))
      .background(
        if (isSelected) {
          if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer
        } else {
          Color.Transparent
        }
      )
      .border(
        width = 1.dp,
        color = if (isSelected) {
          if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.secondary
        } else {
          Color.Transparent
        },
        shape = RoundedCornerShape(9.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(11.dp)
        .clip(CircleShape)
        .background(swatchColor)
        .border(1.dp, if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.outline, CircleShape)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 11.sp
      ),
      color = if (isSelected) {
        if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onPrimaryContainer
      } else {
        noorColors.mutedText
      },
      maxLines = 1
    )
  }
}
