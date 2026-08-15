package com.example.proyectfinanzasinvisibles.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StealthColorScheme = darkColorScheme(
    primary = StealthSilver,
    onPrimary = StealthBlack,
    primaryContainer = StealthSilverContainer,
    onPrimaryContainer = StealthOnSurface,
    secondary = StealthOnSurfaceVariant,
    onSecondary = StealthBlack,
    secondaryContainer = StealthSurfaceHighest,
    onSecondaryContainer = StealthOnSurface,
    tertiary = StealthBlue,
    onTertiary = StealthBlack,
    tertiaryContainer = StealthBlueContainer,
    onTertiaryContainer = StealthOnSurface,
    error = StealthError,
    onError = StealthOnError,
    errorContainer = StealthErrorContainer,
    onErrorContainer = StealthOnSurface,
    background = StealthBlack,
    onBackground = StealthOnSurface,
    surface = StealthSurface,
    onSurface = StealthOnSurface,
    surfaceVariant = StealthSurfaceHighest,
    onSurfaceVariant = StealthOnSurfaceVariant,
    outline = StealthOutline,
    outlineVariant = StealthOutlineVariant
)

@Composable
fun StealthMonochromeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StealthColorScheme,
        typography = Typography,
        content = content
    )
}
