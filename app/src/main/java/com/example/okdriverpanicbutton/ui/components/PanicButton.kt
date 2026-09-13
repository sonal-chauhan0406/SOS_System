package com.example.okdriverpanicbutton.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.ui.theme.RoseDark
import com.example.okdriverpanicbutton.ui.theme.RoseMedium
import com.example.okdriverpanicbutton.ui.theme.RosePrimary

/**
 * 3D tactile panic button with layered shadows, radial gradient,
 * pulsing glow animation, and a pressed-state scale effect.
 * Sized to fill a prominent area on the main SOS screen.
 */
@Composable
fun PanicButton(
    onPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "buttonScale"
    )

    // Pulsing glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.scale(scale)
    ) {
        // Layer 1: Outer pulsing glow ring
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            RosePrimary.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Layer 2: Shadow ring for 3D depth
        Box(
            modifier = Modifier
                .size(210.dp)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        // Outer dark shadow (bottom-right)
                        val darkShadowPaint = Paint().apply {
                            asFrameworkPaint().apply {
                                isAntiAlias = true
                                color = Color.Transparent.toArgb()
                                setShadowLayer(
                                    24f,
                                    8f,
                                    8f,
                                    Color.Black.copy(alpha = 0.6f).toArgb()
                                )
                            }
                        }
                        canvas.drawCircle(
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2 - 4f,
                            paint = darkShadowPaint
                        )

                        // Inner highlight shadow (top-left) for 3D emboss
                        val lightShadowPaint = Paint().apply {
                            asFrameworkPaint().apply {
                                isAntiAlias = true
                                color = Color.Transparent.toArgb()
                                setShadowLayer(
                                    16f,
                                    -4f,
                                    -4f,
                                    RoseMedium.copy(alpha = 0.3f).toArgb()
                                )
                            }
                        }
                        canvas.drawCircle(
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2 - 4f,
                            paint = lightShadowPaint
                        )
                    }
                }
        )

        // Layer 3: Main button circle with radial gradient
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(RosePrimary)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onPress()
                        }
                    )
                }
        ) {
            Text(
                text = "SOS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )
        }
    }
}
