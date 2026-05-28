package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color.White,
    secondary = PurpleGrey80,
    onSecondary = Color.White,
    tertiary = Pink80,
    onTertiary = Color.Black,
    background = SlateBlack,
    onBackground = Color.White,
    surface = SlateGrey,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF222834),
    onSurfaceVariant = Color(0xFFC0CAD9),
    outline = BorderSlate
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Force cinematic dark mode as requested for supreme media theater feel
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
