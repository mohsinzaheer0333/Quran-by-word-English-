package com.example.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MushafScript
import com.example.ui.theme.LocalNoorColors

private val SwitcherContainerShape = RoundedCornerShape(16.dp)
private val SwitcherTabShape = RoundedCornerShape(12.dp)

/**
 * Global 2-tab Mushaf Script Switcher rendered exclusively on the Main Home Screen.
 * Switches between Madinah (Uthmani) and Indo-Pak Quran orthography & typography globally.
 */
@Composable
fun MushafScriptSwitcher(
  selectedScript: MushafScript,
  onSelectScript: (MushafScript) -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  val containerBg = if (noorColors.isDark) {
    noorColors.innerCardBackground // #091526
  } else {
    MaterialTheme.colorScheme.surface
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(SwitcherContainerShape)
      .background(containerBg)
      .border(
        width = 1.dp,
        color = noorColors.cardBorder,
        shape = SwitcherContainerShape
      )
      .padding(4.dp)
      .testTag("mushaf_script_switcher"),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    MushafScriptTab(
      label = MushafScript.UTHMANI.label,
      isSelected = selectedScript == MushafScript.UTHMANI,
      onClick = { onSelectScript(MushafScript.UTHMANI) },
      modifier = Modifier
        .weight(1f)
        .testTag("script_tab_uthmani")
    )

    MushafScriptTab(
      label = MushafScript.INDOPAK.label,
      isSelected = selectedScript == MushafScript.INDOPAK,
      onClick = { onSelectScript(MushafScript.INDOPAK) },
      modifier = Modifier
        .weight(1f)
        .testTag("script_tab_indopak")
    )
  }
}

@Composable
private fun MushafScriptTab(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  val targetBackground = when {
    isSelected && noorColors.isDark -> noorColors.primaryGold.copy(alpha = 0.14f)
    isSelected && !noorColors.isDark -> MaterialTheme.colorScheme.primaryContainer
    else -> Color.Transparent
  }

  val targetBorder = when {
    isSelected && noorColors.isDark -> noorColors.primaryGold // #D9AE4E
    isSelected && !noorColors.isDark -> MaterialTheme.colorScheme.secondary
    else -> Color.Transparent
  }

  val targetTextColor = when {
    isSelected && noorColors.isDark -> noorColors.lightGold // #F2D98D
    isSelected && !noorColors.isDark -> MaterialTheme.colorScheme.onPrimaryContainer
    noorColors.isDark -> noorColors.surahArabicSubtitleText // #B2C3D9
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }

  val animatedBg by animateColorAsState(
    targetValue = targetBackground,
    animationSpec = tween(durationMillis = 180),
    label = "script_tab_bg"
  )
  val animatedBorder by animateColorAsState(
    targetValue = targetBorder,
    animationSpec = tween(durationMillis = 180),
    label = "script_tab_border"
  )
  val animatedText by animateColorAsState(
    targetValue = targetTextColor,
    animationSpec = tween(durationMillis = 180),
    label = "script_tab_text"
  )

  Box(
    modifier = modifier
      .heightIn(min = 48.dp)
      .clip(SwitcherTabShape)
      .background(animatedBg)
      .border(
        width = 1.dp,
        color = animatedBorder,
        shape = SwitcherTabShape
      )
      .semantics { selected = isSelected }
      .clickable(role = Role.Tab, onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 13.sp
      ),
      color = animatedText,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
