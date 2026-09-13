package com.example.okdriverpanicbutton.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.ui.theme.RosePrimary

/**
 * Full-screen overlay shown during the countdown.
 * Features a large animated number and a circular progress indicator.
 */
@Composable
fun CountdownOverlay(
    secondsRemaining: Int,
    totalSeconds: Int,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scale animation for the number
    val numberScale = remember { Animatable(0.5f) }
    LaunchedEffect(secondsRemaining) {
        numberScale.snapTo(0.5f)
        numberScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    }

    val progress = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Radial gradient background in the center
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(RosePrimary.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                )

                // Background track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = RosePrimary.copy(alpha = 0.2f),
                    strokeWidth = 12.dp
                )

                // Foreground animated track
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = RosePrimary,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Large countdown number with scale animation
                    Text(
                        text = "$secondsRemaining",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 80.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.scale(numberScale.value)
                    )
                    Text(
                        text = "seconds",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            TextButton(onClick = onCancel) {
                Text(
                    text = "CANCEL",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.White
                )
            }
        }
    }
}
