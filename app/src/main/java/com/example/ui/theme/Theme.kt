package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeMode

val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = Color.White,
  primaryContainer = EmeraldPrimaryContainer,
  onPrimaryContainer = OnEmeraldContainer,
  secondary = GoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = GoldSecondaryContainer,
  onSecondaryContainer = OnGoldSecondaryContainer,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurfaceVariant,
  outline = LightOutline,
  outlineVariant = LightOutlineVariant
)

val DarkColorScheme = darkColorScheme(
  primary = DarkGoldPrimary,
  onPrimary = DarkBackground,
  primaryContainer = DarkGoldBadgeBg,
  onPrimaryContainer = DarkGoldLight,
  secondary = DarkGoldPrimary,
  onSecondary = DarkBackground,
  secondaryContainer = DarkGoldBadgeBg,
  onSecondaryContainer = DarkGoldLight,
  tertiary = DarkGoldLight,
  onTertiary = DarkBackground,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  outline = DarkOutline,
  outlineVariant = DarkOutlineVariant
)

@Immutable
data class NoorExtendedColors(
  val isDark: Boolean,
  val topBarGradientStart: Color,
  val topBarGradientEnd: Color,
  val outerContainerStart: Color,
  val outerContainerEnd: Color,
  val innerCardBackground: Color,
  val fullAyahBoxBackground: Color,
  val wordCardBackground: Color,
  val activeWordCardBackground: Color,
  val activeWordCardBorder: Color,
  val translationBoxBackground: Color,
  val cardBorder: Color,
  val primaryGold: Color,
  val lightGold: Color,
  val badgeBackground: Color,
  val badgeBorder: Color,
  val badgeText: Color,
  val sectionPillBackground: Color,
  val sectionPillBorder: Color,
  val sectionPillText: Color,
  val translationAccentStart: Color,
  val translationAccentEnd: Color,
  val fullAyahArabicText: Color,
  val wordArabicText: Color,
  val wordArabicActiveText: Color,
  val surahTitleText: Color,
  val surahArabicSubtitleText: Color,
  val wordEnglishText: Color,
  val mutedText: Color,
  val topBarActionIconTint: Color,
  val juzArabicNameText: Color
) {
  val topBarBrush: Brush
    get() = Brush.verticalGradient(listOf(topBarGradientStart, topBarGradientEnd))

  val outerContainerBrush: Brush
    get() = Brush.verticalGradient(listOf(outerContainerStart, outerContainerEnd))

  val translationAccentBrush: Brush
    get() = Brush.verticalGradient(listOf(translationAccentStart, translationAccentEnd))
}

val LightNoorColors = NoorExtendedColors(
  isDark = false,
  topBarGradientStart = LightSurface,
  topBarGradientEnd = LightSurface,
  outerContainerStart = LightSurface,
  outerContainerEnd = LightSurface,
  innerCardBackground = LightSurface,
  fullAyahBoxBackground = LightSurfaceVariant.copy(alpha = 0.25f),
  wordCardBackground = LightSurfaceVariant.copy(alpha = 0.65f),
  activeWordCardBackground = EmeraldPrimaryContainer,
  activeWordCardBorder = GoldSecondary,
  translationBoxBackground = LightSurfaceVariant.copy(alpha = 0.5f),
  cardBorder = LightOutlineVariant,
  primaryGold = Color(0xFFDFC07C),
  lightGold = GoldSecondary,
  badgeBackground = EmeraldPrimaryContainer,
  badgeBorder = GoldSecondary.copy(alpha = 0.4f),
  badgeText = OnEmeraldContainer,
  sectionPillBackground = GoldSecondary.copy(alpha = 0.2f),
  sectionPillBorder = Color.Transparent,
  sectionPillText = GoldSecondary,
  translationAccentStart = GoldSecondary,
  translationAccentEnd = GoldSecondary,
  fullAyahArabicText = LightOnSurface,
  wordArabicText = EmeraldPrimary,
  wordArabicActiveText = OnEmeraldContainer,
  surahTitleText = LightOnSurfaceVariant,
  surahArabicSubtitleText = EmeraldPrimary,
  wordEnglishText = LightOnSurfaceVariant,
  mutedText = LightOnSurfaceVariant,
  topBarActionIconTint = LightOnSurfaceVariant,
  juzArabicNameText = EmeraldPrimary
)

val DarkNoorColors = NoorExtendedColors(
  isDark = true,
  topBarGradientStart = DarkSurface,
  topBarGradientEnd = DarkSurfaceGradientEnd,
  outerContainerStart = DarkSurface,
  outerContainerEnd = DarkSurfaceGradientEnd,
  innerCardBackground = DarkInnerCard,
  fullAyahBoxBackground = DarkInnerCard,
  wordCardBackground = DarkInnerCard,
  activeWordCardBackground = DarkActiveWordCard,
  activeWordCardBorder = DarkGoldPrimary,
  translationBoxBackground = DarkInnerCard,
  cardBorder = DarkOutline,
  primaryGold = DarkGoldPrimary,
  lightGold = DarkGoldLight,
  badgeBackground = DarkGoldBadgeBg,
  badgeBorder = DarkGoldBadgeBorder,
  badgeText = DarkGoldLight,
  sectionPillBackground = DarkGoldBadgeBg,
  sectionPillBorder = DarkGoldBadgeBorder,
  sectionPillText = DarkGoldLight,
  translationAccentStart = DarkGoldLight,
  translationAccentEnd = DarkGoldDeep,
  fullAyahArabicText = DarkArabicFullText,
  wordArabicText = DarkGoldLight,
  wordArabicActiveText = DarkArabicFullText,
  surahTitleText = DarkOnSurface,
  surahArabicSubtitleText = DarkOnSurfaceVariant,
  wordEnglishText = DarkOnSurfaceVariant,
  mutedText = DarkMutedText,
  topBarActionIconTint = DarkGoldPrimary,
  juzArabicNameText = DarkGoldLight
)

val LocalNoorColors = staticCompositionLocalOf { LightNoorColors }

@Composable
fun NoorQuranTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
  }

  val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
  val extendedColors = if (isDark) DarkNoorColors else LightNoorColors

  CompositionLocalProvider(LocalNoorColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = AppTypography,
      content = content
    )
  }
}
