package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LuckyDarkColorScheme = darkColorScheme(
    primary = LuckyGold,
    onPrimary = Color.Black,
    primaryContainer = LuckyGoldDark,
    onPrimaryContainer = LuckyGold,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003740),
    onSecondaryContainer = NeonCyan,
    tertiary = SunsetPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C001F),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF1E2235)
)

@Composable
fun LuckyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuckyDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun OmniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = LuckyTheme(darkTheme, dynamicColor, content)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = LuckyTheme(darkTheme, dynamicColor, content)

