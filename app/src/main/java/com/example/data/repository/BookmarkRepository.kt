package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.local.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {
  val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

  fun isBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean> =
    bookmarkDao.isBookmarked(surahNumber, ayahNumber)

  suspend fun toggleBookmark(bookmark: BookmarkEntity, currentlyBookmarked: Boolean) {
    if (currentlyBookmarked) {
      bookmarkDao.deleteBookmarkByVerse(bookmark.surahNumber, bookmark.ayahNumber)
    } else {
      bookmarkDao.insertBookmark(bookmark)
    }
  }

  suspend fun removeBookmark(id: Long) {
    bookmarkDao.deleteBookmarkById(id)
  }
}
