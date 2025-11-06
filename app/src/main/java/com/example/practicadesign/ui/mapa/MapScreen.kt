package com.example.practicadesign.ui.mapa


import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicadesign.ui.mapa.MapViewModel

//Componentes
import com.example.practicadesign.ui.mapa.componentes.*

// --- IMPORTACIÓN CORRECTA PARA LUCIDE ICONS ---
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Crosshair
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.LocateFixed
import com.composables.icons.lucide.Shell
import com.composables.icons.lucide.Map
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.User
import com.google.maps.android.compose.GoogleMap
import androidx.compose.foundation.layout.IntrinsicSize // <-- AÑADE ESTE IMPORT
import androidx.compose.foundation.layout.height      // <-- AÑADE ESTE IMPORT
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.platform.LocalDensity      // <-- AÑADE ESTE IMPORT
import com.composables.icons.lucide.Filter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch // Para la corrutina del botón
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.maps.android.compose.Polyline
import com.example.practicadesign.R // Importa los recursos de tu app
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
// import com.google.maps.android.compose.BitmapDescriptorFactory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Route
import com.composables.icons.lucide.Users

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.TriangleAlert

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.example.practicadesign.data.toGoogleLatLng

@Composable
private fun bitmapDescriptorFromVector(
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    // Define las dimensiones del bitmap. Puedes ajustarlas según tus necesidades.
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


@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen(navController = NavController(LocalContext.current))
}
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
    // --- ✅ 1. LÓGICA PARA PERMISOS Y UBICACIÓN ---
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // ✅ 2. Usa LaunchedEffect para reaccionar a los cambios de error
    LaunchedEffect(uiState.networkError) {
        if (uiState.networkError != null) {
            snackbarHostState.showSnackbar(
                message = uiState.networkError!!,
                duration = SnackbarDuration.Short
            )
            // Opcional: podrías querer "consumir" el error después de mostrarlo
            // para que no reaparezca si la pantalla se recompone.
            // mapViewModel.clearError() // (Necesitarías crear esta función)
        }
    }


    // Prepara el lanzador para la solicitud de permisos
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Comprueba si el permiso de ubicación fina fue concedido
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                // Si fue concedido, inicia las actualizaciones de ubicación
                mapViewModel.startLocationUpdates()
            } else {
                // Opcional: Muestra un mensaje al usuario si los rechaza
                Toast.makeText(context, "Se requieren permisos de ubicación para mostrar tu posición.", Toast.LENGTH_LONG).show()
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()

    // 1. Inicia la cámara en una posición genérica o nula.
    //    El mapa no se moverá hasta que se lo indiquemos.
    val cameraPositionState = rememberCameraPositionState()

    // 2. Crea un 'LaunchedEffect' que se active CADA VEZ que la ubicación del usuario cambie.
    //    Usamos 'remember' para asegurarnos de que solo reaccione a la PRIMERA ubicación válida.
    var hasAnimatedToUserLocation by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userLocation) {
        // Guarda el valor de userLocation en una variable local inmutable (val)
        val location = uiState.userLocation

        // Ahora, el 'if' comprueba la variable local
        // Si tenemos una ubicación y todavía no hemos animado la cámara...
        if (location != null && !hasAnimatedToUserLocation) {

            // Como 'location' es una variable local, el compilador AHORA SÍ
            // sabe que es un LatLng no nulo dentro de este bloque.
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    location, // ✅ Usa la variable local segura
                    16f // Un nivel de zoom adecuado
                ),
                durationMs = 1500 // Una animación suave
            )
            // Marca que ya hemos realizado la animación inicial
            hasAnimatedToUserLocation = true
        }
    }

    // `LaunchedEffect` se ejecuta una vez cuando el Composable entra en la pantalla
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(locationPermissions)
    }
    // --- FIN DE LA LÓGICA DE PERMISOS ---

    var showFilterMenu by remember { mutableStateOf(false) }

/*    val coroutineScope = rememberCoroutineScope() // Necesario para animar la cámara*/

    // --- AÑADE ESTO ---
    // 1. Crea y recuerda el estado de la cámara.
    //    Este objeto controlará la posición y el zoom del mapa.
