package com.example.ui.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.model.MushafScript
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.quranFontFamily

private val BookmarkCardShape = RoundedCornerShape(16.dp)

@Composable
fun BookmarksTabContent(
  bookmarks: List<BookmarkEntity>,
  mushafScript: MushafScript,
  onOpenAyah: (surahNumber: Int, ayahIndex: Int) -> Unit,
  onOpenMushafPage: (pageNumber: Int, surahNumber: Int) -> Unit,
  onDeleteBookmark: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  var selectedFilter by remember { mutableIntStateOf(0) } // 0 = All, 1 = Ayahs, 2 = Mushaf

  val filteredBookmarks = remember(bookmarks, selectedFilter) {
    when (selectedFilter) {
      1 -> bookmarks.filter { !it.isMushafPage }
      2 -> bookmarks.filter { it.isMushafPage }
      else -> bookmarks
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("bookmarks_tab_content")
  ) {
    // Header & Filter Pills
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Text(
        text = "Saved Bookmarks",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Your saved Ayahs and Mushaf pages",
        style = MaterialTheme.typography.bodySmall,
        color = noorColors.mutedText
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All (${bookmarks.size})", "Ayahs", "Mushaf Pages").forEachIndexed { index, label ->
          val active = selectedFilter == index
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(
                if (active) {
                  if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer
                } else {
                  noorColors.innerCardBackground
                }
              )
              .border(
                width = 1.dp,
                color = if (active) {
                  if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.secondary
                } else {
                  noorColors.cardBorder
                },
                shape = RoundedCornerShape(10.dp)
              )
              .clickable { selectedFilter = index }
              .padding(horizontal = 12.dp, vertical = 7.dp)
          ) {
            Text(
              text = label,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
              ),
              color = if (active) {
                if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onPrimaryContainer
              } else {
                noorColors.mutedText
              }
            )
          }
        }
      }
    }

    if (filteredBookmarks.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(noorColors.badgeBackground)
            .border(1.dp, noorColors.badgeBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = null,
            tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "No Bookmarks Yet",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Tap the bookmark icon on any Ayah or Mushaf page to save it here for quick access.",
          style = MaterialTheme.typography.bodySmall,
          color = noorColors.mutedText,
          textAlign = TextAlign.Center
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("bookmarks_tab_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredBookmarks, key = { it.id }) { bookmark ->
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(BookmarkCardShape)
              .background(noorColors.innerCardBackground)
              .border(1.dp, noorColors.cardBorder, BookmarkCardShape)
              .clickable {
                if (bookmark.isMushafPage) {
                  onOpenMushafPage(bookmark.mushafPageNumber, bookmark.surahNumber)
                } else {
                  onOpenAyah(bookmark.surahNumber, (bookmark.ayahNumber - 1).coerceAtLeast(0))
                }
              }
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(noorColors.badgeBackground)
                    .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (bookmark.isMushafPage) {
                      Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = noorColors.badgeText,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                      text = if (bookmark.isMushafPage) {
                        if (bookmark.mushafPageNumber > 0) "Mushaf Pg ${bookmark.mushafPageNumber}" else "Mushaf"
                      } else {
                        "Juz ${bookmark.juzNumber}"
                      },
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = noorColors.badgeText
                    )
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (bookmark.isMushafPage) {
                    "Surah ${bookmark.surahNumber} • ${bookmark.surahNameEnglish}"
                  } else {
                    "${bookmark.surahNameEnglish}, Ayah ${bookmark.ayahNumber}"
                  },
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              IconButton(
                onClick = { onDeleteBookmark(bookmark.id) },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Delete Bookmark",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = bookmark.arabicTextPreview,
              fontFamily = mushafScript.quranFontFamily(),
              fontSize = 20.sp,
              color = if (noorColors.isDark) noorColors.fullAyahArabicText else MaterialTheme.colorScheme.primary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = bookmark.translationPreview,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
