package com.example.ui.mushaf

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JuzInfo
import com.example.model.MushafLine
import com.example.model.MushafLineType
import com.example.model.MushafPageData
import com.example.model.MushafPaperMode
import com.example.model.MushafScript
import com.example.model.MushafToken
import com.example.model.SurahInfo
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.quranFontFamily

private val CreamPaperBg = Color(0xFFFFFDF7)
private val CreamOuterBg = Color(0xFFF5EFE0)
private val CreamInk = Color(0xFF16120B)
private val CreamRule = Color(0xFFE7D9B9)
private val CreamGoldBorder = Color(0xFFC49A36)
private val CreamGoldMarker = Color(0xFFA87B1E)
private val CreamBannerBg = Color(0x1AC9A227)

private val NavyPaperBg = Color(0xFF071222)
private val NavyOuterBg = Color(0xFF040B16)
private val NavyInk = Color(0xFFFFFDF7)
private val NavyRule = Color(0xFF152946)
private val NavyGoldBorder = Color(0xFFD9AE4E)
private val NavyGoldMarker = Color(0xFFF2D98D)
private val NavyBannerBg = Color(0x1AD9AE4E)

/**
 * Renders a single authentic Mushaf page:
 * - Indo-Pak 16-Line Taj Company Layout (16 ruled lines, double ornamental border, Ruku 'ع' margin)
 * - Madinah 15-Line Uthmani Layout (15 justified lines, ornamental gold frame)
 *
 * Strictly prevents any word from wrapping or overflowing across lines via per-line TextMeasurer
 * auto-fit scaling and dynamic RTL edge-to-edge justification.
 */
