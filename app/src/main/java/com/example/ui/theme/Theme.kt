package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ForgeThemeMode {
  DARK,
  LIGHT,
  HIGH_CONTRAST
}

val LocalReducedMotion = staticCompositionLocalOf { false }
val LocalFontScale = staticCompositionLocalOf { 1.0f }

private val DarkColorScheme = darkColorScheme(
  primary = ForgeAmberPrimary,
  onPrimary = Color(0xFF1E293B),
  primaryContainer = Color(0xFF78350F),
  onPrimaryContainer = Color(0xFFFEF3C7),
  secondary = Color(0xFF38BDF8),
  onSecondary = Color(0xFF0F172A),
  secondaryContainer = Color(0xFF0369A1),
  onSecondaryContainer = Color(0xFFE0F2FE),
  tertiary = Color(0xFFA78BFA),
  background = ForgeDarkBg,
  onBackground = ForgeDarkText,
  surface = ForgeDarkSurface,
  onSurface = ForgeDarkText,
  surfaceVariant = ForgeDarkSurfaceVariant,
  onSurfaceVariant = ForgeDarkTextSecondary,
  outline = ForgeDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = ForgeAmberDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFEF3C7),
  onPrimaryContainer = Color(0xFF78350F),
  secondary = Color(0xFF0284C7),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE0F2FE),
  onSecondaryContainer = Color(0xFF0369A1),
  tertiary = Color(0xFF7C3AED),
  background = ForgeLightBg,
  onBackground = ForgeLightText,
  surface = ForgeLightSurface,
  onSurface = ForgeLightText,
  surfaceVariant = ForgeLightSurfaceVariant,
  onSurfaceVariant = ForgeLightTextSecondary,
  outline = ForgeLightBorder
)

private val HighContrastColorScheme = darkColorScheme(
  primary = ForgeHcAccent,
  onPrimary = Color.Black,
  primaryContainer = ForgeHcAccent,
  onPrimaryContainer = Color.Black,
  secondary = Color.White,
  onSecondary = Color.Black,
  background = ForgeHcBg,
  onBackground = ForgeHcText,
  surface = ForgeHcSurface,
  onSurface = ForgeHcText,
  surfaceVariant = Color(0xFF1A1A1A),
  onSurfaceVariant = Color.White,
  outline = ForgeHcBorder
)

@Composable
fun MyApplicationTheme(
  themeMode: ForgeThemeMode = ForgeThemeMode.DARK,
  reducedMotion: Boolean = false,
  fontScale: Float = 1.0f,
  content: @Composable () -> Unit
) {
  val colorScheme: ColorScheme = when (themeMode) {
    ForgeThemeMode.DARK -> DarkColorScheme
    ForgeThemeMode.LIGHT -> LightColorScheme
    ForgeThemeMode.HIGH_CONTRAST -> HighContrastColorScheme
  }

  CompositionLocalProvider(
    LocalReducedMotion provides reducedMotion,
    LocalFontScale provides fontScale
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
