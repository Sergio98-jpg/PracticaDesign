package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Preview de la fila de estadísticas.
 */
@Preview(showBackground = true)
@Composable
fun PreviewStatsRow() {
    StatsRow()
}

/**
 * Fila de estadísticas que muestra tarjetas con información relevante.
 * 
 * Muestra alertas activas y refugios cercanos.
 * 
 * Nota: Los valores actualmente están hardcodeados. En una implementación futura,
 * estos datos deberían venir del ViewModel o del estado de la aplicación.
 * 
 * @param modifier Modificador de Compose para personalizar el layout
 * @param activeAlerts Número de alertas activas (por defecto 1)
 * @param nearbyShelters Número de refugios cercanos (por defecto 5)
 */
@Composable
fun StatsRow(
    modifier: Modifier = Modifier,
    activeAlerts: Int = 1,
    nearbyShelters: Int = 5
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            number = activeAlerts.toString(),
            label = "Alerta activa",
            type = StatType.ALERT,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            number = nearbyShelters.toString(),
            label = "Refugios cerca",
            type = StatType.SHELTER,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Tipo de estadística a mostrar en las tarjetas.
 */
enum class StatType { ALERT, SHELTER, SAFE }

/**
 * Tarjeta individual de estadística que muestra un número y una etiqueta.
 * 
 * El color de la tarjeta varía según el tipo de estadística.
 * 
 * @param number Número a mostrar
 * @param label Etiqueta descriptiva
 * @param type Tipo de estadística que determina el color
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Composable
fun StatCard(number: String, label: String, type: StatType, modifier: Modifier = Modifier) {
    val color = when (type) {
        StatType.ALERT -> Color(0xFFEF4444)
        StatType.SHELTER -> Color(0xFF0891B2)
        StatType.SAFE -> Color(0xFF10B981)
    }

    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(number, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 14.sp, color = Color(0xFF64748B), maxLines = 2)
    }
}