package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TranslationLanguage

@Composable
fun LanguageDropdown(
  selectedLanguageCode: String,
  availableLanguages: List<TranslationLanguage>,
  onLanguageSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }
  val currentLang = availableLanguages.find { it.code == selectedLanguageCode }
    ?: availableLanguages.firstOrNull() ?: TranslationLanguage("en", "English", "English")

  Box(modifier = modifier) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .testTag("language_selector_button")
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        .clickable { expanded = true }
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Column {
        Text(
          text = "Language",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = currentLang.displayName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Select Language",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
      availableLanguages.forEach { lang ->
        DropdownMenuItem(
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${lang.displayName} (${lang.nativeName})",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (lang.code == selectedLanguageCode) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (lang.isAvailable) MaterialTheme.colorScheme.onSurface
                          else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                if (!lang.isAvailable) {
                  Text(
                    text = "Coming in future update",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp
                  )
                }
              }
              if (lang.code == selectedLanguageCode) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          },
          onClick = {
            if (lang.isAvailable) {
              onLanguageSelected(lang.code)
            }
            expanded = false
          },
          enabled = lang.isAvailable
        )
      }
    }
  }
}
