package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/* -------------------------
   Map Grid
   ------------------------- */

@Composable
fun MapGrid(modifier: Modifier = Modifier) {
    // We'll animate a translation of the grid for the "moving grid" effect
    val infinite = rememberInfiniteTransition(label = "gridMove")
    val offset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(animation = tween(20000, easing = LinearEasing)),
        label = "gridOffset"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cell = 60f  // grid spacing (px)
        // draw a subtle background underlying map
        drawRect(color = Color(0xFFE0F2F1))

        val paintAlpha = 0.15f
        val lineColor = Color(0xFF60A5FA).copy(alpha = paintAlpha)

        val xStart = -cell + (offset % cell)
        val yStart = -cell + (offset % cell)

        var x = xStart
        while (x < w + cell) {
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += cell
        }
        var y = yStart
        while (y < h + cell) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += cell
        }
    }
}