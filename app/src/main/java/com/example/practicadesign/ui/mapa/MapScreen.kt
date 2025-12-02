package com.example.practicadesign.ui.mapa

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
//import androidx.paging.map
//import androidx.privacysandbox.tools.core.generator.build
import com.composables.icons.lucide.*
import com.example.practicadesign.R
import com.example.practicadesign.data.toGoogleLatLng
import com.example.practicadesign.ui.mapa.componentes.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * Convierte un drawable vectorial en un BitmapDescriptor para usar como icono de marcador.
 * 
 * Esta función NO es un composable para poder usarla dentro de `remember` y cachearla eficientemente.
 * 
 * @param context Contexto de Android necesario para acceder a los recursos
 * @param vectorResId ID del recurso drawable vectorial
 * @return BitmapDescriptor para usar en un Marker, o null si falla la conversión
 */
private fun bitmapDescriptorFromVector(
    context: android.content.Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Pantalla principal del mapa que muestra:
 * - Ubicación del usuario
 * - Zonas de riesgo, refugios y calles inundadas
 * - Filtros y búsqueda
 * - Menú lateral y controles de navegación
 * 
 * Sigue el patrón MVVM utilizando MapViewModel para gestionar el estado.
 * 
 * @param modifier Modificador de Compose para personalizar el layout
 * @param navController Controlador de navegación para moverse entre pantallas
 * @param mapViewModel ViewModel que gestiona el estado del mapa
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
    initialShelterId: String? = null // ID del refugio para navegar desde otra pantalla
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Si se pasa un ID de refugio inicial, seleccionarlo y mostrar la ruta
    // Esperamos a que los refugios se carguen antes de intentar seleccionar uno
    LaunchedEffect(initialShelterId, uiState.shelters, uiState.isLoading) {
        initialShelterId?.let { shelterId ->
            // Solo intentar seleccionar si los refugios ya están cargados
            if (!uiState.isLoading && uiState.shelters.isNotEmpty()) {
                // Buscar el refugio en la lista actual
                val shelter = uiState.shelters.find { it.id == shelterId }
                if (shelter != null) {
                    // Seleccionar el refugio y mostrar la ruta
                    mapViewModel.onShelterSelected(shelter)
                    mapViewModel.getRouteToDestination(shelter.position)
                }
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Permisos de ubicación necesarios para la app
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Muestra un mensaje de error si hay problemas de red
    LaunchedEffect(uiState.networkError) {
        uiState.networkError.getMessage()?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
        }
    }


    // Lanzador para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                mapViewModel.startLocationUpdates()
            } else {
                Toast.makeText(
                    context,
                    "Se requieren permisos de ubicación para mostrar tu posición.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()
    
    // Controla si ya se ha animado a la ubicación del usuario la primera vez
    var hasAnimatedToUserLocation by remember { mutableStateOf(false) }

    // Anima la cámara a la ubicación del usuario cuando se obtiene por primera vez
    LaunchedEffect(uiState.userLocation) {
        val location = uiState.userLocation
        if (location != null && !hasAnimatedToUserLocation) {
            // 1. Definimos el desplazamiento vertical que queremos en dp.
            // Un valor negativo mueve la cámara HACIA ABAJO (desplazando el marcador).
            // Ajusta este valor para que coincida con el que usas en el botón de centrar.
            val yOffsetDp = -125.dp

            // 2. Convertimos los dp a píxeles (px).
            val density = context.resources.displayMetrics.density
            val yOffsetPx = yOffsetDp.value * density

            // 3. Primero, animamos la cámara a la ubicación con un zoom inicial.
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 16f),
                durationMs = 1500 // Reducimos un poco para que la animación total no sea muy larga
            )

            // 4. Inmediatamente después, aplicamos la animación de desplazamiento.
            // Esto crea un efecto de "reajuste" suave.
            cameraPositionState.animate(
                update = CameraUpdateFactory.scrollBy(0f, yOffsetPx),
                durationMs = 500 // Duración corta para el ajuste final
            )

            // 5. Marcamos que la animación inicial ya se ha completado.
            hasAnimatedToUserLocation = true
        }
    }

    // Solicita permisos de ubicación al entrar en la pantalla
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(locationPermissions)
    }

    // Verifica si el usuario está en una zona de riesgo cuando cambia la ubicación o las zonas
    LaunchedEffect(uiState.userLocation, uiState.riskZones) {
        uiState.userLocation?.let { location ->
            mapViewModel.checkUserLocationAgainstZones(location)
        }
    }

    var showFilterMenu by remember { mutableStateOf(false) }

    // Bottom sheet para mostrar detalles de refugio o zona de riesgo seleccionada
    val sheetState = rememberModalBottomSheetState()
    val isSheetVisible = uiState.selectedShelter != null || uiState.selectedRiskZone != null

    if (isSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { mapViewModel.onBottomSheetDismissed() },
            sheetState = sheetState
        ) {
            when {
                uiState.selectedShelter != null -> {
                    ShelterInfoContent(
                        shelter = requireNotNull(uiState.selectedShelter),
                        onGetDirections = { destination ->
                            // Obtener la ruta y mostrarla en el mapa
                            mapViewModel.getRouteToDestination(destination)
                        }
                    )
                }
                uiState.selectedRiskZone != null -> {
                    ZoneRiskInfoContent(zone = requireNotNull(uiState.selectedRiskZone))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Altura estándar para el BottomNav (ajustable según necesidades)
        val bottomNavHeight = 80.dp
        val minZoomToShowMarkers = 13.5f

        // Mapa de Google Maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(bottom = bottomNavHeight),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            // ==================== OPTIMIZACIONES DE RENDIMIENTO ====================
            // Cachear los íconos DENTRO del lambda de GoogleMap para asegurar que Google Maps esté inicializado
            // Estos íconos se crean UNA VEZ cuando el mapa se inicializa y se reutilizan en todas las recomposiciones
            val iconUser = remember { bitmapDescriptorFromVector(context, R.drawable.mapa_pin_wrapper) }
            val iconShelterOpen = remember { bitmapDescriptorFromVector(context, R.drawable.refugio_wrapper) }
            val iconShelterClosed = remember { bitmapDescriptorFromVector(context, R.drawable.refugio2_wrapper) }
            
            // Marcador de ubicación del usuario
            uiState.userLocation?.let { userLocation ->

                // Estado del marcador para controlar la InfoWindow
                val markerState = rememberMarkerState(position = userLocation)

                Marker(
                    state = markerState,
                    title = "Mi Ubicación",
                    icon = iconUser,
                    snippet = "Aquí es donde estoy",
                    anchor = Offset(0.5f, 0.5f),
                    onClick = {
                        // Muestra la InfoWindow sin centrar el mapa
                        markerState.showInfoWindow()
                        // Retorna true para indicar que el evento ha sido consumido
                        // y prevenir el centrado automático del mapa
                        true
                    }
                )
            }


            // Zonas de riesgo (polígonos)
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers && uiState.filters.showRiskZones) {
                uiState.riskZones.forEach { riskZone ->
                    // Cachear el color y los puntos para evitar recalcularlos en cada frame
                    val color = remember(riskZone.riskLevel) {
                        when (riskZone.riskLevel) {
                            "ALTO" -> Color.Red.copy(alpha = 0.5f)
                            "MEDIO" -> Color.Yellow.copy(alpha = 0.5f)
                            "BAJO" -> Color.Green.copy(alpha = 0.5f)
                            else -> Color.Gray.copy(alpha = 0.5f)
                        }
                    }
                    
                    // Cachear la conversión de coordenadas para evitar mapear en cada frame
                    val points = remember(riskZone.area) {
                        riskZone.area.map { LatLng(it.latitude, it.longitude) }
                    }
                    
                    Polygon(
                        points = points,
                        fillColor = color,
                        strokeWidth = 3f,
                        strokeColor = color.copy(alpha = 1f),
                        clickable = true,
                        onClick = {
                            mapViewModel.onZoneRiskSelected(riskZone)
                        }
                    )
                }
            }

            // Refugios (marcadores)
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers && uiState.filters.showShelters) {
                uiState.shelters.forEach { shelter ->
                    // Usar el ícono cacheado según el estado del refugio (OPTIMIZACIÓN CRÍTICA)
                    val icon = if (shelter.isOpen) iconShelterOpen else iconShelterClosed
                    
                    Marker(
                        state = rememberMarkerState(position = shelter.position),
                        icon = icon,
                        onClick = {
                            mapViewModel.onShelterSelected(shelter)
                            true
                        }
                    )
                }
            }

            // Calles inundadas (polilíneas)
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers && uiState.filters.showFloodedStreets) {
                uiState.floodedStreets.forEach { street ->
                    // Cachear la conversión de coordenadas para evitar mapear en cada frame
                    val points = remember(street.path) {
                        street.path.map { it.toGoogleLatLng() }
                    }
                    
                    Polyline(
                        points = points,
                        color = Color(0xFF3B82F6),
                        width = 15f,
                        geodesic = true
                    )
                }
            }
            
            // Ruta de navegación (si existe)
            uiState.route?.let { route ->
                Polyline(
                    points = route,
                    color = Color(0xFF10B981), // Verde para la ruta
                    width = 12f,
                    geodesic = true
                )
            }
        }

        // Indicador de carga
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Indicador de carga de ruta
        if (uiState.isRouteLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 200.dp) // Posicionar arriba del bottom sheet
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Calculando ruta...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // UI principal sobre el mapa
        Box(modifier = Modifier.fillMaxSize()) {
            // Logo y ubicación actual
            FloatingLogo(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .padding(top = 16.dp),
                location = uiState.currentLocationName
            )

            // Botón de menú lateral
            FloatingMenu(
                onClick = { mapViewModel.onMenuClicked() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .padding(top = 16.dp)
            )

            // Banner de estado y búsqueda
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 110.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBanner(state = uiState.bannerState)

                // Banner de error de conexión
                NetworkErrorBanner(
                    message = uiState.networkError.getMessage(),
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Mostrar búsqueda solo si hay datos disponibles (cache o conexión exitosa)
                if (uiState.shelters.isNotEmpty() || uiState.riskZones.isNotEmpty()) {
                    FloatingSearchButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onSearchClick = { mapViewModel.onSearchActive() }
                    )
                }
               // StatsRow()
            }

            // Botones flotantes de acción (FABs)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para centrar en la ubicación del usuario
                FloatingActionButton(
                    icon = Lucide.LocateFixed,
                    contentDescription = "Centrar",
                    onClick = {
                        uiState.userLocation?.let { userLocation ->
                            coroutineScope.launch {
                                // 1. Definimos el desplazamiento vertical que queremos en dp.
                                // Un valor negativo mueve la cámara HACIA ABAJO.
                                // Un valor positivo mueve la cámara HACIA ARRIBA.
                                // Como quieres que el marcador baje, necesitas un valor negativo.
                                val yOffsetDp = -125.dp // ¡Ajusta este valor!

                                // 2. Convertimos los dp a píxeles (px).
                                val density = context.resources.displayMetrics.density
                                val yOffsetPx = yOffsetDp.value * density

                                // 3. Primero, nos movemos a la ubicación con el zoom deseado.
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(userLocation, 17f), // Zoom fijo de 17f
                                    durationMs = 1000
                                )

                                // 4. Inmediatamente después (o puedes anidar la animación), aplicamos el desplazamiento.
                                // Esto crea un efecto de "reajuste" muy agradable.
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.scrollBy(0f, yOffsetPx), // No hay scroll horizontal, solo vertical
                                    durationMs = 500 // Una duración corta para el reajuste
                                )
                            }
                        }
                    }
                )

                // Botón de filtros con menú desplegable
                Box {
                    FloatingActionButton(
                        icon = Lucide.Filter,
                        contentDescription = "Filtros",
                        onClick = { showFilterMenu = true }
                    )

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        Text(
                            text = "Capas del mapa",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        FilterMenuItem(
                            text = "Zonas de Riesgo",
                            checked = uiState.filters.showRiskZones,
                            onCheckedChange = { mapViewModel.toggleRiskZonesVisibility() }
                        )
                        FilterMenuItem(
                            text = "Refugios",
                            checked = uiState.filters.showShelters,
                            onCheckedChange = { mapViewModel.toggleSheltersVisibility() }
                        )
                        FilterMenuItem(
                            text = "Calles Inundadas",
                            checked = uiState.filters.showFloodedStreets,
                            onCheckedChange = { mapViewModel.toggleFloodedStreetsVisibility() }
                        )
                    }
                }
            }
        }

        // Overlay oscuro cuando el menú lateral está abierto
        AnimatedVisibility(
            visible = uiState.isMenuOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { mapViewModel.onMenuClicked() }
                    )
            )
        }

        // Menú lateral
        SideMenu(
            open = uiState.isMenuOpen,
            onClose = { mapViewModel.onMenuClicked() },
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // Overlay de búsqueda
        if (uiState.isSearching) {
            SearchOverlay(
                searchResults = uiState.searchResults,
                searchQuery = uiState.searchQuery,
                onQueryChange = { mapViewModel.onSearchQueryChange(it) },
                onDismiss = { mapViewModel.onSearchInactive() },
                onItemSelected = { result ->
                    // Obtener la posición del elemento seleccionado y seleccionarlo en el ViewModel
                    val targetPosition = mapViewModel.onSearchResultSelected(result)
                    
                    // Cerrar el overlay de búsqueda
                    mapViewModel.onSearchInactive()
                    
                    // Centrar la cámara en el resultado seleccionado
                    targetPosition?.let { position ->
                        coroutineScope.launch {
                            // Animar la cámara a la ubicación del resultado con zoom apropiado
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(position, 17f),
                                durationMs = 1000
                            )
                        }
                    }
                }
            )
        }
    }
}
