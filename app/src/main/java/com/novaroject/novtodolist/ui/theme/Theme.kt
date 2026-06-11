package com.novaroject.novtodolist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonCyan    = Color(0xFF00E5FF)
val NeonPurple  = Color(0xFFA855F7)
val NeonPink    = Color(0xFFFF2D78)
val DarkBg      = Color(0xFF04030E)
val DarkSurface = Color(0xFF0A0818)
val DarkCard    = Color(0xFF100D20)
val TextPrimary = Color(0xFFE8E8FF)
val TextSecond  = Color(0xFF7878AA)

val CyberColors = darkColorScheme(
    primary            = NeonCyan,
    onPrimary          = Color.Black,
    primaryContainer   = Color(0xFF001F2A),
    secondary          = NeonPurple,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFF1A0033),
    tertiary           = NeonPink,
    onTertiary         = Color.White,
    background         = DarkBg,
    surface            = DarkSurface,
    surfaceVariant     = DarkCard,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextSecond,
    error              = NeonPink,
    outline            = Color(0xFF221F3A),
)

@Composable
fun NovToDoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberColors,
        typography  = Typography,
        content     = content
    )
}
