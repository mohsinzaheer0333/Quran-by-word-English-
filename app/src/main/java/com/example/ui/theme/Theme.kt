package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
  primary = DarkEmeraldPrimary,
  onPrimary = Color(0xFF042018),
  primaryContainer = DarkEmeraldPrimaryContainer,
  onPrimaryContainer = DarkOnEmeraldContainer,
  secondary = DarkGoldSecondary,
  onSecondary = Color(0xFF261D0A),
  secondaryContainer = DarkGoldSecondaryContainer,
  onSecondaryContainer = DarkOnGoldSecondaryContainer,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  outline = DarkOutline,
  outlineVariant = DarkOutlineVariant
)

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

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
