package com.example.practicadesign.ui.refugios.componentes

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun ShelterItemSkeleton() {
    // La animación de brillo
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton-transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton-alpha"
    )
    val brush = SolidColor(Color.LightGray.copy(alpha = alpha))

    // La estructura del esqueleto (imita a ShelterItem)
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        // ... (dibuja rectángulos con el 'brush' que imiten tu UI)
        // Por ejemplo:
        Spacer(
            modifier = Modifier
                .size(48.dp)
                .background(brush, shape = CircleShape)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.7f)
                    .background(
                        brush,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.5f)
                    .background(
                        brush,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}