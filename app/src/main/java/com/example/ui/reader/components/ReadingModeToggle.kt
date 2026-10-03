package com.example.ui.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReadingMode

/**
 * Premium toggle control allowing users to switch between 'continuous scroll'
 * and 'swipe/page-turn' reading modes for the Quran text.
 */
@Composable
fun ReadingModeSegmentedToggle(
  selectedMode: ReadingMode,
  onModeSelected: (ReadingMode) -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag("reading_mode_segmented_toggle"),
    shape = shape,
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    tonalElevation = 2.dp
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          shape = shape
        )
        .padding(4.dp)
    ) {
      val tabWidth = maxWidth / 2
      val isContinuous = selectedMode == ReadingMode.CONTINUOUS

      val indicatorOffset by animateDpAsState(
        targetValue = if (isContinuous) 0.dp else tabWidth,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessLow
        ),
        label = "mode_indicator_offset"
      )

      // Animated selection background pill
      Box(
        modifier = Modifier
          .offset(x = indicatorOffset)
          .width(tabWidth)
          .fillMaxHeight()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(
            width = 1.dp,
            color = Color(0xFFDFC07C).copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp)
          )
      )

      // Two Segment Buttons
      Row(modifier = Modifier.fillMaxWidth()) {
        ToggleSegment(
          title = "Continuous",
          subtitle = "Scroll",
          icon = Icons.Default.ViewStream,
          isSelected = isContinuous,
          onClick = { onModeSelected(ReadingMode.CONTINUOUS) },
          testTag = "toggle_mode_continuous",
          modifier = Modifier.weight(1f)
        )

        ToggleSegment(
          title = "Page-Turn",
          subtitle = "Swipe",
          icon = Icons.Default.AutoStories,
          isSelected = !isContinuous,
          onClick = { onModeSelected(ReadingMode.SWIPE) },
          testTag = "toggle_mode_swipe",
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun ToggleSegment(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  val iconTint by animateColorAsState(
    targetValue = if (isSelected) Color(0xFFDFC07C) else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "icon_tint"
  )

  val textColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "text_color"
  )

  Row(
    modifier = modifier
      .fillMaxHeight()
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .testTag(testTag)
      .semantics {
        role = Role.Tab
        selected = isSelected
        contentDescription = "$title $subtitle reading mode"
      },
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconTint,
      modifier = Modifier.size(18.dp)
    )

    Spacer(modifier = Modifier.width(8.dp))

    Column(verticalArrangement = Arrangement.Center) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        ),
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = if (isSelected) Color(0xFFDFC07C) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        maxLines = 1
      )
    }
  }
}

/**
 * Top bar toggle strip displayed in the reader with mode description and quick actions.
 */
@Composable
fun ReadingModeToggleBar(
  currentMode: ReadingMode,
  isExpanded: Boolean,
  onModeSelected: (ReadingMode) -> Unit,
  onToggleExpand: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("reading_mode_toggle_bar"),
    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Always visible header row with compact switcher
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(Color(0xFFDFC07C))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (currentMode == ReadingMode.CONTINUOUS) "CONTINUOUS SCROLL MODE" else "PAGE-TURN SWIPE MODE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onToggleExpand,
          modifier = Modifier
            .size(32.dp)
            .testTag("toggle_bar_expand_button")
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse mode selector" else "Expand mode selector",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Expandable section with the full segmented toggle and mode hint
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
        ) {
          ReadingModeSegmentedToggle(
            selectedMode = currentMode,
            onModeSelected = onModeSelected
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = when (currentMode) {
              ReadingMode.CONTINUOUS -> "Vertical reading with smooth scrolling, word-by-word cards, and auto-scroll capability."
              ReadingMode.SWIPE -> "Focused single-verse reading. Swipe horizontally or use navigation arrows to turn pages."
            },
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }
      }
    }
  }
}