/*    val cameraPositionState = rememberCameraPositionState {
        // Posición inicial del mapa (puedes poner una ubicación por defecto)
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 16f) // Ciudad de México como ejemplo
    }*/
    val miUbicacionSimulada = LatLng(19.4326, -99.1332) // Simulación
    LaunchedEffect(uiState.userLocation, uiState.riskZones) {
        uiState.userLocation?.let { location ->
            mapViewModel.checkUserLocationAgainstZones(location)
        }
    }


    // --- ✅ AÑADE LA LÓGICA DEL BOTTOMSHEET ---
    val sheetState = rememberModalBottomSheetState()
    // Comprobamos si CUALQUIER item está seleccionado para decidir si mostrar el BottomSheet
    val isSheetVisible = uiState.selectedShelter != null || uiState.selectedRiskZone != null


    if (isSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { mapViewModel.onBottomSheetDismissed() },
            sheetState = sheetState
        ) {
            // Por ahora, solo un texto de prueba. Luego crearemos un Composable dedicado.
            // Le pasamos el refugio seleccionado que no puede ser nulo aquí.
            when {
                uiState.selectedShelter != null -> {
                    ShelterInfoContent(shelter = uiState.selectedShelter!!)
                }
                uiState.selectedRiskZone != null -> {
                    ZoneRiskInfoContent(zone = uiState.selectedRiskZone!!)
                }
            }
        }
    }
    // --- FIN DE LA LÓGICA DEL BOTTOMSHEET ---
    Box(modifier = Modifier.fillMaxSize()) {

        // -----------------------------
        // 1️⃣ CAPA DE FONDO
        // -----------------------------

        // 1. Definimos una altura estándar para nuestro BottomNav.
        //    Esto nos permitirá calcular el padding necesario.
        val bottomNavHeight = 80.dp // Puedes ajustar este valor si es necesario.

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState, // <-- ¡AQUÍ!
            // Aquí puedes configurar el estado inicial del mapa, como la cámara
            // 2. Aplicamos el contentPadding.
            //    Esto "empuja" la UI del mapa (logo de Google, etc.) hacia arriba.
            contentPadding = PaddingValues(bottom = bottomNavHeight),
            // Opcional pero recomendado: Desactiva los controles de zoom por defecto
            // si vas a usar tus propios botones.
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            // Dentro de este bloque puedes añadir Marcadores, Polígonos, etc.
            // Por ejemplo, para tus zonas de riesgo:
            // uiState.riskZones.forEach { zone ->
            //     Polygon(...)
            // }
/*            val iconUser = bitmapDescriptorFromVector(
                R.drawable.mapa_pin_wrapper
            )*/
/*            val iconUser = PulsingLocationMarker()
            Marker(
                state = rememberMarkerState(position = miUbicacionSimulada),
                title = "Mi Ubicación",
                icon = iconUser,
                snippet = "Aquí es donde estoy (simulado)"
            )*/
            val minZoomToShowMarkers = 13.5f // Puedes ajustar este valor

            uiState.userLocation?.let { userLocation ->
               // val iconUser = PulsingLocationMarker() // Tu composable para el marcador
                val iconUser = bitmapDescriptorFromVector(
                    R.drawable.mapa_pin_wrapper
                )
                Marker(
                    state = rememberMarkerState(position = userLocation),
                    title = "Mi Ubicación",
                    icon = iconUser,
                    snippet = "Aquí es donde estoy",
                    anchor = Offset(0.5f, 0.5f)
                )
            }


            // --- ✅ AÑADE ESTA LÓGICA PARA DIBUJAR LAS ZONAS ---
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers) {
                if (uiState.filters.showRiskZones) {
                    uiState.riskZones.forEach { zone ->
                        Polygon(
                            points = zone.area.map { it.toGoogleLatLng() },
                            strokeWidth = 3f, // Ancho del borde
                            strokeColor = when (zone.state) { // Color del borde
                                BannerState.Warning -> Color(0x80_FBBF24) // Amarillo semitransparente
                                BannerState.Danger -> Color(0x80_F87171) // Rojo semitransparente
                                else -> Color.Transparent
                            },
                            fillColor = when (zone.state) { // Color del relleno
                                BannerState.Warning -> Color(0x55_FBBF24) // Amarillo muy transparente
                                BannerState.Danger -> Color(0x55_F87171) // Rojo muy transparente
                                else -> Color.Transparent
                            },
                            clickable = true,
                            onClick = {
                                mapViewModel.onZoneRiskSelected(zone)
                            }
                        )
                    }
                }
            }
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers) {
                if (uiState.filters.showShelters) {
                    uiState.shelters.forEach { shelter ->
                        val icon = bitmapDescriptorFromVector(
                            if (shelter.isOpen) R.drawable.refugio_wrapper else R.drawable.refugio2_wrapper
                        )

                        Marker(
                            state = rememberMarkerState(position = shelter.position),
                            /*                    title = shelter.name,
                    snippet = if (shelter.isOpen) "Estado: Abierto" else "Estado: Cerrado",*/
                            icon = icon, // <-- Asigna el icono ya convertido
                            // ✅ AÑADE ESTA LAMBDA onMarkerClick
                            onClick = {
                                mapViewModel.onShelterSelected(shelter)
                                true // Evita que el mapa recentre la cámara automáticamente
                            }
                        )

                    }
                }
            }
            if (cameraPositionState.position.zoom >= minZoomToShowMarkers) {
                if (uiState.filters.showFloodedStreets) {
                    uiState.floodedStreets.forEach { street ->
                        Polyline(
                            points = street.path.map { it.toGoogleLatLng() },
                            color = Color(0xFF3B82F6), // Un color azul intenso
                            width = 15f, // Grosor de la línea
                            geodesic = true // Hace que la línea siga la curvatura de la Tierra
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center) // Se alinea dentro del Box
            )
        }
        // -----------------------------
        // 2️⃣ CAPA INTERMEDIA (UI principal)
        // -----------------------------
        Box(modifier = Modifier.fillMaxSize()) {
            // Header
            FloatingLogo(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .padding(top = 16.dp),
                location = uiState.currentLocationName
            )

            // Botón de menú
            FloatingMenu(
                onClick = { mapViewModel.onMenuClicked() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .padding(top = 16.dp)
            )

            // Banner y estadísticas
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
                    onSearchClick = { mapViewModel.onSearchActive() } // Esta llamada está perfecta
                )

                StatsRow()
            }

            // FABs flotantes
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(icon = Lucide.LocateFixed, contentDescription = "Centrar",
                    onClick = {
                    // Idealmente, aquí obtendrías la ubicación real.
                    // Por ahora, vamos a simularla con un valor fijo.

          /*          // Usamos una corrutina para llamar a la función de animación
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                miUbicacionSimulada,
                                20f // Un nivel de zoom más cercano
                            ),
                            durationMs = 1000 // Duración de la animación en milisegundos
                        )
                    }*/
                        uiState.userLocation?.let { userLocation ->
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        userLocation,
                                        17f // Un nivel de zoom más cercano
                                    ),
                                    durationMs = 1000
                                )
                            }
                        }
                })

                // ✅ INICIA LA MODIFICACIÓN DEL BOTÓN DE FILTRO
                Box { // Envolvemos el botón en un Box para anclar el DropdownMenu
                    FloatingActionButton(
                        icon = Lucide.Filter,
                        contentDescription = "Filtros",
                        onClick = { showFilterMenu = true } // Abre el menú
                    )

                    // El menú desplegable
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

            // Barra inferior
/*            BottomNav(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomNavHeight))*/
        }

        // -----------------------------
        // 3️⃣ CAPA SUPERIOR (menú lateral)
        // -----------------------------
        AnimatedVisibility(
            visible = uiState.isMenuOpen,
            // 1. USA SOLO FADE IN / FADE OUT. ¡ELIMINA expandIn Y shrinkOut!
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            // Este Box es solo para el fondo oscuro
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Sin efecto ripple al hacer clic
                        onClick = { mapViewModel.onMenuClicked() }
                    )
            )
        }

        // 2. MUEVE EL SideMenu FUERA DEL AnimatedVisibility
        //    Esto es crucial. El SideMenu ya no es hijo directo de la animación de visibilidad.
        //    Ahora su offset se animará sin interferencias.
        SideMenu(
            open = uiState.isMenuOpen,
            onClose = { mapViewModel.onMenuClicked() },
            modifier = Modifier.align(Alignment.TopEnd) // Lo alineamos a la derecha
        )

        if (uiState.isSearching) {
            SearchOverlay(
                searchResults = uiState.searchResults,
                searchQuery = uiState.searchQuery,
                onQueryChange = { mapViewModel.onSearchQueryChange(it) },
                onDismiss = { mapViewModel.onSearchInactive() },
                onItemSelected = { result ->
                    mapViewModel.onSearchInactive()
                    // TODO: Centrar cámara en el resultado
                }
            )
        }
    }
}


@Composable
fun PulsingLocationMarker(): BitmapDescriptor {
    val context = LocalContext.current

    val bitmap = remember {
        val size = 200 // px aprox
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Círculo de pulso
        val pulsePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#660891B2") // color semitransparente
            style = android.graphics.Paint.Style.FILL
        }

        // Círculo principal (punto central)
        val dotPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#0891B2") // color base
            style = android.graphics.Paint.Style.FILL
        }

        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 6f
        }

        val center = size / 2f
        val dotRadius = 16f
        val pulseRadius = 28f

        // Dibuja el pulso
        canvas.drawCircle(center, center, pulseRadius, pulsePaint)
        // Dibuja el punto central
        canvas.drawCircle(center, center, dotRadius, dotPaint)
        // Dibuja el borde blanco
        canvas.drawCircle(center, center, dotRadius, borderPaint)

        bitmap
    }

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/*                    */
