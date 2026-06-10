package com.novaroject.novtodolist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple      = Color(0xFF7B61FF)
val PurpleLight = Color(0xFF9B8FFF)
val Cyan        = Color(0xFF00E5FF)
val Background  = Color(0xFF12101E)
val Surface     = Color(0xFF1E1B2E)
val SurfaceVar  = Color(0xFF2A2640)
val OnBg        = Color(0xFFEEEEEE)
val OnSurf      = Color(0xFFBBBBCC)
val ErrorColor  = Color(0xFFCF6679)

private val DarkColors = darkColorScheme(
    primary         = Purple,
    onPrimary       = Color.White,
    primaryContainer = SurfaceVar,
    secondary       = Cyan,
    onSecondary     = Color.Black,
    background      = Background,
    surface         = Surface,
    surfaceVariant  = SurfaceVar,
    onBackground    = OnBg,
    onSurface       = OnSurf,
    error           = ErrorColor,
)

@Composable
fun NovToDoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography  = Typography,
        content     = content
    )
}
