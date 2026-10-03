package com.example.ui.reader

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ReadingMode
import com.example.ui.reader.components.AutoScrollControls
import com.example.ui.reader.components.AyahCard
import com.example.ui.reader.components.ReadingModeToggleBar
import com.example.ui.reader.state.ReadingViewStateManager
import com.example.ui.reader.state.rememberReadingViewStateManager
import com.example.ui.theme.AmiriFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
  viewModel: ReaderViewModel,
  juzNumber: Int,
  initialAyahIndex: Int,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  var showFontSizeSheet by remember { mutableStateOf(false) }

  LaunchedEffect(juzNumber, initialAyahIndex) {
    viewModel.loadJuz(juzNumber, initialAyahIndex)
  }

  // Toggle-based View State Manager
  val viewStateManager = rememberReadingViewStateManager(
    currentMode = uiState.readingMode,
    initialAyahIndex = initialAyahIndex,
    totalAyahs = uiState.ayahs.size,
    onModeChanged = { mode -> viewModel.setReadingMode(mode) },
    onAyahChanged = { index -> viewModel.onAyahVisible(index) }
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Juz ${juzNumber} • ${uiState.currentJuz?.transliteration ?: ""}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = uiState.currentJuz?.arabicName ?: "",
              fontFamily = AmiriFontFamily,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("reader_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Home",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        actions = {
          // Quick Toggle Button: toggles between Continuous Scroll and Swipe / Page-Turn
          IconButton(
            onClick = { viewStateManager.toggleMode() },
            modifier = Modifier.testTag("reader_mode_toggle_button")
          ) {
            Icon(
              imageVector = if (viewStateManager.currentMode == ReadingMode.CONTINUOUS) {
                Icons.Default.ViewStream
              } else {
                Icons.Default.AutoStories
              },
              contentDescription = if (viewStateManager.currentMode == ReadingMode.CONTINUOUS) {
                "Switch to Page-Turn Mode"
              } else {
                "Switch to Continuous Scroll Mode"
              },
              tint = Color(0xFFDFC07C)
            )
          }

          // Auto Scroll Toggle (only active in Continuous mode)
          if (viewStateManager.currentMode == ReadingMode.CONTINUOUS) {
            IconButton(
              onClick = { viewModel.toggleAutoScroll() },
              modifier = Modifier.testTag("auto_scroll_button")
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Auto Scroll",
                tint = if (uiState.isAutoScrollActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Font Sizing Sheet
          IconButton(
            onClick = { showFontSizeSheet = true },
            modifier = Modifier.testTag("font_size_sheet_button")
          ) {
            Icon(
              imageVector = Icons.Default.FormatSize,
              contentDescription = "Adjust Font Sizes",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Toggle-based View State Manager Bar
        ReadingModeToggleBar(
          currentMode = viewStateManager.currentMode,
          isExpanded = viewStateManager.isToggleBarVisible,
          onModeSelected = { selectedMode ->
            viewStateManager.setReadingMode(selectedMode)
          },
          onToggleExpand = {
            viewStateManager.toggleBarVisibility()
          }
        )

        // Reading View Body with animated crossfade transition
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          if (uiState.isLoading) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("reader_loading_spinner")
              )
            }
          } else if (uiState.ayahs.isEmpty()) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No Ayahs available for this Juz",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            AnimatedContent(
              targetState = viewStateManager.currentMode,
              transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
              },
              label = "reading_mode_switch_transition",
              modifier = Modifier.fillMaxSize()
            ) { activeMode ->
              when (activeMode) {
                ReadingMode.CONTINUOUS -> {
                  ContinuousReaderView(
                    ayahs = uiState.ayahs,
                    listState = viewStateManager.lazyListState,
                    bookmarkedKeys = uiState.bookmarkedVerseKeys,
                    arabicFontSize = uiState.arabicFontSize,
                    translationFontSize = uiState.translationFontSize,
                    isAutoScrollActive = uiState.isAutoScrollActive,
                    isAutoScrollPlaying = uiState.isAutoScrollPlaying,
                    autoScrollSpeed = uiState.autoScrollSpeed,
                    onToggleBookmark = { ayah -> viewModel.toggleBookmark(ayah) },
                    onAyahVisible = { index -> viewStateManager.onAyahVisible(index) }
                  )
                }
                ReadingMode.SWIPE -> {
                  SwipeReaderView(
                    ayahs = uiState.ayahs,
                    pagerState = viewStateManager.pagerState,
                    bookmarkedKeys = uiState.bookmarkedVerseKeys,
                    arabicFontSize = uiState.arabicFontSize,
                    translationFontSize = uiState.translationFontSize,
                    onToggleBookmark = { ayah -> viewModel.toggleBookmark(ayah) },
                    onPageChanged = { index -> viewStateManager.onAyahVisible(index) }
                  )
                }
              }
            }
          }
        }
      }

      // Auto Scroll Controls Floating Overlay (in Continuous Mode)
      AnimatedVisibility(
        visible = viewStateManager.currentMode == ReadingMode.CONTINUOUS && uiState.isAutoScrollActive,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(horizontal = 16.dp, vertical = 20.dp)
      ) {
        AutoScrollControls(
          isPlaying = uiState.isAutoScrollPlaying,
          currentSpeed = uiState.autoScrollSpeed,
          onTogglePlay = { viewModel.toggleAutoScrollPlay() },
          onSpeedDecrease = { viewModel.decreaseAutoScrollSpeed() },
          onSpeedIncrease = { viewModel.increaseAutoScrollSpeed() },
          onClose = { viewModel.closeAutoScroll() }
        )
      }
    }
  }

  // Quick Font Size Adjustment Sheet
  if (showFontSizeSheet) {
    ModalBottomSheet(
      onDismissRequest = { showFontSizeSheet = false },
      sheetState = rememberModalBottomSheetState(),
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 16.dp)
          .padding(bottom = 32.dp)
      ) {
        Text(
          text = "Reading Typography",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Arabic Font Size
        Text(
          text = "Arabic Quran Text Size: ${uiState.arabicFontSize.toInt()} sp",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
          value = uiState.arabicFontSize,
          onValueChange = { /* Updated via viewmodel */ },
          valueRange = 22f..42f,
          modifier = Modifier.testTag("arabic_font_slider")
        )

        // Translation Font Size
        Text(
          text = "English Translation Size: ${uiState.translationFontSize.toInt()} sp",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
          value = uiState.translationFontSize,
          onValueChange = { /* Updated via viewmodel */ },
          valueRange = 13f..24f,
          modifier = Modifier.testTag("translation_font_slider")
        )
      }
    }
  }
}

