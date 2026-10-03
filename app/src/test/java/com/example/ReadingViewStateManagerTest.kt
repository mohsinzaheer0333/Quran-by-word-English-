package com.example

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import com.example.model.ReadingMode
import com.example.ui.reader.state.ReadingViewStateManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReadingViewStateManagerTest {

  private val testDispatcher = StandardTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  @Test
  fun `verify toggleMode switches from continuous to swipe and back`() = runTest(testDispatcher) {
    var reportedMode: ReadingMode? = null
    var reportedAyah = -1

    val lazyListState = LazyListState(firstVisibleItemIndex = 5)
    val pagerState = object : PagerState(currentPage = 0) {
      override val pageCount: Int = 50
    }

    val manager = ReadingViewStateManager(
      initialMode = ReadingMode.CONTINUOUS,
      initialAyahIndex = 5,
      lazyListState = lazyListState,
      pagerState = pagerState,
      coroutineScope = testScope,
      onModeChanged = { reportedMode = it },
      onAyahChanged = { reportedAyah = it }
    )

    assertEquals(ReadingMode.CONTINUOUS, manager.currentMode)
    assertEquals(5, manager.currentAyahIndex)

    // Toggle to SWIPE
    manager.toggleMode()
    assertEquals(ReadingMode.SWIPE, manager.currentMode)
    assertEquals(ReadingMode.SWIPE, reportedMode)

    // Toggle back to CONTINUOUS
    manager.toggleMode()
    assertEquals(ReadingMode.CONTINUOUS, manager.currentMode)
    assertEquals(ReadingMode.CONTINUOUS, reportedMode)
  }

  @Test
  fun `verify setReadingMode directly changes view state`() = runTest(testDispatcher) {
    var reportedMode: ReadingMode? = null
    val lazyListState = LazyListState(firstVisibleItemIndex = 0)
    val pagerState = object : PagerState(currentPage = 0) {
      override val pageCount: Int = 30
    }

    val manager = ReadingViewStateManager(
      initialMode = ReadingMode.CONTINUOUS,
      initialAyahIndex = 0,
      lazyListState = lazyListState,
      pagerState = pagerState,
      coroutineScope = testScope,
      onModeChanged = { reportedMode = it },
      onAyahChanged = {}
    )

    manager.setReadingMode(ReadingMode.SWIPE)
    assertEquals(ReadingMode.SWIPE, manager.currentMode)
    assertEquals(ReadingMode.SWIPE, reportedMode)

    // Redundant call does nothing
    reportedMode = null
    manager.setReadingMode(ReadingMode.SWIPE)
    assertEquals(null, reportedMode)
  }

  @Test
  fun `verify ayah index change propagates through manager`() = runTest(testDispatcher) {
    var lastReportedAyah = -1
    val lazyListState = LazyListState()
    val pagerState = object : PagerState(currentPage = 0) {
      override val pageCount: Int = 20
    }

    val manager = ReadingViewStateManager(
      initialMode = ReadingMode.CONTINUOUS,
      initialAyahIndex = 0,
      lazyListState = lazyListState,
      pagerState = pagerState,
      coroutineScope = testScope,
      onModeChanged = {},
      onAyahChanged = { lastReportedAyah = it }
    )

    manager.onAyahVisible(12)
    assertEquals(12, manager.currentAyahIndex)
    assertEquals(12, lastReportedAyah)
  }

  @Test
  fun `verify toggle bar visibility can be collapsed and expanded`() = runTest(testDispatcher) {
    val lazyListState = LazyListState()
    val pagerState = object : PagerState(currentPage = 0) {
      override val pageCount: Int = 10
    }

    val manager = ReadingViewStateManager(
      initialMode = ReadingMode.CONTINUOUS,
      initialAyahIndex = 0,
      lazyListState = lazyListState,
      pagerState = pagerState,
      coroutineScope = testScope,
      onModeChanged = {},
      onAyahChanged = {}
    )

    assertTrue(manager.isToggleBarVisible)
    manager.toggleBarVisibility()
    assertFalse(manager.isToggleBarVisible)
    manager.toggleBarVisibility()
    assertTrue(manager.isToggleBarVisible)
  }
}
