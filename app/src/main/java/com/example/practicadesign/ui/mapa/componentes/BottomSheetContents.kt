package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.practicadesign.ui.theme.PracticaDesignTheme
import com.example.practicadesign.ui.theme.riskColors // Importamos la extensión
import com.google.android.gms.maps.model.LatLng

/**
 * Preview para el contenido de información de zona de riesgo en estado de peligro.
 */
@Preview(showBackground = true, name = "Zona de Peligro")
@Composable
fun PreviewZoneRiskInfoContentDanger() {
    PracticaDesignTheme(darkTheme = true) {
        val sampleZone = RiskZone(
            id = "zone_danger_1",
            name = "Centro Histórico",
            riskLevel = "ALTO",
            area = emptyList()
        )
        ZoneRiskInfoContent(
            zone = sampleZone,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "Refugio Abierto")
@Composable
fun PreviewShelterInfoContentOpen() {
    PracticaDesignTheme(darkTheme = true) {
        val sampleShelterOpen = Shelter(
            id = "shelter_1",
            name = "Polideportivo Central",
            address = "Av. Libertad 123, Centro",
            capacity = 500,
            currentOccupancy = 120,
            isOpen = true,
            latitude = 19.4326,
            longitude = -99.1332,
            phoneContact = "123456789",
            responsible = "El Jefe",
        )

        ShelterInfoContent(
            shelter = sampleShelterOpen,
            onGetDirections = {}
        )
    }
}

@Preview(showBackground = true, name = "Refugio Cerrado")
@Composable
fun PreviewShelterInfoContentClosed() {
    PracticaDesignTheme (darkTheme = true) {
        val sampleShelterClosed = Shelter(
            id = "shelter_2",
            name = "Escuela Primaria Benito Juárez",
            address = "Calle 5 de Mayo #45",
            capacity = 200,
            currentOccupancy = 0,
            isOpen = false,
            latitude = 19.4326,
            longitude = -99.1332,
            phoneContact = "123456789",
            responsible = "El Jefe",
        )

        ShelterInfoContent(
            shelter = sampleShelterClosed,
            onGetDirections = {}
        )
    }
}

/**
 * Muestra el contenido de información de una zona de riesgo.
 * Adapta los colores automáticamente según el nivel de riesgo usando el Tema.
 */
@Composable
fun ZoneRiskInfoContent(zone: RiskZone, modifier: Modifier = Modifier) {
    // 1. Lógica de colores usando MaterialTheme.riskColors
    // Esto garantiza soporte automático para modo Claro/Oscuro
    val (estadoTexto, colorContenedor, colorTexto) = when (zone.riskLevel.uppercase()) {
        "BAJO" -> Triple(
            "Zona segura",
            MaterialTheme.riskColors.lowContainer,
            MaterialTheme.riskColors.low
        )
        "MEDIO" -> Triple(
            "Zona en advertencia",
            MaterialTheme.riskColors.mediumContainer,
            MaterialTheme.riskColors.medium
        )
        "ALTO" -> Triple(
            "Zona en peligro",
            MaterialTheme.riskColors.highContainer,
            MaterialTheme.riskColors.high
        )
        else -> Triple(
            "Nivel desconocido",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Nivel de riesgo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface // Color de texto estándar
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .background(colorContenedor, shape = RoundedCornerShape(40))
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
            text = when (zone.riskLevel.uppercase()) {
                "BAJO" -> "Esta área es considerada segura actualmente."
                "MEDIO" -> "Precaución: hay posibles riesgos en esta zona. Mantente informado."
                "ALTO" -> "Peligro: área con alto riesgo. Se recomienda buscar un lugar seguro y consultar los refugios cercanos."
                else -> "No hay información de riesgo disponible para esta zona."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Color grisáceo estándar del tema
        )

        Button(
            onClick = { /* TODO: Navegación */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Usa el color primario del tema
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Lucide.House, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Ver refugios cercanos")
        }
    }
}

/**
 * Muestra el contenido de información de un refugio.
 */
@Composable
fun ShelterInfoContent(
    shelter: Shelter,
    modifier: Modifier = Modifier,
    onGetDirections: (LatLng) -> Unit
) {
    // Definimos colores de estado basados en el tema
    // Usamos el color de riesgo bajo (generalmente verde/seguro) para "Abierto"
    // y el color de error (rojo) para "Cerrado".
    val statusColor = if (shelter.isOpen) {
        MaterialTheme.riskColors.low // O usa un color específico de éxito si lo tienes en ThemeColors.kt
    } else {
        MaterialTheme.colorScheme.error
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título
        Text(
            text = shelter.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Fila de Estado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (shelter.isOpen) Lucide.CircleCheck else Lucide.CircleX,
                contentDescription = "Estado",
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (shelter.isOpen) "Abierto" else "Cerrado",
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Fila de Dirección
        InfoRow(icon = Lucide.MapPin, title = "Dirección", content = shelter.address)

        // Fila de Capacidad
        InfoRow(icon = Lucide.Users, title = "Capacidad", content = "${shelter.currentOccupancy} / ${shelter.capacity} personas")

        // Botón principal
        Button(
            onClick = { onGetDirections(shelter.position) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Lucide.Route, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("CÓMO LLEGAR")
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, content: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
