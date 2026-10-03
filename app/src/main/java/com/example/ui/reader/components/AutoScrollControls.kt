package com.example.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AutoScrollControls(
  isPlaying: Boolean,
  currentSpeed: Float,
  onTogglePlay: () -> Unit,
  onSpeedDecrease: () -> Unit,
  onSpeedIncrease: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("auto_scroll_controls")
      .clip(RoundedCornerShape(24.dp)),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
          shape = RoundedCornerShape(24.dp)
        )
        .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Play / Pause Button
        IconButton(
          onClick = onTogglePlay,
          modifier = Modifier
            .size(44.dp)
            .testTag("auto_scroll_toggle_play")
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause Auto Scroll" else "Resume Auto Scroll",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
          )
        }

        // Speed Controls: [-]  SPEED 1.0x  [+]
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          IconButton(
            onClick = onSpeedDecrease,
            modifier = Modifier
              .testTag("speed_decrease_button")
              .size(36.dp),
            enabled = currentSpeed > 0.5f
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Decrease Speed",
              tint = if (currentSpeed > 0.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
              modifier = Modifier.size(18.dp)
            )
          }

          Box(
            modifier = Modifier
              .padding(horizontal = 6.dp)
              .testTag("speed_indicator_badge"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = String.format("%.1fx", currentSpeed),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 13.sp
            )
          }

          IconButton(
            onClick = onSpeedIncrease,
            modifier = Modifier
              .testTag("speed_increase_button")
              .size(36.dp),
            enabled = currentSpeed < 3.0f
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Increase Speed",
              tint = if (currentSpeed < 3.0f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Close Auto Scroll Bar
        IconButton(
          onClick = onClose,
          modifier = Modifier
            .testTag("auto_scroll_close_button")
            .size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Auto Scroll",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
