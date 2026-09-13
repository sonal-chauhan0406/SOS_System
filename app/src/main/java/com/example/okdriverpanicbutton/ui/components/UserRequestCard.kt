package com.example.okdriverpanicbutton.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.data.MockUser
import com.example.okdriverpanicbutton.ui.theme.AccentGreen

import com.example.okdriverpanicbutton.ui.theme.GlassBorder
import com.example.okdriverpanicbutton.ui.theme.MutedGray
import com.example.okdriverpanicbutton.ui.theme.RosePrimary
import com.example.okdriverpanicbutton.ui.theme.SurfaceElevated
import com.example.okdriverpanicbutton.ui.theme.TextPrimary
import com.example.okdriverpanicbutton.ui.theme.TextSecondary

/**
 * Glassmorphic card prompting a user with "Are you available to help?"
 * Slides in from the bottom with a fade-in animation.
 */
@Composable
fun UserRequestCard(
    user: MockUser,
    distance: Double,
    onYes: () -> Unit,
    onNo: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Trigger visibility animation on appearance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(user.id) {
        visible = false
        // Small delay to reset animation when user changes
        kotlinx.coroutines.delay(50)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        modifier = modifier
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            cornerRadius = 28.dp,
            contentPadding = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar & Name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Text(
                        text = user.avatarEmoji,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "%.1f km away".format(distance / 10.0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = RosePrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(1.dp)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Are you available to help?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Yes / No buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onNo,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MutedGray
                        ),
                        border = BorderStroke(1.dp, MutedGray.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "✕  No",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onYes,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RosePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "✓  Yes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
