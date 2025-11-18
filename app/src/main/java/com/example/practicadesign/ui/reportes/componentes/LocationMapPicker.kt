package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Componente de mapa interactivo para seleccionar una ubicación.
 * 
 * Muestra un Google Map donde el usuario puede tocar para seleccionar
 * una ubicación específica. La ubicación seleccionada se marca con un pin.
 * 
 * @param initialLocation Ubicación inicial del mapa (por defecto CDMX Centro)
 * @param onLocationSelected Callback cuando el usuario confirma la ubicación seleccionada
 * @param onCancel Callback cuando el usuario cancela la selección
 */
@Composable
fun LocationMapPicker(
    initialLocation: LatLng = LatLng(19.4326, -99.1332), // CDMX Centro por defecto
    onLocationSelected: (LatLng) -> Unit,
    onCancel: () -> Unit
) {
    // Ubicación seleccionada por el usuario
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    
    // Estado de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa de Google
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false
            ),
            onMapClick = { latLng ->
                // Actualiza la ubicación seleccionada cuando el usuario toca el mapa
                selectedLocation = latLng
            }
        ) {
            // Marcador en la ubicación seleccionada
            Marker(
                state = MarkerState(position = selectedLocation),
                title = "Ubicación seleccionada",
                snippet = "Lat: ${String.format("%.6f", selectedLocation.latitude)}, " +
                        "Lng: ${String.format("%.6f", selectedLocation.longitude)}"
            )
        }
        
        // Botones de acción en la parte inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cancelar
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                
                // Botón Confirmar
                Button(
                    onClick = { onLocationSelected(selectedLocation) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0891B2)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirmar ubicación")
                }
            }
        }
    }
}

