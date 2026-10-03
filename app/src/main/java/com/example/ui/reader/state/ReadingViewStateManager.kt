package com.example.ui.reader.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.model.ReadingMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * State holder and coordinator for the Quran Reader viewing modes:
 * - Continuous Scroll: Smooth vertical reading with automatic and manual scrolling
 * - Page-Turn / Swipe: Focused horizontal page-turn per Ayah
 *
 * Ensures position parity so switching between modes never loses the reader's current verse.
 */
@Stable
class ReadingViewStateManager(
  initialMode: ReadingMode,
  initialAyahIndex: Int,
  val lazyListState: LazyListState,
  val pagerState: PagerState,
  private val coroutineScope: CoroutineScope,
  private val onModeChanged: (ReadingMode) -> Unit,
  private val onAyahChanged: (Int) -> Unit
) {
  var currentMode by mutableStateOf(initialMode)
    private set

  var currentAyahIndex by mutableIntStateOf(initialAyahIndex)
    private set

  var isToggleBarVisible by mutableStateOf(true)
    private set

  /**
   * Switches the reading mode with seamless index synchronization.
   */
  fun setReadingMode(newMode: ReadingMode) {
    if (newMode == currentMode) return

    // Capture the active index before mode switch
    val targetIndex = when (currentMode) {
      ReadingMode.CONTINUOUS -> lazyListState.firstVisibleItemIndex
      ReadingMode.SWIPE -> pagerState.currentPage
    }

    currentAyahIndex = targetIndex
    currentMode = newMode
    onModeChanged(newMode)

    // Synchronize target state smoothly
    coroutineScope.launch {
      when (newMode) {
        ReadingMode.CONTINUOUS -> {
          lazyListState.scrollToItem(targetIndex)
        }
        ReadingMode.SWIPE -> {
          if (targetIndex in 0 until pagerState.pageCount) {
            pagerState.scrollToPage(targetIndex)
          }
        }
      }
    }
  }

  /**
   * Toggles between Continuous Scroll and Swipe / Page-Turn.
   */
  fun toggleMode() {
    val nextMode = when (currentMode) {
      ReadingMode.CONTINUOUS -> ReadingMode.SWIPE
      ReadingMode.SWIPE -> ReadingMode.CONTINUOUS
    }
    setReadingMode(nextMode)
  }

  /**
   * Called when an Ayah becomes active/visible in either mode.
   */
  fun onAyahVisible(index: Int) {
    if (index != currentAyahIndex) {
      currentAyahIndex = index
      onAyahChanged(index)
    }
  }

  /**
   * Smoothly navigates to a specific Ayah index.
   */
  fun jumpToAyah(index: Int, animate: Boolean = true) {
    currentAyahIndex = index
    onAyahChanged(index)
    coroutineScope.launch {
      when (currentMode) {
        ReadingMode.CONTINUOUS -> {
          if (animate) {
            lazyListState.animateScrollToItem(index)
          } else {
            lazyListState.scrollToItem(index)
          }
        }
        ReadingMode.SWIPE -> {
          if (index in 0 until pagerState.pageCount) {
            if (animate) {
              pagerState.animateScrollToPage(index)
            } else {
              pagerState.scrollToPage(index)
            }
          }
        }
      }
    }
  }

  /**
   * Toggle visibility of the mode toggle bar for full-screen immersive reading.
   */
  fun toggleBarVisibility() {
    isToggleBarVisible = !isToggleBarVisible
  }

  fun updateModeFromExternal(newMode: ReadingMode) {
    if (newMode != currentMode) {
      currentMode = newMode
    }
  }
}

/**
 * Creates and remembers a [ReadingViewStateManager].
 */
@Composable
fun rememberReadingViewStateManager(
  currentMode: ReadingMode,
  initialAyahIndex: Int,
  totalAyahs: Int,
  onModeChanged: (ReadingMode) -> Unit,
  onAyahChanged: (Int) -> Unit
): ReadingViewStateManager {
  val coroutineScope = rememberCoroutineScope()
  val clampedIndex = initialAyahIndex.coerceIn(0, (totalAyahs - 1).coerceAtLeast(0))

  val lazyListState = rememberLazyListState(
    initialFirstVisibleItemIndex = clampedIndex
  )

  val pagerState = rememberPagerState(
    initialPage = clampedIndex,
    pageCount = { totalAyahs.coerceAtLeast(1) }
  )

  val manager = remember(totalAyahs) {
    ReadingViewStateManager(
      initialMode = currentMode,
      initialAyahIndex = clampedIndex,
      lazyListState = lazyListState,
      pagerState = pagerState,
      coroutineScope = coroutineScope,
      onModeChanged = onModeChanged,
      onAyahChanged = onAyahChanged
    )
  }

  // Keep internal mode in sync with ViewModel state changes
  manager.updateModeFromExternal(currentMode)

  return manager
}
