package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = DarkBackground,
    primaryContainer = TealPrimary,
    onPrimaryContainer = DarkOnSurface,
    secondary = SlateBlue,
    onSecondary = DarkBackground,
    tertiary = IndigoAccent,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    error = RoseCritical
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = LightSurface,
    primaryContainer = TealAccent,
    onPrimaryContainer = LightOnSurface,
    secondary = SlateBlue,
    onSecondary = LightSurface,
    tertiary = IndigoAccent,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceMuted,
    error = RoseCritical
)

@Composable
fun LifeOSTheme(
    themeMode: String = "Dark", // System, Light, Dark
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
