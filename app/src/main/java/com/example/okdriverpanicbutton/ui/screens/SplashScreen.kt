package com.example.okdriverpanicbutton.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.ui.theme.RoseDark
import com.example.okdriverpanicbutton.ui.theme.RosePrimary
import kotlinx.coroutines.delay

/**
 * Animated splash/preloader screen with a solid red background.
 * Shows a rounded-square SOS icon, fades in tagline text,
 * then auto-navigates to the Main SOS screen after 2 seconds.
 */
@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
) {
    // Animation states
    val iconScale = remember { Animatable(0.3f) }
    val iconAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Phase 1: Icon appears with scale + fade
        iconAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        iconScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))

        // Phase 2: Title text fades in
        delay(200)
        textAlpha.animateTo(1f, tween(500))

        // Phase 3: Subtitle fades in
        delay(150)
        subtitleAlpha.animateTo(1f, tween(400))

        // Wait then navigate
        delay(800)
        onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RosePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rounded-square SOS icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .scale(iconScale.value)
                    .alpha(iconAlpha.value)
                    .clip(RoundedCornerShape(22.dp))
                    .background(RoseDark)
            ) {
                Text(
                    text = "SOS",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title text
            Text(
                text = "S O S   A L E R T",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle text
            Text(
                text = "be safe, be secure",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}
