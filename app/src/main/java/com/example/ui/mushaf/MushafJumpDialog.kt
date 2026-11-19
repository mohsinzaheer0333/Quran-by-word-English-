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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.JuzInfo
import com.example.model.MushafEditionData
import com.example.model.SurahInfo
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors

@Composable
fun MushafJumpDialog(
  edition: MushafEditionData,
  currentPage: Int,
  surahs: List<SurahInfo>,
  juzList: List<JuzInfo>,
  onDismiss: () -> Unit,
  onJumpToPage: (Int) -> Unit
) {
  val noorColors = LocalNoorColors.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Page, 1 = Surah, 2 = Juz
  var pageInput by remember(currentPage) { mutableStateOf(currentPage.toString()) }
  var filterText by remember { mutableStateOf("") }

  val parsedPage = pageInput.toIntOrNull()
  val isValidPage = parsedPage != null && parsedPage in 1..edition.totalPages

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = if (noorColors.isDark) noorColors.topBarGradientStart else MaterialTheme.colorScheme.surface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, noorColors.cardBorder, RoundedCornerShape(22.dp))
        .testTag("mushaf_jump_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Go to Page / Surah / Juz",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${edition.linesPerPage}-Line Mushaf • ${edition.totalPages} Pages",
              style = MaterialTheme.typography.labelSmall,
              color = noorColors.mutedText
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3-Tab Selector: Page | Surah | Juz
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(noorColors.innerCardBackground)
            .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
            .padding(3.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf("Page", "Surah", "Juz").forEachIndexed { idx, label ->
            val active = selectedTab == idx
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(9.dp))
                .background(
                  if (active) {
                    if (noorColors.isDark) noorColors.primaryGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer
                  } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                  }
                )
                .border(
                  width = 1.dp,
                  color = if (active) {
                    if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.secondary
                  } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                  },
                  shape = RoundedCornerShape(9.dp)
                )
                .clickable {
                  selectedTab = idx
                  filterText = ""
                }
                .padding(vertical = 9.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
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

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
          0 -> {
            // Direct Page Jump
            OutlinedTextField(
              value = pageInput,
              onValueChange = { raw -> pageInput = raw.filter(Char::isDigit).take(3) },
              label = { Text("Page Number (1 – ${edition.totalPages})") },
              isError = pageInput.isNotEmpty() && !isValidPage,
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = noorColors.cardBorder,
                focusedContainerColor = noorColors.innerCardBackground,
                unfocusedContainerColor = noorColors.innerCardBackground
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("mushaf_page_number_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick shortcuts
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(
                "Start (1)" to 1,
                "Middle (${edition.totalPages / 2})" to (edition.totalPages / 2),
                "End (${edition.totalPages})" to edition.totalPages
              ).forEach { (title, target) ->
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(noorColors.badgeBackground)
                    .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(10.dp))
                    .clickable { onJumpToPage(target) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = noorColors.badgeText
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { parsedPage?.let { onJumpToPage(it) } },
              enabled = isValidPage,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("mushaf_jump_page_confirm")
            ) {
              Text("Open Page ${parsedPage ?: ""}", fontWeight = FontWeight.Bold)
            }
          }

          1 -> {
            // Surah Jump
            OutlinedTextField(
              value = filterText,
              onValueChange = { filterText = it },
              placeholder = { Text("Filter Surah...") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = noorColors.cardBorder,
                focusedContainerColor = noorColors.innerCardBackground,
                unfocusedContainerColor = noorColors.innerCardBackground
              ),
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            val filtered = remember(surahs, filterText) {
              val q = filterText.trim().lowercase()
              if (q.isEmpty()) surahs
              else surahs.filter {
                it.nameSimple.lowercase().contains(q) ||
                  it.nameArabic.contains(q) ||
                  it.number.toString() == q
              }
            }
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(noorColors.innerCardBackground)
                .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
            ) {
              items(filtered, key = { it.number }) { surah ->
                val targetPage = edition.surahFirstPage[surah.number] ?: 1
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onJumpToPage(targetPage) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = "${surah.number}.",
                      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                      color = noorColors.mutedText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = surah.nameSimple,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "Page $targetPage",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
                      )
                    }
                  }
                  Text(
                    text = surah.nameArabic,
                    fontFamily = AmiriFontFamily,
                    fontSize = 18.sp,
                    color = noorColors.juzArabicNameText
                  )
                }
              }
            }
          }

          else -> {
            // Juz Jump
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 310.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(noorColors.innerCardBackground)
                .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
            ) {
              items(juzList, key = { it.number }) { juz ->
                val targetPage = edition.juzFirstPage[juz.number] ?: 1
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onJumpToPage(targetPage) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Juz ${juz.number} • ${juz.transliteration}",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Starts on Page $targetPage",
                      style = MaterialTheme.typography.labelSmall,
                      color = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
                    )
                  }
                  Text(
                    text = juz.arabicName,
                    fontFamily = AmiriFontFamily,
                    fontSize = 18.sp,
                    color = noorColors.juzArabicNameText
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
