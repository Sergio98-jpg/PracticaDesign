package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paso 2: Selección de ubicación del reporte.
 * 
 * Permite al usuario elegir entre usar su ubicación actual (GPS) o
 * seleccionar una ubicación manualmente en el mapa.
 * 
 * @param selectedLocation Tipo de ubicación seleccionada ("current" o "map", null si ninguna)
 * @param onSelect Callback cuando se selecciona un tipo de ubicación
 */
@Composable
fun StepLocation(
    selectedLocation: String?,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text("¿Dónde ocurrió?", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Selecciona la ubicación del incidente", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LocationOption(
                title = "Mi ubicación actual",
                subtitle = "Usar GPS para ubicación exacta",
                selected = selectedLocation == "current",
                onSelect = { onSelect("current") }
            )
            LocationOption(
                title = "Seleccionar en mapa",
                subtitle = "Elegir ubicación manualmente",
                selected = selectedLocation == "map",
                onSelect = { onSelect("map") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map preview placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0F2F1)),
            contentAlignment = Alignment.Center
        ) {
            // Marker
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0891B2))
                    .border(BorderStroke(4.dp, Color.White), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 * Componente reutilizable para mostrar una opción de ubicación.
 * 
 * @param title Título de la opción
 * @param subtitle Subtítulo descriptivo
 * @param selected Indica si esta opción está seleccionada
 * @param onSelect Callback cuando se selecciona esta opción
 */
@Composable
private fun LocationOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFF0FDFA) else Color.White),
        border = BorderStroke(2.dp, if (selected) Color(0xFF0891B2) else Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) Brush.linearGradient(
                            listOf(
                                Color(0xFF0891B2),
                                Color(0xFF06B6D4)
                            )
                        ) else SolidColor(Color(0xFFF1F5F9))
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Icono de GPS simulado con un círculo
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (selected) Color.White else Color(0xFF94A3B8))
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF0F172A))
                Text(subtitle, color = Color(0xFF64748B))
            }
        }
    }
}
