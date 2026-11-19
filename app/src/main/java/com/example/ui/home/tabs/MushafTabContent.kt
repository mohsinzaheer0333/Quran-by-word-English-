package com.example.ui.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JuzInfo
import com.example.model.MushafEditionData
import com.example.model.MushafPageData
import com.example.model.MushafPaperMode
import com.example.model.MushafScript
import com.example.model.SurahInfo
import com.example.ui.mushaf.MushafJumpDialog
import com.example.ui.mushaf.MushafPageCanvas
import com.example.ui.mushaf.MushafToolbar
import com.example.ui.mushaf.TajweedLegendBar
import com.example.ui.theme.LocalNoorColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val MIN_ZOOM = 1.0f
private const val MAX_ZOOM = 2.5f
private const val ZOOM_STEP = 0.25f

/**
 * Dedicated Arabic-Only Mushaf Tab:
 * - Authentic Indo-Pak 16-Line Taj Company (548 pages) & Madinah 15-Line Uthmani (604 pages)
 * - Classic Cream Paper (#FFFDF7) vs Midnight Navy (#071222) with gold ornamental borders
 * - Authentic RTL page swiping + interactive 2-finger Pinch-to-Zoom (1.0x..2.5x) with pan & floating zoom controls
 */
@Composable
fun MushafTabContent(
  edition: MushafEditionData?,
  isLoading: Boolean,
  currentPage: Int,
  selectedScript: MushafScript,
  paperMode: MushafPaperMode,
  tajweedEnabled: Boolean,
  surahs: List<SurahInfo>,
  juzList: List<JuzInfo>,
  isCurrentPageBookmarked: Boolean,
  onSelectScript: (MushafScript) -> Unit,
  onSelectPaperMode: (MushafPaperMode) -> Unit,
  onToggleTajweed: () -> Unit,
  onPageChanged: (Int) -> Unit,
  onToggleBookmark: (MushafPageData, SurahInfo?) -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  val scope = rememberCoroutineScope()
  var showJumpDialog by remember { mutableStateOf(false) }
  var isLegendExpanded by remember { mutableStateOf(tajweedEnabled) }

  // Automatically expand/collapse legend cleanly when user toggles Tajweed ON/OFF
  LaunchedEffect(tajweedEnabled) {
    isLegendExpanded = tajweedEnabled
  }

  // Interactive Zoom & Pan state (1.0x up to 2.5x)
  var zoomScale by remember { mutableFloatStateOf(MIN_ZOOM) }
  var panOffset by remember { mutableStateOf(Offset.Zero) }
  var viewportSize by remember { mutableStateOf(IntSize.Zero) }

  fun clampOffset(offset: Offset, scale: Float, size: IntSize): Offset {
    if (scale <= 1.01f || size.width == 0 || size.height == 0) return Offset.Zero
    val maxX = (size.width * (scale - 1f)) / 2f
    val maxY = (size.height * (scale - 1f)) / 2f
    return Offset(
      x = offset.x.coerceIn(-maxX, maxX),
      y = offset.y.coerceIn(-maxY, maxY)
    )
  }

  fun updateZoom(newScale: Float) {
    val clamped = newScale.coerceIn(MIN_ZOOM, MAX_ZOOM)
    zoomScale = clamped
    panOffset = clampOffset(panOffset, clamped, viewportSize)
  }

  fun resetZoom() {
    zoomScale = MIN_ZOOM
    panOffset = Offset.Zero
  }

  val surahsByNumber = remember(surahs) { surahs.associateBy { it.number } }
  val juzByNumber = remember(juzList) { juzList.associateBy { it.number } }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("mushaf_tab_content")
  ) {
    val totalPages = edition?.totalPages ?: if (selectedScript == MushafScript.INDOPAK) 548 else 604
    val activePageData = edition?.getPage(currentPage)
    val activeSurah = activePageData?.let { surahsByNumber[it.primarySurahNumber] }

    // Top Mushaf Controls Toolbar
    MushafToolbar(
      selectedScript = selectedScript,
      paperMode = paperMode,
      tajweedEnabled = tajweedEnabled,
      currentPage = currentPage.coerceIn(1, totalPages),
      totalPages = totalPages,
      surahLabel = activeSurah?.nameSimple ?: "Surah 1",
      juzNumber = activePageData?.juzNumber ?: 1,
      isBookmarked = isCurrentPageBookmarked,
      onSelectScript = { nextScript ->
        resetZoom()
        onSelectScript(nextScript)
      },
      onSelectPaperMode = onSelectPaperMode,
      onToggleTajweed = onToggleTajweed,
      onPrevPage = {
        if (currentPage > 1) {
          resetZoom()
          onPageChanged(currentPage - 1)
        }
      },
      onNextPage = {
        if (currentPage < totalPages) {
          resetZoom()
          onPageChanged(currentPage + 1)
        }
      },
      onOpenJumpDialog = { showJumpDialog = true },
      onToggleBookmark = {
        activePageData?.let { onToggleBookmark(it, activeSurah) }
      }
    )

    // Mushaf Page Viewport
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .clipToBounds()
        .onSizeChanged { viewportSize = it }
    ) {
      if (isLoading || edition == null) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
              color = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (selectedScript == MushafScript.INDOPAK) {
                "Loading Indo-Pak 16-Line Mushaf..."
              } else {
                "Loading Madinah 15-Line Mushaf..."
              },
              style = MaterialTheme.typography.bodySmall,
              color = noorColors.mutedText
            )
          }
        }
      } else {
        // Key pager on edition so switching between 548-page and 604-page resets cleanly
        androidx.compose.runtime.key(edition.script) {
          val initialIndex = (currentPage - 1).coerceIn(0, edition.totalPages - 1)
          val pagerState = rememberPagerState(
            initialPage = initialIndex,
            pageCount = { edition.totalPages }
          )

          // Sync external page jumps (buttons / jump dialog / bookmark) -> Pager
          LaunchedEffect(currentPage, edition.totalPages) {
            val targetIdx = (currentPage - 1).coerceIn(0, edition.totalPages - 1)
            if (pagerState.currentPage != targetIdx) {
              pagerState.scrollToPage(targetIdx)
            }
          }

          // Sync user RTL swipes -> ViewModel
          val latestCurrentPage by rememberUpdatedState(currentPage)
          val latestOnPageChanged by rememberUpdatedState(onPageChanged)
          LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
              .distinctUntilChanged()
              .collect { idx ->
                val pageNum = idx + 1
                if (pageNum != latestCurrentPage) {
                  resetZoom()
                  latestOnPageChanged(pageNum)
                }
              }
          }

          // Authentic RTL Book Direction (Swipe Left-to-Right to advance forward to next page)
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .pointerInput(edition.script) {
                  awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                      val event = awaitPointerEvent()
                      val pointerCount = event.changes.count { it.pressed }
                      if (pointerCount >= 2) {
                        // 2-finger Pinch-to-Zoom + Pan
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()
                        val nextScale = (zoomScale * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)
                        zoomScale = nextScale
                        panOffset = clampOffset(panOffset + panChange, nextScale, viewportSize)
                        event.changes.forEach { it.consume() }
                      } else if (zoomScale > 1.02f && pointerCount == 1) {
                        // Single-finger Pan while zoomed in
                        val panChange = event.calculatePan()
                        if (panChange != Offset.Zero) {
                          panOffset = clampOffset(panOffset + panChange, zoomScale, viewportSize)
                          event.changes.forEach { it.consume() }
                        }
                      }
                    } while (event.changes.any { it.pressed })
                  }
                }
            ) {
              HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                userScrollEnabled = zoomScale <= 1.02f,
                key = { idx -> idx + 1 },
                modifier = Modifier
                  .fillMaxSize()
                  .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = panOffset.x
                    translationY = panOffset.y
                  }
                  .testTag("mushaf_pager")
              ) { pageIdx ->
                val pageData = edition.pages[pageIdx]
                MushafPageCanvas(
                  page = pageData,
                  script = edition.script,
                  paperMode = paperMode,
                  tajweedEnabled = tajweedEnabled,
                  linesPerPage = edition.linesPerPage,
                  surahsByNumber = surahsByNumber,
                  juzByNumber = juzByNumber
                )
              }
            }
          }
        }

        // Subtle Floating Accessibility Zoom Controls (+ / Zoom Level / - / Reset)
        FloatingZoomControls(
          zoomScale = zoomScale,
          onZoomOut = { updateZoom(zoomScale - ZOOM_STEP) },
          onZoomIn = { updateZoom(zoomScale + ZOOM_STEP) },
          onReset = { resetZoom() },
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 12.dp, bottom = 12.dp)
        )
      }
    }

    // Collapsible English-Only Tajweed Color Guide below the Mushaf page frame
    TajweedLegendBar(
      paperMode = paperMode,
      tajweedEnabled = tajweedEnabled,
      expanded = isLegendExpanded,
      onToggleExpanded = { isLegendExpanded = !isLegendExpanded }
    )
  }

  if (showJumpDialog && edition != null) {
    MushafJumpDialog(
      edition = edition,
      currentPage = currentPage,
      surahs = surahs,
      juzList = juzList,
      onDismiss = { showJumpDialog = false },
      onJumpToPage = { targetPage ->
        showJumpDialog = false
        resetZoom()
        scope.launch { onPageChanged(targetPage) }
      }
    )
  }
}

