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
import androidx.compose.material3.Divider
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
import com.google.android.gms.maps.model.LatLng

@Preview(showBackground = true, name = "Zona de Peligro")
@Composable
fun PreviewZoneRiskInfoContentDanger() {
    // 1. Crea un objeto RiskZone de ejemplo para el estado "Danger"
    val sampleZone = RiskZone(
        id = "zone_danger_1",
        state = BannerState.Danger, // Estado de peligro
        area = emptyList() // El área no es necesaria para la preview del contenido
    )

    // 2. Pasa el objeto de ejemplo al Composable
    ZoneRiskInfoContent(
        zone = sampleZone,
        modifier = Modifier.fillMaxWidth()
    )
}
/*
@Preview(showBackground = true, name = "Zona en Advertencia")
@Composable
fun PreviewZoneRiskInfoContentWarning() {
    // Puedes crear múltiples previews para diferentes estados
    val sampleZone = RiskZone(
        id = "zone_warning_1",
        state = BannerState.Warning, // Estado de advertencia
        area = emptyList()
    )
    ZoneRiskInfoContent(
        zone = sampleZone,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, name = "Refugio Abierto")
@Composable
fun PreviewShelterInfoContentOpen() {
    // 1. Crea un objeto Shelter de ejemplo para el estado "Abierto"
    val sampleShelter = Shelter(
        id = "shelter_1",
        name = "Refugio Deportivo Benito Juárez",
        address = "Calle 123, Colonia Centro, Mérida, Yucatán",
        position = LatLng(20.9674, -89.6243), // La posición no es visible aquí, pero es necesaria
        capacity = 150,
        currentOccupancy = 95,
        isOpen = true // Estado Abierto
    )

    // 2. Pasa el objeto de ejemplo al Composable
    ShelterInfoContent(
        shelter = sampleShelter,
        modifier = Modifier.fillMaxWidth()
    )
}*/
/*
@Preview(showBackground = true, name = "Refugio Cerrado")
@Composable
fun PreviewShelterInfoContentClosed() {
    // 1. Crea otro objeto Shelter para el estado "Cerrado"
    val sampleShelter = Shelter(
        id = "shelter_2",
        name = "Escuela Primaria Leona Vicario",
        address = "Calle 50 por 61 y 63, Centro",
        position = LatLng(20.9674, -89.6243),
        capacity = 80,
        currentOccupancy = 0,
        isOpen = false // Estado Cerrado
    )
    ShelterInfoContent(
        shelter = sampleShelter,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, name = "Fila de Información (InfoRow)")
@Composable
fun PreviewInfoRow() {
    InfoRow(
        icon = Lucide.MapPin, // Pasa un icono de ejemplo de Lucide
        title = "Dirección",
        content = "Av. de los Insurgentes Sur 300, Colonia Roma"
    )
}*/
@Composable
fun ZoneRiskInfoContent(zone: RiskZone, modifier: Modifier = Modifier) {
/*    val (estadoTexto, colorEstado) = when (zone.state) {
        BannerState.Safe -> "Zona segura" to Color(0xFF10B981)      // Verde
        BannerState.Warning -> "Zona en advertencia" to Color(0xFFFBBF24) // Amarillo
        BannerState.Danger -> "Zona en peligro" to Color(0xFF991B1B) // Rojo
    }*/
    val (estadoTexto, colorEstado, colorTexto) = when (zone.state) {
        BannerState.Safe -> Triple("Zona segura", Color(0xFF10B981), Color(0xFF065F46))      // Verde + texto verde oscuro
        BannerState.Warning -> Triple("Zona en advertencia", Color(0xFFFDF7E7), Color(0xFF8F4617)) // Amarillo + texto ámbar oscuro
        BannerState.Danger -> Triple("Zona en peligro", Color(0xFFFFEBEE), Color(0xFF991B1B)) // Rojo + texto claro
    }
    //991b1b 0xFF991B1B
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
                .background(colorEstado, shape = RoundedCornerShape(40)) // pill shape
                .border(1.dp, colorTexto, RoundedCornerShape(40)) // borde opcional
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
/*        Text(
            text = estadoTexto,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorEstado
        )*/

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (zone.state) {
                BannerState.Safe -> "Esta área es considerada segura actualmente."
                BannerState.Warning -> "Precaución: hay posibles riesgos en esta zona."
                BannerState.Danger -> "Peligro: área con alto riesgo de inundación. Se recomienda evacuar inmediatamente y dirigirse al refugio más cercano."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Button(
            onClick = { /* TODO: Implementar Fase 4 - Trazar Ruta */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
                //0e7490
                    // ✅ Y la configuración de color se pone aquí
            colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0E7490), // Tu color cian oscuro
            contentColor = Color.White // El color para el texto y el icono
        )
        ) {
            Icon(Lucide.House, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Ver refugios cercanos")
        }
    }
}

// ✅ AÑADE ESTE NUEVO COMPOSABLE DE CONTENIDO (aún es un borrador)
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
        Divider()

        // Fila de Dirección
        InfoRow(icon = Lucide.MapPin, title = "Dirección", content = shelter.address)

        // Fila de Capacidad
        InfoRow(icon = Lucide.Users, title = "Capacidad", content = "${shelter.currentOccupancy} / ${shelter.capacity} personas")

        // Botón principal
        Button(
            onClick = { /* TODO: Implementar Fase 4 - Trazar Ruta */ },
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

// Pequeño Composable de ayuda para no repetir código
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

