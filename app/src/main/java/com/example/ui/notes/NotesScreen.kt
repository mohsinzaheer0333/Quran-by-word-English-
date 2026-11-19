package com.example.ui.notes

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.NoteEntity
import com.example.model.SurahInfo
import com.example.ui.theme.LocalNoorColors

private val NoteCardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
  notes: List<NoteEntity>,
  surahs: List<SurahInfo>,
  onSaveNote: (id: Long, title: String, content: String, surahNumber: Int?, surahName: String?, ayahNumber: Int?) -> Unit,
  onDeleteNote: (Long) -> Unit,
  onOpenSurahAyah: (surahNumber: Int, ayahIndex: Int) -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onNavigateBack() }

  val noorColors = LocalNoorColors.current
  var editingNote by remember { mutableStateOf<NoteEntity?>(null) }
  var showNoteEditor by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(noorColors.topBarBrush)
      ) {
        TopAppBar(
          title = {
            Column {
              Text(
                text = "My Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${notes.size} saved reflections",
                style = MaterialTheme.typography.labelSmall,
                color = noorColors.mutedText
              )
            }
          },
          navigationIcon = {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.testTag("notes_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.onSurface
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        if (noorColors.isDark) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(noorColors.cardBorder)
          )
        }
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          editingNote = null
          showNoteEditor = true
        },
        containerColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_note_fab")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
      }
    },
    modifier = modifier
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      if (notes.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(noorColors.badgeBackground)
              .border(1.dp, noorColors.badgeBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.EditNote,
              contentDescription = null,
              tint = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No Personal Notes Yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Capture personal reflections, lessons, and Surah/Ayah study notes stored securely on your device.",
            style = MaterialTheme.typography.bodySmall,
            color = noorColors.mutedText,
            textAlign = TextAlign.Center
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("notes_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(notes, key = { it.id }) { note ->
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(NoteCardShape)
                .background(noorColors.innerCardBackground)
                .border(1.dp, noorColors.cardBorder, NoteCardShape)
                .clickable {
                  editingNote = note
                  showNoteEditor = true
                }
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = note.title,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
                )
                IconButton(
                  onClick = { onDeleteNote(note.id) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Note",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              if (note.surahNumber != null && note.ayahNumber != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(noorColors.badgeBackground)
                    .border(1.dp, noorColors.badgeBorder, RoundedCornerShape(8.dp))
                    .clickable {
                      onOpenSurahAyah(note.surahNumber, (note.ayahNumber - 1).coerceAtLeast(0))
                    }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "${note.surahNameEnglish ?: "Surah ${note.surahNumber}"} • Ayah ${note.ayahNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = noorColors.badgeText
                  )
                }
              }

              if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = note.content,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 4,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }
  }

  if (showNoteEditor) {
    NoteEditorDialog(
      initialNote = editingNote,
      surahs = surahs,
      onDismiss = { showNoteEditor = false },
      onSave = { title, content, surahNum, ayahNum ->
        val surahName = surahNum?.let { num -> surahs.find { it.number == num }?.nameSimple }
        onSaveNote(editingNote?.id ?: 0L, title, content, surahNum, surahName, ayahNum)
        showNoteEditor = false
      }
    )
  }
}

@Composable
private fun NoteEditorDialog(
  initialNote: NoteEntity?,
  surahs: List<SurahInfo>,
  onDismiss: () -> Unit,
  onSave: (title: String, content: String, surahNumber: Int?, ayahNumber: Int?) -> Unit
) {
  val noorColors = LocalNoorColors.current
  var title by remember(initialNote) { mutableStateOf(initialNote?.title ?: "") }
  var content by remember(initialNote) { mutableStateOf(initialNote?.content ?: "") }
  var surahText by remember(initialNote) { mutableStateOf(initialNote?.surahNumber?.toString() ?: "") }
  var ayahText by remember(initialNote) { mutableStateOf(initialNote?.ayahNumber?.toString() ?: "") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = if (noorColors.isDark) noorColors.topBarGradientStart else MaterialTheme.colorScheme.surface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, noorColors.cardBorder, RoundedCornerShape(22.dp))
        .testTag("note_editor_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = if (initialNote == null) "New Reflection Note" else "Edit Note",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
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
            .testTag("note_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = surahText,
            onValueChange = { surahText = it.filter(Char::isDigit).take(3) },
            label = { Text("Surah # (opt)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = noorColors.cardBorder,
              focusedContainerColor = noorColors.innerCardBackground,
              unfocusedContainerColor = noorColors.innerCardBackground
            ),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = ayahText,
            onValueChange = { ayahText = it.filter(Char::isDigit).take(3) },
            label = { Text("Ayah # (opt)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = noorColors.cardBorder,
              focusedContainerColor = noorColors.innerCardBackground,
              unfocusedContainerColor = noorColors.innerCardBackground
            ),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Reflection / Note") },
          minLines = 4,
          maxLines = 7,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = noorColors.cardBorder,
            focusedContainerColor = noorColors.innerCardBackground,
            unfocusedContainerColor = noorColors.innerCardBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_content_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = noorColors.mutedText)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val sNum = surahText.toIntOrNull()?.takeIf { it in 1..114 }
              val maxA = sNum?.let { n -> surahs.find { it.number == n }?.versesCount } ?: 286
              val aNum = ayahText.toIntOrNull()?.takeIf { it in 1..maxA }
              onSave(title, content, sNum, aNum)
            },
            enabled = title.isNotBlank() || content.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.testTag("note_save_button")
          ) {
            Text("Save Note", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