@Composable
private fun FloatingZoomControls(
  zoomScale: Float,
  onZoomOut: () -> Unit,
  onZoomIn: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  val noorColors = LocalNoorColors.current
  val isZoomed = zoomScale > 1.02f
  val containerBg = if (noorColors.isDark) {
    noorColors.topBarGradientStart.copy(alpha = 0.88f)
  } else {
    MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
  }
  val accentColor = if (noorColors.isDark) noorColors.primaryGold else MaterialTheme.colorScheme.primary

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(containerBg)
      .border(1.dp, if (noorColors.isDark) noorColors.badgeBorder else noorColors.cardBorder, RoundedCornerShape(14.dp))
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .testTag("mushaf_zoom_controls"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    IconButton(
      onClick = onZoomOut,
      enabled = zoomScale > MIN_ZOOM + 0.01f,
      modifier = Modifier
        .size(38.dp)
        .testTag("mushaf_zoom_out_button")
    ) {
      Icon(
        imageVector = Icons.Default.Remove,
        contentDescription = "Zoom Out",
        tint = if (zoomScale > MIN_ZOOM + 0.01f) accentColor else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(18.dp)
      )
    }

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable(enabled = isZoomed, onClick = onReset)
        .padding(horizontal = 6.dp, vertical = 4.dp)
        .testTag("mushaf_zoom_readout"),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "${"%.2f".format(zoomScale).trimEnd('0').trimEnd('.')}x",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        ),
        color = if (noorColors.isDark) noorColors.lightGold else MaterialTheme.colorScheme.onSurface
      )
    }

    IconButton(
      onClick = onZoomIn,
      enabled = zoomScale < MAX_ZOOM - 0.01f,
      modifier = Modifier
        .size(38.dp)
        .testTag("mushaf_zoom_in_button")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Zoom In",
        tint = if (zoomScale < MAX_ZOOM - 0.01f) accentColor else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(18.dp)
      )
    }

    if (isZoomed) {
      IconButton(
        onClick = onReset,
        modifier = Modifier
          .size(38.dp)
          .testTag("mushaf_zoom_reset_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset Zoom",
          tint = accentColor,
          modifier = Modifier.size(17.dp)
        )
      }
    }
  }
}
