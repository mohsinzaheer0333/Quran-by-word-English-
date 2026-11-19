package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.home.HomeTab
import com.example.ui.theme.LocalNoorColors

@Composable
fun HomeBottomNavBar(
  selectedTab: HomeTab,
  onTabSelected: (HomeTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(if (noorColors.isDark) noorColors.topBarGradientStart else MaterialTheme.colorScheme.surface)
      .testTag("home_bottom_nav_bar")
  ) {
    // Crisp 1dp top border separating content from navigation bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(noorColors.cardBorder)
    )

    NavigationBar(
      containerColor = Color.Transparent,
      tonalElevation = 0.dp
    ) {
      HomeTab.entries.forEach { tab ->
        val isSelected = selectedTab == tab
        NavigationBarItem(
          selected = isSelected,
          onClick = { onTabSelected(tab) },
          modifier = Modifier.testTag("bottom_nav_${tab.name.lowercase()}"),
          icon = {
            Icon(
              imageVector = tab.icon(),
              contentDescription = tab.label,
              modifier = Modifier.size(22.dp)
            )
          },
          label = {
            Text(
              text = tab.label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
            selectedTextColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
            indicatorColor = if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.14f) else MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = noorColors.mutedText,
            unselectedTextColor = noorColors.mutedText
          )
        )
      }
    }
  }
}

private fun HomeTab.icon(): ImageVector = when (this) {
  HomeTab.SURAH -> Icons.AutoMirrored.Filled.MenuBook
  HomeTab.PARAH -> Icons.Default.Layers
  HomeTab.SEARCH -> Icons.Default.Search
  HomeTab.MUSHAF -> Icons.Default.AutoStories
  HomeTab.BOOKMARKS -> Icons.Default.Bookmark
}
