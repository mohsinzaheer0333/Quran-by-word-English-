package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNoorColors

private val QuickActionPillShape = RoundedCornerShape(14.dp)

@Composable
fun QuickActionRow(
  notesCount: Int,
  onFindAyahClick: () -> Unit,
  onMyNotesClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .testTag("home_quick_action_row"),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    QuickActionPill(
      icon = Icons.Default.ManageSearch,
      label = "Find Ayah",
      onClick = onFindAyahClick,
      modifier = Modifier
        .weight(1f)
        .testTag("quick_action_find_ayah")
    )

    QuickActionPill(
      icon = Icons.Default.EditNote,
      label = if (notesCount > 0) "My Notes ($notesCount)" else "My Notes",
      onClick = onMyNotesClick,
      modifier = Modifier
        .weight(1f)
        .testTag("quick_action_my_notes")
    )
  }
}

@Composable
private fun QuickActionPill(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Row(
    modifier = modifier
      .heightIn(min = 48.dp)
      .clip(QuickActionPillShape)
      .background(noorColors.innerCardBackground)
      .border(
        width = 1.dp,
        color = noorColors.cardBorder,
        shape = QuickActionPillShape
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
      ),
      color = MaterialTheme.colorScheme.onSurface,
      maxLines = 1
    )
  }
}
