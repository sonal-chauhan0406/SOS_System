package com.example.okdriverpanicbutton.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PanicDarkColorScheme = darkColorScheme(
    primary = RosePrimary,
    onPrimary = Color.White,
    primaryContainer = RoseDark,
    onPrimaryContainer = RoseLight,
    secondary = RoseMedium,
    onSecondary = Color.Black,
    secondaryContainer = RoseDark,
    onSecondaryContainer = RoseLight,
    tertiary = RoseDark,
    onTertiary = Color.White,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = SurfaceDeep,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = RoseDark,
    onError = Color.White,
    outline = GlassBorder
)

private val PanicLightColorScheme = lightColorScheme(
    primary = RosePrimary,
    onPrimary = Color.White,
    primaryContainer = RoseLight,
    onPrimaryContainer = SOSRedDark,
    secondary = SOSRedLight,
    onSecondary = Color.White,
    secondaryContainer = RoseLight,
    onSecondaryContainer = SOSRedDark,
    tertiary = RoseDark,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFFCE4EC),
    onSurfaceVariant = LightSecondaryText,
    error = RoseDark,
    onError = Color.White,
    outline = LightOutline
)

@Composable
fun OkDriverPanicButtonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PanicDarkColorScheme else PanicLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                SurfaceDark.toArgb()
            } else {
                RosePrimary.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}