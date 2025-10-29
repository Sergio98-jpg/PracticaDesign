package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/* -------------------------
   Risk Zone (radial blurred gradient)
   ------------------------- */

@Composable
fun RiskZone(modifier: Modifier = Modifier, color: Color, alpha: Float = 0.4f) {
    // We can't rely on platform blur uniformly; we simulate with large faded radial gradient
    Box(
        modifier = modifier
            .graphicsLayer {
                // on API 31+ you could set RenderEffect blur; but we simulate using alpha & gradient
                shadowElevation = 0f
                clip = false
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
                    center = Offset(0.5f, 0.5f),
                    radius = 300f
                ),
                shape = CircleShape
            )
    )
}
