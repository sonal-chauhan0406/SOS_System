package com.example.okdriverpanicbutton.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.okdriverpanicbutton.ui.theme.GlassBackground
import com.example.okdriverpanicbutton.ui.theme.GlassBorder

/**
 * Reusable glassmorphism container.
 * Applies semi-transparent background, subtle white border, and rounded corners.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassBackground)
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = shape
            )
            .padding(contentPadding),
        content = content
    )
}
