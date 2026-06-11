package com.novaroject.novtodolist.auth.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val CF_Purple = Color(0xFFA855F7)
private val CF_Cyan   = Color(0xFF00E5FF)

@Composable
fun CyberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val focusAnim by animateFloatAsState(if (isFocused) 1f else 0f, tween(400), label = "focus")

    val inf = rememberInfiniteTransition(label = "cyberSweep")
    val sweep by inf.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "sweep"
    )

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource,
        singleLine = singleLine,
        modifier = modifier
            .drawWithContent {
                drawContent()
                if (focusAnim > 0.01f) {
                    val sw = 2.2f.dp.toPx()
                    val cr = 16.dp.toPx()
                    val tl = Offset(sw / 2f, sw / 2f)
                    val sz = Size(size.width - sw, size.height - sw)
                    // glow
                    drawRoundRect(
                        color = CF_Purple.copy(alpha = 0.18f * focusAnim),
                        topLeft = tl, size = sz,
                        cornerRadius = CornerRadius(cr),
                        style = Stroke(width = sw * 6f)
                    )
                    // sweeping arc
                    withTransform({
                        rotate(sweep * focusAnim, Offset(size.width / 2f, size.height / 2f))
                    }) {
                        drawRoundRect(
                            brush = Brush.sweepGradient(
                                0f    to Color.Transparent,
                                0.06f to CF_Purple.copy(alpha = 0.5f * focusAnim),
                                0.14f to CF_Cyan.copy(alpha = focusAnim),
                                0.22f to CF_Purple.copy(alpha = 0.5f * focusAnim),
                                0.40f to Color.Transparent,
                                1f    to Color.Transparent,
                            ),
                            topLeft = tl, size = sz,
                            cornerRadius = CornerRadius(cr),
                            style = Stroke(width = sw)
                        )
                    }
                }
            },
        colors = TextFieldDefaults.colors(
            focusedContainerColor    = Color(0xFF14112A),
            unfocusedContainerColor  = Color(0xFF0E0C1C),
            focusedIndicatorColor    = Color.Transparent,
            unfocusedIndicatorColor  = Color.Transparent,
            focusedTextColor         = Color.White,
            unfocusedTextColor       = Color(0xFFCCCCDD),
            cursorColor              = CF_Cyan,
            focusedPlaceholderColor  = Color(0xFF5555AA),
            unfocusedPlaceholderColor = Color(0xFF4A4A7A),
            focusedLeadingIconColor  = CF_Cyan.copy(alpha = 0.9f),
            unfocusedLeadingIconColor = Color(0xFF6666AA),
            focusedTrailingIconColor  = Color(0xFFAAAACC),
            unfocusedTrailingIconColor = Color(0xFF666688),
        )
    )
}
