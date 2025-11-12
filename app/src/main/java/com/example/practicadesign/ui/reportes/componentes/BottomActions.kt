package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Barra de acciones inferior del formulario de reportes.
 * 
 * Muestra botones para navegar entre pasos (Anterior/Siguiente) o para enviar el reporte
 * cuando se está en el último paso.
 * 
 * @param currentStep Paso actual del formulario
 * @param totalSteps Número total de pasos
 * @param onPrevious Callback cuando se presiona el botón "Anterior"
 * @param onNext Callback cuando se presiona el botón "Siguiente" o "Enviar Reporte"
 */
@Preview(showBackground = true)
@Composable
fun BottomActionsScreenPreview() {
    BottomActions(
        currentStep = 1,
        totalSteps = 5,
        onPrevious = {},
        onNext = {}
    )
}

@Composable
fun BottomActions(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                Button(
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anterior", color = Color(0xFF334155))
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (currentStep == totalSteps) "Enviar Reporte" else "Siguiente", color = Color.White)
            }
        }
    }
}
