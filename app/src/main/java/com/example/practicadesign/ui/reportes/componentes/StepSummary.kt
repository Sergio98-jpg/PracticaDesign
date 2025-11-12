package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paso 5: Resumen del reporte.
 * 
 * Muestra un resumen de toda la información del reporte antes de enviarlo.
 * 
 * @param selectedType Tipo de reporte seleccionado
 * @param selectedLocation Tipo de ubicación seleccionada
 * @param title Título del reporte
 * @param urgency Nivel de urgencia seleccionado
 * @param photosCount Número de fotos adjuntas
 */
@Composable
fun StepSummary(
    selectedType: String?,
    selectedLocation: String?,
    title: String,
    urgency: String,
    photosCount: Int
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Confirmar reporte",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Revisa la información antes de enviar",
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                SummaryRow(label = "Tipo de reporte", value = mapTypeKeyToName(selectedType))
                SummaryRow(label = "Ubicación", value = mapLocationKeyToName(selectedLocation))
                SummaryRow(label = "Título", value = title.ifBlank { "-" })
                SummaryRow(label = "Urgencia", value = mapUrgencyToLabel(urgency))
                SummaryRow(label = "Fotos adjuntas", value = photosCount.toString())
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 * Componente para mostrar una fila de información en el resumen.
 * 
 * @param label Etiqueta de la información
 * @param value Valor de la información
 */
@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF64748B))
        Text(
            text = value,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}

private fun mapTypeKeyToName(key: String?): String {
    return when (key) {
        "inundacion" -> "Inundación"
        "calle-bloqueada" -> "Calle Bloqueada"
        "refugio-lleno" -> "Refugio Lleno"
        "dano-infraestructura" -> "Daño a Infraestructura"
        "persona-riesgo" -> "Persona en Riesgo"
        "otro" -> "Otro"
        else -> "-"
    }
}

private fun mapLocationKeyToName(key: String?): String {
    return when (key) {
        "current" -> "Mi ubicación actual"
        "map" -> "Seleccionada en mapa"
        else -> "-"
    }
}

private fun mapUrgencyToLabel(u: String) = when (u) {
    "high" -> "🔴 Alta"
    "medium" -> "🟡 Media"
    "low" -> "🟢 Baja"
    else -> "-"
}
