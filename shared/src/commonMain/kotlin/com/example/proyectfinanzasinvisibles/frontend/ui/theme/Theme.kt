package com.example.proyectfinanzasinvisibles.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StealthColorScheme = darkColorScheme(
    primary = StealthWhite,
    onPrimary = Color(0xFF2F3131),
    primaryContainer = Color(0xFFE2E2E2),
    onPrimaryContainer = Color(0xFF636565),
    secondary = Color(0xFFC6C6CF),
    onSecondary = Color(0xFF2F3037),
    error = StealthError,
    onError = StealthOnError,
    errorContainer = StealthErrorContainer,
    onBackground = StealthOnSurface,
    surface = StealthBlack,
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
