package com.example.practicadesign.ui.reportes.componentes

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Paso 2: Selección de ubicación del reporte.
 * 
 * Permite al usuario elegir entre usar su ubicación actual (GPS) o
 * seleccionar una ubicación manualmente en el mapa.
 * 
 * @param selectedLocation Tipo de ubicación seleccionada ("current" o "map", null si ninguna)
 * @param currentCoordinates Coordenadas actuales seleccionadas (puede ser null)
 * @param onSelect Callback cuando se selecciona un tipo de ubicación con las coordenadas
 * @param error Mensaje de error si no se ha seleccionado una ubicación (null si no hay error)
 */
@Composable
fun StepLocation(
    selectedLocation: String?,
    currentCoordinates: LatLng?,
    onSelect: (String, LatLng?) -> Unit,
    error: String? = null
) {
    val context = LocalContext.current
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    
    // Launcher para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Permisos concedidos, obtener ubicación
            getCurrentLocation(context) { location ->
                isLoadingLocation = false
                if (location != null) {
                    onSelect("current", location)
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo obtener la ubicación. Usando ubicación por defecto.",
                        Toast.LENGTH_SHORT
                    ).show()
                    onSelect("current", LatLng(19.4326, -99.1332))
                }
            }
        } else {
            isLoadingLocation = false
            Toast.makeText(
                context,
                "Permisos de ubicación denegados. Se usará ubicación por defecto.",
                Toast.LENGTH_LONG
            ).show()
            onSelect("current", LatLng(19.4326, -99.1332))
        }
    }
    
    // Si se muestra el selector de mapa
    if (showMapPicker) {
        LocationMapPicker(
            initialLocation = currentCoordinates ?: LatLng(19.4326, -99.1332),
            onLocationSelected = { location ->
                onSelect("map", location)
                showMapPicker = false
            },
            onCancel = {
                showMapPicker = false
            }
        )
        return
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "¿Dónde ocurrió?",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Selecciona la ubicación del incidente",
            color = Color(0xFF64748B)
        )
        
        // Mensaje de error si existe
        error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LocationOption(
                title = "Mi ubicación actual",
                subtitle = if (isLoadingLocation) "Obteniendo ubicación..." else "Usar GPS para ubicación exacta",
                selected = selectedLocation == "current",
                isLoading = isLoadingLocation,
                onSelect = {
                    isLoadingLocation = true
                    // Solicitar permisos de ubicación
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
            LocationOption(
                title = "Seleccionar en mapa",
                subtitle = "Elegir ubicación manualmente",
                selected = selectedLocation == "map",
                onSelect = {
                    // Mostrar el selector de mapa
                    showMapPicker = true
                }
            )
        }
        
        // Mostrar coordenadas si ya se seleccionó una ubicación
        if (currentCoordinates != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
                border = BorderStroke(1.dp, Color(0xFF0891B2))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ubicación seleccionada:",
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${String.format("%.6f", currentCoordinates.latitude)}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Lng: ${String.format("%.6f", currentCoordinates.longitude)}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
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
 * @param isLoading Indica si se está cargando la ubicación (muestra indicador de progreso)
 * @param onSelect Callback cuando se selecciona esta opción
 */
@Composable
private fun LocationOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    isLoading: Boolean = false,
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
            
            // Mostrar indicador de carga si está obteniendo ubicación
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF0891B2)
                )
            }
        }
    }
}

/**
 * Obtiene la ubicación actual del dispositivo usando los servicios de ubicación.
 * 
 * Usa PRIORITY_BALANCED_POWER_ACCURACY en lugar de HIGH_ACCURACY para mayor compatibilidad
 * y mejor tasa de éxito en la obtención de ubicación.
 * 
 * @param context Contexto de Android
 * @param onLocationResult Callback que recibe la ubicación obtenida (puede ser null si falla)
 */
private fun getCurrentLocation(
    context: android.content.Context,
    onLocationResult: (LatLng?) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Primero intenta obtener la última ubicación conocida (más rápido)
        try {
            @Suppress("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    // Si hay una última ubicación, usarla
                    android.util.Log.d("StepLocation", "Usando última ubicación conocida: ${lastLocation.latitude}, ${lastLocation.longitude}")
                    onLocationResult(LatLng(lastLocation.latitude, lastLocation.longitude))
                } else {
                    // Si no hay última ubicación, obtener una nueva
                    android.util.Log.d("StepLocation", "No hay última ubicación, obteniendo nueva...")
                    getCurrentLocationFresh(fusedLocationClient, onLocationResult)
                }
            }.addOnFailureListener { e ->
                android.util.Log.e("StepLocation", "Error al obtener última ubicación", e)
                // Si falla, intentar obtener una nueva
                getCurrentLocationFresh(fusedLocationClient, onLocationResult)
            }
        } catch (e: SecurityException) {
            android.util.Log.e("StepLocation", "SecurityException al obtener última ubicación", e)
            onLocationResult(null)
        }
    } catch (e: Exception) {
        android.util.Log.e("StepLocation", "Error general al obtener ubicación", e)
        onLocationResult(null)
    }
}

/**
 * Obtiene una ubicación fresca del GPS.
 */
private fun getCurrentLocationFresh(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationResult: (LatLng?) -> Unit
) {
    try {
        val cancellationTokenSource = CancellationTokenSource()
        
        @Suppress("MissingPermission")
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Cambiado de HIGH_ACCURACY para mejor compatibilidad
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                android.util.Log.d("StepLocation", "Nueva ubicación obtenida: ${location.latitude}, ${location.longitude}")
                onLocationResult(LatLng(location.latitude, location.longitude))
            } else {
                android.util.Log.w("StepLocation", "getCurrentLocation retornó null")
                onLocationResult(null)
            }
        }.addOnFailureListener { e ->
            android.util.Log.e("StepLocation", "Error en getCurrentLocation", e)
            onLocationResult(null)
        }
    } catch (e: SecurityException) {
        android.util.Log.e("StepLocation", "SecurityException en getCurrentLocationFresh", e)
        onLocationResult(null)
    }
}
