package com.example.okdriverpanicbutton.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.data.MockUser
import com.example.okdriverpanicbutton.ui.theme.AccentGreen
import com.example.okdriverpanicbutton.ui.theme.GlassBorder
import com.example.okdriverpanicbutton.ui.theme.RosePrimary
import com.example.okdriverpanicbutton.ui.theme.SurfaceElevated
import com.example.okdriverpanicbutton.ui.theme.TextPrimary
import com.example.okdriverpanicbutton.ui.theme.TextSecondary

/**
 * Glassmorphic success card showing confirmed help with ETA and location.
 * Features an animated checkmark drawn with Canvas.
 */
@Composable
fun ConfirmedCard(
    user: MockUser,
    eta: String,
    location: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val checkProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        visible = true
        checkProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, delayMillis = 300, easing = FastOutSlowInEasing)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400)),
        modifier = modifier
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            cornerRadius = 28.dp,
            contentPadding = 28.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Animated checkmark circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RosePrimary.copy(alpha = 0.15f))
                ) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val progress = checkProgress.value
                        val strokeWidth = 4.dp.toPx()

                        // Draw circle outline
                        drawCircle(
                            color = RosePrimary,
                            radius = (size.minDimension / 2f) + 12.dp.toPx(),
                            style = Stroke(width = strokeWidth)
                        )

                        // Draw checkmark
                        if (progress > 0f) {
                            val startX = size.width * 0.2f
                            val startY = size.height * 0.5f
                            val midX = size.width * 0.42f
                            val midY = size.height * 0.75f
                            val endX = size.width * 0.82f
                            val endY = size.height * 0.25f

                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(startX, startY)
                                if (progress <= 0.5f) {
                                    val p = progress * 2f
                                    lineTo(
                                        startX + (midX - startX) * p,
                                        startY + (midY - startY) * p
                                    )
                                } else {
                                    lineTo(midX, midY)
                                    val p = (progress - 0.5f) * 2f
                                    lineTo(
                                        midX + (endX - midX) * p,
                                        midY + (endY - midY) * p
                                    )
                                }
                            }

                            drawPath(
                                path = path,
                                color = RosePrimary,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Help is on the way!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = RosePrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // User info row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                    ) {
                        Text(text = user.avatarEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "is coming to help",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ETA & Location details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(
                        label = "ETA",
                        value = eta,
                        modifier = Modifier.weight(1f)
                    )
                    DetailColumn(
                        label = "Location",
                        value = location,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )
    }
}
