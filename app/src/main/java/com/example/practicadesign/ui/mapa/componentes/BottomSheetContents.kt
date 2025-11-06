package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Route
import com.composables.icons.lucide.Users
import com.example.practicadesign.data.RiskZone
import com.example.practicadesign.data.Shelter

/**
 * Preview para el contenido de información de zona de riesgo en estado de peligro.
 */
@Preview(showBackground = true, name = "Zona de Peligro")
@Composable
fun PreviewZoneRiskInfoContentDanger() {
    // El modelo ahora usa 'riskLevel' con un String, imitando los datos del backend.
    val sampleZone = RiskZone(
        id = "zone_danger_1",
        name = "Centro Histórico", // Añadimos el nombre para que el preview sea más completo
        riskLevel = "ALTO", // Pasamos el dato crudo como String
        area = emptyList()
    )
    ZoneRiskInfoContent(
        zone = sampleZone,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Muestra el contenido de información de una zona de riesgo en el bottom sheet.
 * 
 * Incluye el nivel de riesgo, una descripción y un botón para ver refugios cercanos.
 * 
 * @param zone La zona de riesgo a mostrar
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Composable
fun ZoneRiskInfoContent(zone: RiskZone, modifier: Modifier = Modifier) {
/*    val (estadoTexto, colorEstado, colorTexto) = when (zone.state) {
        BannerState.Safe -> Triple("Zona segura", Color(0xFF10B981), Color(0xFF065F46))
        BannerState.Warning -> Triple("Zona en advertencia", Color(0xFFFDF7E7), Color(0xFF8F4617))
        BannerState.Danger -> Triple("Zona en peligro", Color(0xFFFFEBEE), Color(0xFF991B1B))
    }*/
    val (estadoTexto, colorEstado, colorTexto) = when (zone.riskLevel.uppercase()) {
        "BAJO" -> Triple("Zona segura", Color(0xFF10B981), Color(0xFF065F46))
        "MEDIO" -> Triple("Zona en advertencia", Color(0xFFFDF7E7), Color(0xFF8F4617))
        "ALTO" -> Triple("Zona en peligro", Color(0xFFFFEBEE), Color(0xFF991B1B))
        else -> Triple("Nivel desconocido", Color.Gray, Color.White) // Caso por defecto
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Nivel de riesgo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF020000)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .background(colorEstado, shape = RoundedCornerShape(40))
                .border(1.dp, colorTexto, RoundedCornerShape(40))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = estadoTexto,
                color = colorTexto,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
/*            text = when (zone.state) {
                BannerState.Safe -> "Esta área es considerada segura actualmente."
                BannerState.Warning -> "Precaución: hay posibles riesgos en esta zona."
                BannerState.Danger -> "Peligro: área con alto riesgo de inundación. Se recomienda evacuar inmediatamente y dirigirse al refugio más cercano."
            }*/
            text = when (zone.riskLevel.uppercase()) {
                "BAJO" -> "Esta área es considerada segura actualmente."
                "MEDIO" -> "Precaución: hay posibles riesgos en esta zona. Mantente informado."
                "ALTO" -> "Peligro: área con alto riesgo. Se recomienda buscar un lugar seguro y consultar los refugios cercanos."
                else -> "No hay información de riesgo disponible para esta zona."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Button(
            onClick = {
                // TODO: Implementar navegación a lista de refugios cercanos cuando esté disponible
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E7490),
                contentColor = Color.White
            )
        ) {
            Icon(Lucide.House, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Ver refugios cercanos")
        }
    }
}

/**
 * Muestra el contenido de información de un refugio en el bottom sheet.
 * 
 * Incluye el nombre, estado (abierto/cerrado), dirección, capacidad y un botón
 * para trazar la ruta hacia el refugio.
 * 
 * @param shelter El refugio a mostrar
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Composable
fun ShelterInfoContent(shelter: Shelter, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(), // Ocupa el ancho
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre elementos
    ) {
        // Título
        Text(
            text = shelter.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Fila de Estado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (shelter.isOpen) Lucide.CircleCheck else Lucide.CircleX,
                contentDescription = "Estado",
                tint = if (shelter.isOpen) Color(0xFF34D399) else Color(0xFFF87171),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (shelter.isOpen) "Abierto" else "Cerrado",
                fontWeight = FontWeight.SemiBold,
                color = if (shelter.isOpen) Color(0xFF34D399) else Color(0xFFF87171)
            )
        }

        // Divisor
        HorizontalDivider()

        // Fila de Dirección
        InfoRow(icon = Lucide.MapPin, title = "Dirección", content = shelter.address)

        // Fila de Capacidad
        InfoRow(icon = Lucide.Users, title = "Capacidad", content = "${shelter.currentOccupancy} / ${shelter.capacity} personas")

        // Botón principal
        Button(
            onClick = {
                // TODO: Implementar apertura de Google Maps con navegación al refugio
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Icon(Lucide.Route, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("CÓMO LLEGAR")
        }
    }
}

/**
 * Componente reutilizable para mostrar una fila de información con ícono, título y contenido.
 * 
 * @param icon Ícono a mostrar a la izquierda
 * @param title Título de la información
 * @param content Contenido de la información
 */
@Composable
fun InfoRow(icon: ImageVector, title: String, content: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = content, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

