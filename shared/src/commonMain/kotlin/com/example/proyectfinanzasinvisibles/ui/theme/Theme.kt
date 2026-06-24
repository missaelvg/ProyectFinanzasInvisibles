package com.example.proyectfinanzasinvisibles.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = NeutralGray,
    background = DarkBackground,
    surface = SurfaceColor,
    onPrimary = OnSurfaceWhite,
    onSecondary = DarkBackground,
    onTertiary = OnSurfaceWhite,
    onBackground = OnSurfaceWhite,
    onSurface = OnSurfaceWhite,
)

@Composable
fun InvisibleInsightsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
