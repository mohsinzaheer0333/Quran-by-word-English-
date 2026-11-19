package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
  val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

  suspend fun saveNote(note: NoteEntity): Long {
    return if (note.id == 0L) {
      noteDao.insertNote(note)
    } else {
      noteDao.updateNote(note)
      note.id
    }
  }

  suspend fun deleteNote(id: Long) {
    noteDao.deleteNoteById(id)
  }
}
