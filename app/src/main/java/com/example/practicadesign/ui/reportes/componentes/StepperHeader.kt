package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * Encabezado del stepper que muestra el progreso del formulario.
 * 
 * Muestra una barra de progreso animada y círculos numerados para cada paso,
 * indicando visualmente en qué paso del formulario se encuentra el usuario.
 * 
 * @param currentStep Paso actual del formulario (1-5)
 * @param totalSteps Número total de pasos
 */
@Preview(showBackground = true)
@Composable
fun StepperHeaderScreenPreview() {
    StepperHeader(currentStep = 2, totalSteps = 5)
}

@Composable
fun StepperHeader(currentStep: Int, totalSteps: Int) {
    val targetProgress = (currentStep - 1).toFloat() / (totalSteps - 1)

    // Anima el progreso de la barra
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "ProgressBarAnimation"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        // Box para superponer la barra de progreso y los círculos
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Barra de progreso (se dibuja primero, en el fondo)
            Box(modifier = Modifier.padding(horizontal = 16.dp).offset(y = 16.dp)) {
                // Línea de fondo (track)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(2.dp))
                )
                // Línea de progreso (foreground)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0891B2), Color(0xFF06B6D4))
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            // Fila de círculos (se dibuja después, encima de la barra)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val labels = listOf("Tipo", "Ubicación", "Detalles", "Evidencia", "Confirmar")
                (1..totalSteps).forEach { idx ->
                    val isCompleted = idx < currentStep
                    val isActive = idx == currentStep

                    // Define los colores objetivo según el estado
                    val targetCircleColor = when {
                        isCompleted -> Color(0xFF10B981)
                        isActive -> Color(0xFF0891B2)
                        else -> Color.White
                    }
                    val targetBorderColor = when {
                        isCompleted -> Color(0xFF10B981)
                        isActive -> Color(0xFF0891B2)
                        else -> Color(0xFFE2E8F0)
                    }
                    val targetLabelColor = if (isActive) Color(0xFF0891B2) else Color(0xFF94A3B8)
                    val targetNumberColor = if (isActive) Color.White else Color(0xFF94A3B8)

                    // Anima los colores
                    val animationSpec = tween<Color>(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )

                    val animatedCircleColor by animateColorAsState(
                        targetCircleColor,
                        animationSpec,
                        label = "CircleColor"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetBorderColor,
                        animationSpec,
                        label = "BorderColor"
                    )
                    val animatedLabelColor by animateColorAsState(
                        targetLabelColor,
                        animationSpec,
                        label = "LabelColor"
                    )
                    val animatedNumberColor by animateColorAsState(
                        targetNumberColor,
                        animationSpec,
                        label = "NumberColor"
                    )

                    // Dibuja cada círculo y su etiqueta
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(animatedCircleColor)
                                .border(
                                    BorderStroke(2.dp, animatedBorderColor),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "$idx",
                                    color = animatedNumberColor,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = labels[idx - 1],
                            fontSize = 11.sp,
                            color = animatedLabelColor
                        )
                    }
                }
            }
        }
    }
}
