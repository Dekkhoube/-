package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    onPrimary = Emerald900,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = GoldLight,
    onTertiary = Emerald900,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald50,
    onPrimaryContainer = Emerald900,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = Gold50,
    onSecondaryContainer = GoldDark,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = OffWhite,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = ErrorRed
)

@Composable
fun NoorIslamicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
