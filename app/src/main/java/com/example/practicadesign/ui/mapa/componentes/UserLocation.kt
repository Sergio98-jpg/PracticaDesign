package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/* -------------------------
   User Location (dot + pulse)
   ------------------------- */

@Composable
fun UserLocation(modifier: Modifier = Modifier) {
    // pulse animation for outer ring
    val infinite = rememberInfiniteTransition(label = "pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAnim"
    )

    Box(
        modifier = modifier.size(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse - grows from 16 -> 60 with fading alpha
        Canvas(modifier = Modifier.size(60.dp), onDraw = {
            val maxRadius = size.minDimension / 2f
            val radius = androidx.compose.ui.util.lerp(8f, maxRadius, pulse)
            drawCircle(
                color = Color(0xFF0891B2).copy(alpha = 0.4f * (1f - pulse)),
                radius = radius
            )
        })

        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFF0891B2))
                .border(width = 3.dp, color = Color.White, shape = CircleShape)
                .shadow(8.dp, CircleShape)
        )
    }
}
