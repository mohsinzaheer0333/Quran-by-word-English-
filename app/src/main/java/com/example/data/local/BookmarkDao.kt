package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
  @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber)")
  fun isBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity): Long

  @Query("DELETE FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
  suspend fun deleteBookmarkByVerse(surahNumber: Int, ayahNumber: Int)

  @Query("DELETE FROM bookmarks WHERE id = :id")
  suspend fun deleteBookmarkById(id: Long)
}
