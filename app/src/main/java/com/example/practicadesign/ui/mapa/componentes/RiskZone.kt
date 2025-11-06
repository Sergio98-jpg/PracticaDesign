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

/**
 * Componente visual de zona de riesgo con gradiente radial.
 * 
 * Simula un efecto de desenfoque usando un gradiente radial que se desvanece
 * desde el centro hacia afuera. Se utiliza para representar visualmente
 * zonas de riesgo en el mapa.
 * 
 * @param modifier Modificador de Compose para personalizar el layout
 * @param color Color base del gradiente
 * @param alpha Transparencia del gradiente (por defecto 0.4)
 */
@Composable
fun RiskZone(modifier: Modifier = Modifier, color: Color, alpha: Float = 0.4f) {
    // Simula un efecto de desenfoque usando un gradiente radial grande que se desvanece
    Box(
        modifier = modifier
            .graphicsLayer {
                // Nota: En API 31+ se podría usar RenderEffect blur, pero simulamos con alpha y gradiente
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
