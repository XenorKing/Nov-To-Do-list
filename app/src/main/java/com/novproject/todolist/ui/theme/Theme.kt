package com.novproject.todolist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF6C63FF)
val PurpleLight = Color(0xFF9B94FF)
val Cyan = Color(0xFF00E5FF)
val Background = Color(0xFF12101E)
val Surface = Color(0xFF1E1B2E)
val SurfaceVariant = Color(0xFF2A2640)
val OnBackground = Color(0xFFEEEEEE)
val OnSurface = Color(0xFFCCCCCC)
val Error = Color(0xFFCF6679)

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    secondary = Cyan,
    onSecondary = Color.Black,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = Error,
)

@Composable
fun NovToDoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
