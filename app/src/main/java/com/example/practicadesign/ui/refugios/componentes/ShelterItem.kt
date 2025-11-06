package com.example.practicadesign.ui.refugios.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.example.practicadesign.data.Shelter
import com.example.practicadesign.ui.mapa.MapScreen
import com.google.android.gms.maps.model.LatLng



// --- VISTAS PREVIAS (PREVIEWS) ---

@Preview(showBackground = false, name = "Item - Abierto y con espacio")
@Composable
private fun ShelterItemPreviewOpen() {
    // 1. Crea un refugio de prueba "ideal"
    val mockShelter = Shelter(
        id = "shelter-1",
        //position = LatLng(19.43, -99.13),
        latitude = 19.43,
        longitude = -99.13,
        name = "Refugio Corazón de la Ciudad",
        isOpen = true,
        address = "Calle Falsa 123, Centro Histórico",
        capacity = 150,
        currentOccupancy = 75
    )
    // 2. Llama al componente con el estado "colapsado"
    ShelterItem(
        shelter = mockShelter,
        expanded = false,
        onClick = {}
    )
}

@Preview(showBackground = false, name = "Item - Lleno y expandido")
@Composable
private fun ShelterItemPreviewFullExpanded() {
    // 1. Crea un refugio de prueba "lleno"
    val mockShelter = Shelter(
        id = "shelter-2",
     //   position = LatLng(19.40, -99.15),
        latitude = 19.40,
        longitude = -99.15,
        name = "Centro Comunitario Roma",
        isOpen = true,
        address = "Av. Insurgentes Sur 300, Roma Nte.",
        capacity = 100,
        currentOccupancy = 100
    )
    // 2. Llama al componente con el estado "expandido"
    ShelterItem(
        shelter = mockShelter,
        expanded = true, // La flecha aparecerá girada
        onClick = {}
    )
}

@Preview(showBackground = false, name = "Item - Cerrado")
@Composable
private fun ShelterItemPreviewClosed() {
    // 1. Crea un refugio de prueba "cerrado"
    val mockShelter = Shelter(
        id = "shelter-3",
       // position = LatLng(19.35, -99.18),
        latitude = 19.35,
        longitude = -99.18,
        name = "Auditorio del Sur (No disponible)",
        isOpen = false,
        address = "Periférico Sur 4121, Fuentes del Pedregal",
        capacity = 300,
        currentOccupancy = 0
    )
    // 2. Llama al componente
    ShelterItem(
        shelter = mockShelter,
        expanded = false,
        onClick = {}
    )
}

/* -------------------------------------------------
   Refugio compacto
------------------------------------------------- */
@Composable
fun ShelterItem(
    shelter: Shelter,
    expanded: Boolean,
    onClick: () -> Unit
) {

    // ✅ 3. Crea el estado de la animación de rotación
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f, // Gira a 90 grados si está expandido
        animationSpec = tween(durationMillis = 300), // Sincroniza la duración
        label = "rotationAnimation"
    )


    val (bgColor, textColor) = when {
        !shelter.isOpen -> Color(0xFFFEF2F2) to Color(0xFFDC2626)
        shelter.currentOccupancy >= shelter.capacity -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        else -> Color(0xFFD1FAE5) to Color(0xFF059669)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono estado
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.House,
                contentDescription = null,
                tint = textColor
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shelter.name,
                color = Color(0xFF0F172A),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = shelter.address,
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${shelter.currentOccupancy}/${shelter.capacity} • " +
                        if (!shelter.isOpen) "Cerrado" else if (shelter.currentOccupancy >= shelter.capacity) "Lleno" else "Abierto",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = "Expandir/Colapsar",
            modifier = Modifier.rotate(rotationAngle), // ¡La magia sucede aquí!
            tint = Color(0xFF475569)
        )
    }
}