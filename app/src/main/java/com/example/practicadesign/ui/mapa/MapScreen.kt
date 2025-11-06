package com.example.practicadesign.ui.mapa

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
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
 * @param vectorResId ID del recurso drawable vectorial
 * @return BitmapDescriptor para usar en un Marker, o null si falla la conversión
 */
@Composable
private fun bitmapDescriptorFromVector(
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    val context = LocalContext.current
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
    mapViewModel: MapViewModel = viewModel()
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Permisos de ubicación necesarios para la app
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Muestra un mensaje de error si hay problemas de red
    LaunchedEffect(uiState.networkError) {
        uiState.networkError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
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
/*            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 16f),
                durationMs = 1500
            )
            hasAnimatedToUserLocation = true*/
            // --- INICIO DE LA LÓGICA DE CENTRADO CON DESPLAZAMIENTO ---

            // 1. Definimos el desplazamiento vertical que queremos en dp.
            // Un valor negativo mueve la cámara HACIA ABAJO (desplazando el marcador).
            // Ajusta este valor para que coincida con el que usas en el botón de centrar.
            val yOffsetDp = -125.dp

            // 2. Convertimos los dp a píxeles (px).
        //    val density = LocalDensity.current.density
        //    val yOffsetPx = yOffsetDp.value * density

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

            // --- FIN DE LA LÓGICA ---
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
                    ShelterInfoContent(shelter = requireNotNull(uiState.selectedShelter))
                }
                uiState.selectedRiskZone != null -> {
                    ZoneRiskInfoContent(zone = requireNotNull(uiState.selectedRiskZone))
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
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
            // Marcador de ubicación del usuario
            uiState.userLocation?.let { userLocation ->
                val iconUser = bitmapDescriptorFromVector(R.drawable.mapa_pin_wrapper)
                Marker(
                    state = rememberMarkerState(position = userLocation),
                    title = "Mi Ubicación",
                    icon = iconUser,
                    snippet = "Aquí es donde estoy",
                    anchor = Offset(0.5f, 0.5f)
                )
            }


            // Zonas de riesgo (polígonos)
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers && uiState.filters.showRiskZones) {
                uiState.riskZones.forEach { zone ->
                    Polygon(
                        points = zone.area.map { it.toGoogleLatLng() },
                        strokeWidth = 3f,
                        strokeColor = when (zone.state) {
                            BannerState.Warning -> Color(0x80_FBBF24)
                            BannerState.Danger -> Color(0x80_F87171)
                            else -> Color.Transparent
                        },
                        fillColor = when (zone.state) {
                            BannerState.Warning -> Color(0x55_FBBF24)
                            BannerState.Danger -> Color(0x55_F87171)
                            else -> Color.Transparent
                        },
                        clickable = true,
                        onClick = {
                            mapViewModel.onZoneRiskSelected(zone)
                        }
                    )
                }
            }

            // Refugios (marcadores)
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers && uiState.filters.showShelters) {
                uiState.shelters.forEach { shelter ->
                    val icon = bitmapDescriptorFromVector(
                        if (shelter.isOpen) R.drawable.refugio_wrapper else R.drawable.refugio2_wrapper
                    )
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
                    Polyline(
                        points = street.path.map { it.toGoogleLatLng() },
                        color = Color(0xFF3B82F6),
                        width = 15f,
                        geodesic = true
                    )
                }
            }
        }

        // Indicador de carga
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusBanner(state = uiState.bannerState)
                FloatingSearchButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onSearchClick = { mapViewModel.onSearchActive() }
                )
                StatsRow()
            }

            // Botones flotantes de acción (FABs)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
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
                            color = Color(0xFF050505),
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
                    mapViewModel.onSearchInactive()
                    // TODO: Centrar cámara en el resultado seleccionado
                }
            )
        }
    }
}