@Composable
fun MushafPageCanvas(
  page: MushafPageData,
  script: MushafScript,
  paperMode: MushafPaperMode,
  tajweedEnabled: Boolean,
  linesPerPage: Int,
  surahsByNumber: Map<Int, SurahInfo>,
  juzByNumber: Map<Int, JuzInfo>,
  modifier: Modifier = Modifier
) {
  val isCream = paperMode == MushafPaperMode.CLASSIC_CREAM
  val isIndoPak = script == MushafScript.INDOPAK
  val paperBg = if (isCream) CreamPaperBg else NavyPaperBg
  val outerBg = if (isCream) CreamOuterBg else NavyOuterBg
  val inkColor = if (isCream) CreamInk else NavyInk
  val ruleColor = if (isCream) CreamRule else NavyRule
  val goldBorder = if (isCream) CreamGoldBorder else NavyGoldBorder
  val goldMarker = if (isCream) CreamGoldMarker else NavyGoldMarker
  val bannerBg = if (isCream) CreamBannerBg else NavyBannerBg
  val tajweedPalette = remember(paperMode) { TajweedPalettes.forPaperMode(paperMode) }

  val primarySurah = surahsByNumber[page.primarySurahNumber]
  val juzInfo = juzByNumber[page.juzNumber]
  val fontFamily = script.quranFontFamily()

  Box(
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(14.dp))
      .background(outerBg)
      .padding(6.dp)
  ) {
    // Classic double-lined ornamental gold border
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(10.dp))
        .background(paperBg)
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeOuter = 2.dp.toPx()
        val strokeInner = 0.9.dp.toPx()
        val insetOuter = strokeOuter / 2f
        val insetInner = 5.5.dp.toPx()

        // Outer ornamental border
        drawRoundRect(
          color = goldBorder,
          topLeft = Offset(insetOuter, insetOuter),
          size = Size(size.width - strokeOuter, size.height - strokeOuter),
          cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
          style = Stroke(width = strokeOuter)
        )

        // Inner hairline border
        drawRoundRect(
          color = goldBorder.copy(alpha = 0.7f),
          topLeft = Offset(insetInner, insetInner),
          size = Size(size.width - insetInner * 2f, size.height - insetInner * 2f),
          cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
          style = Stroke(width = strokeInner)
        )

        // Corner diamond accents on all 4 corners
        val accentOffset = 10.dp.toPx()
        val accentRadius = 2.2.dp.toPx()
        listOf(
          Offset(accentOffset, accentOffset),
          Offset(size.width - accentOffset, accentOffset),
          Offset(accentOffset, size.height - accentOffset),
          Offset(size.width - accentOffset, size.height - accentOffset)
        ).forEach { center ->
          drawCircle(color = goldBorder, radius = accentRadius, center = center)
        }
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 10.dp, vertical = 9.dp)
      ) {
        // Ornamental Page Top Header: Parah (Right), Page Badge (Center), Surah (Left)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Right side (RTL start): Parah / Juz Name
            Text(
              text = juzInfo?.let { "پاره ${toArabicIndic(it.number)} • ${it.arabicName}" }
                ?: "جزء ${toArabicIndic(page.juzNumber)}",
              fontFamily = AmiriFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = goldMarker,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Start,
              modifier = Modifier.weight(1f)
            )

            // Center: Page Number Badge
            Box(
              modifier = Modifier
                .padding(horizontal = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bannerBg)
                .border(0.8.dp, goldBorder.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                .padding(horizontal = 9.dp, vertical = 1.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = toArabicIndic(page.pageNumber),
                fontFamily = AmiriFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = goldMarker,
                maxLines = 1
              )
            }

            // Left side (RTL end): Surah Name
            Text(
              text = primarySurah?.let { "سُورَةُ ${it.nameArabic}" } ?: "",
              fontFamily = AmiriFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = goldMarker,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.End,
              modifier = Modifier.weight(1f)
            )
          }
        }

        // Top separator rule below header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .height(1.dp)
            .background(goldBorder.copy(alpha = 0.55f))
        )

        // Exact 16-Line (Indo-Pak) or 15-Line (Madinah) Page Body
        BoxWithConstraints(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 3.dp)
        ) {
          val density = LocalDensity.current
          val rukuMarginDp = if (isIndoPak) 18.dp else 0.dp
          val textAreaWidthPx = with(density) {
            (maxWidth - rukuMarginDp - 4.dp).toPx().coerceAtLeast(100f)
          }
          val slotHeightPx = with(density) {
            (maxHeight / linesPerPage.coerceAtLeast(1)).toPx()
          }
          // Compute ideal base Arabic font size proportionally from line height
          val baseFontSizeSp = with(density) {
            val targetPx = if (isIndoPak) slotHeightPx * 0.56f else slotHeightPx * 0.52f
            targetPx.toSp().value.coerceIn(13f, 24f)
          }

          Column(modifier = Modifier.fillMaxSize()) {
            page.lines.forEachIndexed { index, line ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.fillMaxSize(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Outer left margin for Ruku marker ('ع') on Indo-Pak 16-Line pages
                  if (isIndoPak) {
                    Box(
                      modifier = Modifier
                        .width(rukuMarginDp)
                        .fillMaxHeight(),
                      contentAlignment = Alignment.Center
                    ) {
                      if (line.hasRukuEnd) {
                        Text(
                          text = "ع",
                          fontFamily = AmiriFontFamily,
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = goldMarker,
                          maxLines = 1,
                          softWrap = false
                        )
                      }
                    }
                  }

                  // Main line slot (strictly single-line, zero wrap/overflow)
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .fillMaxHeight()
                      .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    when (line.type) {
                      MushafLineType.SURAH_HEADER -> {
                        val surah = surahsByNumber[line.surahNumber]
                        MushafSurahBannerLine(
                          surahArabicName = surah?.nameArabic ?: "",
                          surahEnglishName = surah?.nameSimple ?: "",
                          bannerBg = bannerBg,
                          borderColor = goldBorder,
                          textColor = goldMarker
                        )
                      }

                      MushafLineType.BISMILLAH -> {
                        val bismillahText = if (isIndoPak) {
                          "بِسۡمِ اللهِ الرَّحۡمٰنِ الرَّحِيۡمِ"
                        } else {
                          "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ"
                        }
                        if (tajweedEnabled) {
                          val coloredBismillah = remember(bismillahText, tajweedPalette) {
                            TajweedColorizer.colorizePhrase(bismillahText, tajweedPalette)
                          }
                          Text(
                            text = coloredBismillah,
                            fontFamily = fontFamily,
                            fontSize = (baseFontSizeSp * 0.92f).sp,
                            color = inkColor,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                          )
                        } else {
                          Text(
                            text = bismillahText,
                            fontFamily = fontFamily,
                            fontSize = (baseFontSizeSp * 0.92f).sp,
                            color = inkColor,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                          )
                        }
                      }

                      MushafLineType.TEXT -> {
                        MushafJustifiedLine(
                          tokens = line.tokens,
                          maxWidthPx = textAreaWidthPx,
                          baseFontSizeSp = baseFontSizeSp,
                          fontFamily = fontFamily,
                          inkColor = inkColor,
                          markerColor = goldMarker,
                          tajweedEnabled = tajweedEnabled,
                          tajweedPalette = tajweedPalette,
                          isOpeningOrShortPage = page.pageNumber <= 2
                        )
                      }

                      MushafLineType.EMPTY -> {
                        // Preserves exact 16-line / 15-line vertical cadence on opening & closing pages
                        Spacer(modifier = Modifier.fillMaxSize())
                      }
                    }
                  }
                }

                // Classic Taj Company horizontal ruled line beneath each of the 16 lines
                if (isIndoPak && index < page.lines.lastIndex) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.BottomCenter)
                      .fillMaxWidth()
                      .padding(start = rukuMarginDp)
                      .height(0.6.dp)
                      .background(ruleColor)
                  )
                }
              }
            }
          }
        }

        // Subtle footer metadata (English page number & edition)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp, start = 6.dp, end = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = primarySurah?.nameSimple ?: "",
            fontSize = 9.5.sp,
            color = goldMarker.copy(alpha = 0.8f),
            maxLines = 1
          )
          Text(
            text = "Page ${page.pageNumber}",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = goldMarker,
            maxLines = 1
          )
          Text(
            text = "Juz ${page.juzNumber}",
            fontSize = 9.5.sp,
            color = goldMarker.copy(alpha = 0.8f),
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
private fun MushafSurahBannerLine(
  surahArabicName: String,
  surahEnglishName: String,
  bannerBg: Color,
  borderColor: Color,
  textColor: Color
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.84f)
      .clip(RoundedCornerShape(6.dp))
      .background(bannerBg)
      .border(0.9.dp, borderColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
      .padding(horizontal = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "❖",
        fontSize = 10.sp,
        color = textColor,
        maxLines = 1
      )
      Text(
        text = "سُورَةُ $surahArabicName",
        fontFamily = AmiriFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        maxLines = 1,
        softWrap = false
      )
      Text(
        text = "❖",
        fontSize = 10.sp,
        color = textColor,
        maxLines = 1
      )
    }
  }
}

