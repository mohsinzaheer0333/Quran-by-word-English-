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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.SurahInfo
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors

@Composable
fun FindAyahDialog(
  surahs: List<SurahInfo>,
  onDismiss: () -> Unit,
  onJumpToAyah: (surahNumber: Int, ayahIndex: Int) -> Unit
) {
  val noorColors = LocalNoorColors.current
  var surahFilter by remember { mutableStateOf("") }
  var selectedSurah by remember(surahs) { mutableStateOf(surahs.firstOrNull()) }
  var ayahInput by remember { mutableStateOf("1") }

  val filteredSurahs = remember(surahs, surahFilter) {
    val q = surahFilter.trim().lowercase()
    if (q.isEmpty()) surahs
    else surahs.filter {
      it.nameSimple.lowercase().contains(q) ||
        it.nameArabic.contains(q) ||
        it.number.toString() == q
    }
  }

  val maxAyah = selectedSurah?.versesCount ?: 1
  val parsedAyah = ayahInput.toIntOrNull()
  val isValidAyah = parsedAyah != null && parsedAyah in 1..maxAyah

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = if (noorColors.isDark) noorColors.topBarGradientStart else MaterialTheme.colorScheme.surface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, noorColors.cardBorder, RoundedCornerShape(22.dp))
        .testTag("find_ayah_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Find Ayah",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Jump directly to any Surah & Ayah",
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("find_ayah_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Surah filter input
        OutlinedTextField(
          value = surahFilter,
          onValueChange = { surahFilter = it },
          placeholder = { Text("Search Surah by name or number...") },
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
          modifier = Modifier
            .fillMaxWidth()
            .testTag("find_ayah_surah_filter")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Scrollable Surah selector list
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 210.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(noorColors.innerCardBackground)
            .border(1.dp, noorColors.cardBorder, RoundedCornerShape(12.dp))
        ) {
          LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(filteredSurahs, key = { it.number }) { surah ->
              val isSelected = selectedSurah?.number == surah.number
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    if (isSelected) {
                      if (noorColors.isDark) noorColors.activeWordCardBackground else MaterialTheme.colorScheme.primaryContainer
                    } else {
                      MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                    }
                  )
                  .clickable {
                    selectedSurah = surah
                    val currentNum = ayahInput.toIntOrNull() ?: 1
                    if (currentNum > surah.versesCount) {
                      ayahInput = "1"
                    }
                  }
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
                    color = if (isSelected && noorColors.isDark) noorColors.primaryGold else noorColors.mutedText
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = surah.nameSimple,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                      ),
                      color = if (isSelected && noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "${surah.versesCount} Ayahs",
                      style = MaterialTheme.typography.labelSmall,
                      color = noorColors.mutedText
                    )
                  }
                }
                Text(
                  text = surah.nameArabic,
                  fontFamily = AmiriFontFamily,
                  fontSize = 18.sp,
                  color = if (isSelected && noorColors.isDark) noorColors.lightGold else noorColors.juzArabicNameText
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ayah number input
        OutlinedTextField(
          value = ayahInput,
          onValueChange = { raw ->
            ayahInput = raw.filter { it.isDigit() }.take(3)
          },
          label = { Text("Ayah Number (1 – $maxAyah)") },
          isError = ayahInput.isNotEmpty() && !isValidAyah,
          supportingText = {
            if (ayahInput.isNotEmpty() && !isValidAyah) {
              Text("Enter a number between 1 and $maxAyah")
            } else if (selectedSurah != null) {
              Text("Selected: ${selectedSurah!!.nameSimple} • Ayah ${parsedAyah ?: 1}")
            }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = noorColors.cardBorder,
            focusedContainerColor = noorColors.innerCardBackground,
            unfocusedContainerColor = noorColors.innerCardBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("find_ayah_number_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Dialog Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = noorColors.mutedText)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val surah = selectedSurah ?: return@Button
              val targetAyah = (parsedAyah ?: 1).coerceIn(1, surah.versesCount)
              onJumpToAyah(surah.number, targetAyah - 1)
            },
            enabled = selectedSurah != null && isValidAyah,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.testTag("find_ayah_jump_button")
          ) {
            Text("Jump to Ayah", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
