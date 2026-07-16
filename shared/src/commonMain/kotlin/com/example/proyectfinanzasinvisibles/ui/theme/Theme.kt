package com.example.proyectfinanzasinvisibles.ui.theme

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
    secondaryContainer = Color(0xFF45464E),
    onSecondaryContainer = Color(0xFFB4B4BD),
    
    tertiary = StealthWhite,
    onTertiary = Color(0xFF303033),
    tertiaryContainer = Color(0xFFE4E1E5),
    onTertiaryContainer = Color(0xFF656467),
    
    error = StealthError,
    onError = StealthOnError,
    errorContainer = StealthErrorContainer,
    onBackground = StealthOnSurface,
    
    surface = StealthBlack,
    onSurface = StealthOnSurface,
    surfaceVariant = StealthSurfaceHighest,
    onSurfaceVariant = StealthOnSurfaceVariant,
    
    outline = StealthOutline,
    outlineVariant = StealthOutlineVariant,
    
    inverseSurface = StealthOnSurface,
    inverseOnSurface = Color(0xFF313030),
    inversePrimary = Color(0xFF5D5F5F)
)

@Composable
fun InvisibleInsightsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StealthColorScheme,
        typography = Typography,
        content = content
    )
}