/**
 * Dynamically measures every token on a Mushaf line using TextMeasurer, auto-fits font size so
 * the line NEVER wraps or overflows, and applies edge-to-edge RTL horizontal justification.
 * Compact ayah markers (﴿١﴾) are rendered at 72% of the main Arabic font size.
 */
@Composable
private fun MushafJustifiedLine(
  tokens: List<MushafToken>,
  maxWidthPx: Float,
  baseFontSizeSp: Float,
  fontFamily: FontFamily,
  inkColor: Color,
  markerColor: Color,
  tajweedEnabled: Boolean,
  tajweedPalette: TajweedPalette,
  isOpeningOrShortPage: Boolean
) {
  if (tokens.isEmpty()) return

  val textMeasurer = rememberTextMeasurer()
  val density = LocalDensity.current
  val minWordGapPx = with(density) { 2.5.dp.toPx() }

  // Compute exact auto-fitted font size & whether line should be justified or centered
  val layoutResult = remember(tokens, maxWidthPx, baseFontSizeSp, fontFamily) {
    fun measureLineWidth(fontSp: Float): Float {
      val wordStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSp.sp,
        fontWeight = FontWeight.Normal
      )
      val markerStyle = TextStyle(
        fontFamily = AmiriFontFamily,
        fontSize = (fontSp * 0.72f).sp,
        fontWeight = FontWeight.Normal
      )
      var total = 0f
      for (token in tokens) {
        val w = when (token) {
          is MushafToken.Word -> textMeasurer.measure(
            text = token.text,
            style = wordStyle,
            maxLines = 1,
            softWrap = false
          ).size.width.toFloat()
          is MushafToken.AyahEnd -> textMeasurer.measure(
            text = formatCompactAyahMarker(token.ayahNumber),
            style = markerStyle,
            maxLines = 1,
            softWrap = false
          ).size.width.toFloat()
        }
        total += w
      }
      val gaps = (tokens.size - 1).coerceAtLeast(0) * minWordGapPx
      return total + gaps
    }

    var fittedSp = baseFontSizeSp
    var measuredWidth = measureLineWidth(fittedSp)

    // Shrink proportionally if line exceeds page width (max 2 fast passes)
    if (measuredWidth > maxWidthPx && measuredWidth > 0f) {
      val ratio = (maxWidthPx / measuredWidth) * 0.97f
      fittedSp = (fittedSp * ratio).coerceAtLeast(9.5f)
      measuredWidth = measureLineWidth(fittedSp)
      if (measuredWidth > maxWidthPx && measuredWidth > 0f) {
        val ratio2 = (maxWidthPx / measuredWidth) * 0.96f
        fittedSp = (fittedSp * ratio2).coerceAtLeast(8.5f)
        measuredWidth = measureLineWidth(fittedSp)
      }
    }

    // Center very short lines (opening frames or final short line of a Surah)
    val shouldCenter = isOpeningOrShortPage ||
      tokens.size <= 2 ||
      measuredWidth < maxWidthPx * 0.56f

    Pair(fittedSp, shouldCenter)
  }

  val fittedSp = layoutResult.first
  val shouldCenter = layoutResult.second
  val markerSp = fittedSp * 0.72f // Compact 70-75% ayah marker ratio

  // Precompute Tajweed AnnotatedStrings per word only when Tajweed Colors are enabled.
  // Because SpanStyle(color) does not alter glyph widths, fittedSp and line boundaries stay 100% identical.
  val tajweedWordMap = remember(tokens, tajweedEnabled, tajweedPalette) {
    if (tajweedEnabled) {
      TajweedColorizer.colorizeLine(tokens, tajweedPalette)
    } else {
      emptyMap()
    }
  }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = if (shouldCenter) {
        Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
      } else {
        Arrangement.SpaceBetween
      },
      verticalAlignment = Alignment.CenterVertically
    ) {
      tokens.forEachIndexed { tokenIdx, token ->
        when (token) {
          is MushafToken.Word -> {
            val colored = tajweedWordMap[tokenIdx]
            if (colored != null) {
              Text(
                text = colored,
                fontFamily = fontFamily,
                fontSize = fittedSp.sp,
                fontWeight = FontWeight.Normal,
                color = inkColor,
                maxLines = 1,
                softWrap = false
              )
            } else {
              Text(
                text = token.text,
                fontFamily = fontFamily,
                fontSize = fittedSp.sp,
                fontWeight = FontWeight.Normal,
                color = inkColor,
                maxLines = 1,
                softWrap = false
              )
            }
          }
          is MushafToken.AyahEnd -> {
            Text(
              text = formatCompactAyahMarker(token.ayahNumber),
              fontFamily = AmiriFontFamily,
              fontSize = markerSp.sp,
              fontWeight = FontWeight.Medium,
              color = markerColor,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }
    }
  }
}

private fun formatCompactAyahMarker(ayahNumber: Int): String =
  "﴿${toArabicIndic(ayahNumber)}﴾"

fun toArabicIndic(number: Int): String {
  val digits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
  return number.toString().map { ch ->
    if (ch in '0'..'9') digits[ch - '0'] else ch
  }.joinToString("")
}
