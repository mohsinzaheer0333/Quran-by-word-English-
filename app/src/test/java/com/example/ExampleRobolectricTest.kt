package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.QuranRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    val launcherName = context.getString(R.string.launcher_label)
    assertEquals("Quran by Word English", appName)
    assertEquals("Quran by Word English", launcherName)
  }

  @Test
  fun `verify all 30 juz metadata are present`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = QuranRepositoryImpl(context)
    val allJuz = repository.getAllJuz()
    assertEquals(30, allJuz.size)
    assertEquals(1, allJuz.first().number)
    assertEquals("الم", allJuz.first().arabicName)
    assertEquals(30, allJuz.last().number)
    assertEquals("عَمَّ يَتَسَاءَلُونَ", allJuz.last().arabicName)
  }

  @Test
  fun `verify reading Juz 1, 2, 3, 10, 20, 30 from offline verified assets`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = QuranRepositoryImpl(context)

    // Juz 1: Must be Al-Fatihah 1:1 through Al-Baqarah 2:141 (148 verses)
    val juz1 = repository.getAyahsForJuz(1)
    assertEquals(148, juz1.size)
    assertEquals(1, juz1.first().surahNumber)
    assertEquals(1, juz1.first().ayahNumber)
    assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ", juz1.first().arabicText)
    assertTrue(juz1.first().words.isNotEmpty())
    assertEquals("In (the) name", juz1.first().words.first().english)
    assertEquals(2, juz1.last().surahNumber)
    assertEquals(141, juz1.last().ayahNumber)

    // Juz 2: Must be Al-Baqarah 2:142 through 2:252 (111 verses)
    val juz2 = repository.getAyahsForJuz(2)
    assertEquals(111, juz2.size)
    assertEquals(2, juz2.first().surahNumber)
    assertEquals(142, juz2.first().ayahNumber)
    assertEquals(2, juz2.last().surahNumber)
    assertEquals(252, juz2.last().ayahNumber)
    // Verify Juz 2 does NOT contain Juz 1's starting verse
    assertNotEquals(juz1.first().arabicText, juz2.first().arabicText)

    // Juz 3: Must be Al-Baqarah 2:253 through Ali 'Imran 3:92 (126 verses)
    val juz3 = repository.getAyahsForJuz(3)
    assertEquals(126, juz3.size)
    assertEquals(2, juz3.first().surahNumber)
    assertEquals(253, juz3.first().ayahNumber)
    assertEquals(3, juz3.last().surahNumber)
    assertEquals(92, juz3.last().ayahNumber)

    // Juz 10: Must be Al-Anfal 8:41 through At-Tawbah 9:92 (127 verses)
    val juz10 = repository.getAyahsForJuz(10)
    assertEquals(127, juz10.size)
    assertEquals(8, juz10.first().surahNumber)
    assertEquals(41, juz10.first().ayahNumber)
    assertEquals(9, juz10.last().surahNumber)
    assertEquals(92, juz10.last().ayahNumber)

    // Juz 20: Must be An-Naml 27:56 through Al-Ankabut 29:45 (171 verses)
    val juz20 = repository.getAyahsForJuz(20)
    assertEquals(171, juz20.size)
    assertEquals(27, juz20.first().surahNumber)
    assertEquals(56, juz20.first().ayahNumber)
    assertEquals(29, juz20.last().surahNumber)
    assertEquals(45, juz20.last().ayahNumber)

    // Juz 30: Must be An-Naba 78:1 through An-Nas 114:6 (564 verses)
    val juz30 = repository.getAyahsForJuz(30)
    assertEquals(564, juz30.size)
    assertEquals(78, juz30.first().surahNumber)
    assertEquals(1, juz30.first().ayahNumber)
    assertEquals(114, juz30.last().surahNumber)
    assertEquals(6, juz30.last().ayahNumber)
    assertTrue(juz30.last().words.isNotEmpty())
  }

  @Test
  fun `verify surahs metadata covers all 114 surahs`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = QuranRepositoryImpl(context)
    val surahs = repository.getAllSurahs()
    assertEquals(114, surahs.size)
    assertEquals("Al-Fatihah", surahs.first().nameSimple)
    assertEquals("An-Nas", surahs.last().nameSimple)
  }
}
