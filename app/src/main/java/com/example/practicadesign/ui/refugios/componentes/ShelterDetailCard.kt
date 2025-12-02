package com.example.practicadesign.ui.refugios.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Lucide
import com.example.practicadesign.data.Shelter
import com.example.practicadesign.ui.mapa.MapScreen
import com.example.practicadesign.ui.navegacion.Screen

@Preview(showBackground = true, name = "Tarjeta de Detalle de Refugio")
@Composable
private fun ShelterDetailCardPreview() {
    // 1. Crea un objeto Shelter falso con datos de prueba.
    val mockShelter = Shelter(
        id = "shelter-1",
        //position = LatLng(19.4326, -99.1332), // Coordenadas de ejemplo
        latitude = 19.4326,
        longitude = -99.1332,
        name = "Refugio Principal Zócalo",
        isOpen = true,
        address = "Plaza de la Constitución S/N, Centro Histórico, CDMX",
        capacity = 250,
        currentOccupancy = 112
    )

    // 2. Llama a tu componente pasándole los datos falsos.
    ShelterDetailCard(
        shelter = mockShelter,
        onClose = {
            // La acción de cierre no hace nada en la preview
        }
    )
}

@Preview(showBackground = true, name = "Tarjeta de Detalle de Refugio")
@Composable
private fun ShelterDetailCardPClosereview() {
    // 1. Crea un objeto Shelter falso con datos de prueba.
    val mockShelter = Shelter(
        id = "shelter-1",
       // position = LatLng(19.4326, -99.1332), // Coordenadas de ejemplo
        latitude = 19.4326,
        longitude = -99.1332,
        name = "Refugio Principal Zócalo",
        isOpen = false,
        address = "Plaza de la Constitución S/N, Centro Histórico, CDMX",
        capacity = 250,
        currentOccupancy = 112
    )

    // 2. Llama a tu componente pasándole los datos falsos.
    ShelterDetailCard(
        shelter = mockShelter,
        onClose = {
            // La acción de cierre no hace nada en la preview
        }
    )
}

/* -------------------------------------------------
   Detalle expandible
------------------------------------------------- */
@Composable
fun ShelterDetailCard(
    shelter: Shelter,
    navController: NavController? = null, // NavController opcional (null solo en previews)
    onClose: () -> Unit
) {
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = shelter.name,
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClose) {
                    Icon(Lucide.CircleX, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(shelter.address, color = Color(0xFF475569), style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailStat("Capacidad", "${shelter.currentOccupancy}/${shelter.capacity}")
                DetailStat("Estado", if (shelter.isOpen) "Abierto" else "Cerrado")
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        // Navegar al mapa con el refugio seleccionado para mostrar la ruta
                        navController?.navigate(Screen.Mapa.withShelterId(shelter.id)) {
                            // Limpiar el back stack hasta el mapa para evitar navegación circular
                            popUpTo(Screen.Mapa.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = navController != null, // Deshabilitar si no hay navController (solo en previews)
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2))
                ) {
                    Text("Cómo llegar", color = Color.White)
                }

                OutlinedButton(
                    onClick = { /* TODO: llamada */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Llamar")
                }
            }
        }
    }
}

@Composable
fun RowScope.DetailStat(label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .padding(12.dp)
    ) {
        Text(label, color = Color(0xFF64748B), style = MaterialTheme.typography.bodySmall)
        Text(value, color = Color(0xFF0F172A), style = MaterialTheme.typography.titleSmall)
    }
}