@Composable
private fun ContinuousReaderView(
  ayahs: List<com.example.model.Ayah>,
  listState: LazyListState,
  bookmarkedKeys: Set<String>,
  arabicFontSize: Float,
  translationFontSize: Float,
  isAutoScrollActive: Boolean,
  isAutoScrollPlaying: Boolean,
  autoScrollSpeed: Float,
  onToggleBookmark: (com.example.model.Ayah) -> Unit,
  onAyahVisible: (Int) -> Unit
) {
  // Track currently visible Ayah
  val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
  LaunchedEffect(firstVisibleItemIndex) {
    onAyahVisible(firstVisibleItemIndex)
  }

  // Auto Scroll Coroutine
  LaunchedEffect(isAutoScrollActive, isAutoScrollPlaying, autoScrollSpeed) {
    if (isAutoScrollActive && isAutoScrollPlaying) {
      while (isActive) {
        if (!listState.isScrollInProgress) {
          listState.scrollBy(2.5f * autoScrollSpeed)
        }
        delay(30L)
      }
    }
  }

  LazyColumn(
    state = listState,
    modifier = Modifier
      .fillMaxSize()
      .testTag("continuous_reader_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    itemsIndexed(
      items = ayahs,
      key = { _, item -> item.verseKey }
    ) { _, ayah ->
      AyahCard(
        ayah = ayah,
        isBookmarked = bookmarkedKeys.contains(ayah.verseKey),
        onToggleBookmark = { onToggleBookmark(ayah) },
        arabicFontSize = arabicFontSize,
        translationFontSize = translationFontSize
      )
    }
  }
}

@Composable
private fun SwipeReaderView(
  ayahs: List<com.example.model.Ayah>,
  pagerState: PagerState,
  bookmarkedKeys: Set<String>,
  arabicFontSize: Float,
  translationFontSize: Float,
  onToggleBookmark: (com.example.model.Ayah) -> Unit,
  onPageChanged: (Int) -> Unit
) {
  val scope = rememberCoroutineScope()

  LaunchedEffect(pagerState.currentPage) {
    onPageChanged(pagerState.currentPage)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("swipe_reader_view")
  ) {
    // Horizontal Ayah Pager
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) { page ->
      val ayah = ayahs[page]
      Box(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        AyahCard(
          ayah = ayah,
          isBookmarked = bookmarkedKeys.contains(ayah.verseKey),
          onToggleBookmark = { onToggleBookmark(ayah) },
          arabicFontSize = arabicFontSize,
          translationFontSize = translationFontSize
        )
      }
    }

    // Bottom Navigation Bar for Page-Turn Mode
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            if (pagerState.currentPage > 0) {
              scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            }
          },
          enabled = pagerState.currentPage > 0,
          modifier = Modifier.testTag("swipe_prev_ayah_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Previous Ayah",
            tint = if (pagerState.currentPage > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "Ayah ${pagerState.currentPage + 1} of ${ayahs.size}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = {
            if (pagerState.currentPage < ayahs.size - 1) {
              scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
          },
          enabled = pagerState.currentPage < ayahs.size - 1,
          modifier = Modifier.testTag("swipe_next_ayah_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next Ayah",
            tint = if (pagerState.currentPage < ayahs.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
          )
        }
      }
    }
  }
}
