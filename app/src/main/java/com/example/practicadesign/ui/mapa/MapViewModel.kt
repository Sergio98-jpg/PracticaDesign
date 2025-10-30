// en /ui/map/MapViewModel.kt
package com.example.practicadesign.ui.mapa

import android.content.pm.PackageManager
import android.location.LocationRequest
import androidx.compose.animation.core.copy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.practicadesign.ui.mapa.componentes.BannerState
import com.example.practicadesign.ui.mapa.componentes.RiskZone

import com.google.android.gms.maps.model.LatLng // ✅ Importa LatLng
import androidx.lifecycle.viewModelScope // ✅ Importa viewModelScope
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import kotlinx.coroutines.launch // ✅ Importa launch
import com.google.maps.android.PolyUtil
import com.google.maps.android.PolyUtil.containsLocation
import com.example.practicadesign.ui.mapa.componentes.Shelter
import com.example.practicadesign.ui.mapa.componentes.FloodedStreet
import com.example.practicadesign.ui.mapa.componentes.SearchResult
import android.Manifest
import android.app.Application
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import android.location.Geocoder
import java.util.Locale
import com.example.practicadesign.data.MapRepository
import com.example.practicadesign.data.MapDataResult


// Define el estado de la UI para la pantalla del mapa
data class MapUiState(
    val isLoading: Boolean = true,
    val isMenuOpen: Boolean = false,
   // val riskZones: List<String> = emptyList(), // Futuro: será List<InundationZone>
    val userLocation: LatLng? = null,           // Futuro: será un objeto LatLng
    val bannerState: BannerState = BannerState.Safe,
    val riskZones: List<RiskZone> = emptyList(),
    val shelters: List<Shelter> = emptyList(),
    val floodedStreets: List<FloodedStreet> = emptyList(),
    val selectedShelter: Shelter? = null,
    val selectedRiskZone: RiskZone? = null,
    val filters: MapFilters = MapFilters(),
    val currentLocationName: String = "Cargando...",
    val isSearching: Boolean = false,
    val searchQuery: TextFieldValue = TextFieldValue(""),
    val searchResults: List<SearchResult> = emptyList()
)

data class MapFilters(
    val showRiskZones: Boolean = true,
    val showShelters: Boolean = true,
    val showFloodedStreets: Boolean = true
)
// Cambia la herencia de ViewModel a AndroidViewModel
//class MapViewModel : ViewModel() {
class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()


    // 1. Añade una instancia del Repository
    private val mapRepository = MapRepository()
    // --- ✅ 2. LÓGICA DE UBICACIÓN EN EL VIEWMODEL ---

    // --- LÓGICA DE UBICACIÓN CORREGIDA ---
    // --- ✅ 1. INICIALIZA GEOCODER ---
    private val geocoder = Geocoder(application, Locale.getDefault())

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application) // ✅ Ahora 'application' es válido

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Obtenemos la nueva LatLng
                val newLatLng = LatLng(location.latitude, location.longitude)

                // --- LLAMA A LA FUNCIÓN DE COMPROBACIÓN AQUÍ ---
                // Cada vez que recibimos una nueva ubicación, verificamos
                // si el usuario está dentro de una zona de riesgo.
                checkUserLocationAgainstZones(newLatLng) // ✅ ¡INTEGRACIÓN CLAVE!
                // --- ✅ 2. REALIZA LA GEODIFICACIÓN INVERSA ---
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    // --- ✅ LÓGICA MEJORADA PARA OBTENER LA UBICACIÓN COMPLETA ---
                    val fullLocationName = if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0] // Guardamos la primera dirección en una variable

                        // 1. Obtenemos la parte principal (municipio o colonia)
                        val mainLocation = address.locality ?: address.subLocality

                        // 2. Obtenemos el estado o área administrativa
                        val adminArea = address.adminArea

                        // 3. Combinamos los dos, si existen
                        when {
                            // Si tenemos ambos, los unimos con una coma
                            !mainLocation.isNullOrBlank() && !adminArea.isNullOrBlank() -> "$mainLocation, $adminArea"
                            // Si solo tenemos el principal, lo mostramos
                            !mainLocation.isNullOrBlank() -> mainLocation
                            // Si solo tenemos el estado, lo mostramos (raro, pero posible)
                            !adminArea.isNullOrBlank() -> adminArea
                            // Si no tenemos nada, mostramos el mensaje por defecto
                            else -> "Ubicación desconocida"
                        }
                    } else {
                        "Ubicación desconocida"
                    }

                    // Actualiza el uiState con el nombre completo
                    _uiState.update {
                        it.copy(
                            userLocation = newLatLng,
                            currentLocationName = fullLocationName
                        )
                    }

                } catch (e: Exception) {
                    // Si Geocoder falla, al menos actualizamos la coordenada
                    _uiState.update {
                        it.copy(
                            userLocation = newLatLng,
                            currentLocationName = "Buscando ubicación..."
                        )
                    }
                }
            }
        }
    }

    // Esta función será llamada desde la UI cuando se concedan los permisos
    fun startLocationUpdates() {
        // ✅ Forma moderna usando Builder
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // intervalo en milisegundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // equivalente a fastestInterval
            setWaitForAccurateLocation(false)
        }.build()

        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // Limpia el callback cuando el ViewModel es destruido
    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        mapRepository.closeWebSocket()
    }
    // --- FIN DE LA LÓGICA DE UBICACIÓN ---

    init {
        loadMapDataFromBackend()
        // 2. ✅ Comienza a escuchar las actualizaciones del WebSocket
        listenForRealTimeUpdates()
    }


    // 2. Crea la función para manejar la acción del menú
    fun onMenuClicked() {
        _uiState.update { currentState ->
            currentState.copy(isMenuOpen = !currentState.isMenuOpen)
        }
    }

/*    // Aquí irán las funciones para la navegación que usará el BottomNav
    fun onNavigateToAlerts() {
        // Lógica para navegar a la pantalla de alertas
    }

    fun onNavigateToShelters() {
        // Lógica para navegar a la pantalla de refugios
    }

    init {
        // En el futuro, aquí iniciarías la carga de datos del mapa,
        // la ubicación del usuario, etc.
        // viewModelScope.launch { loadMapData() }
    }

    // Aquí irán las funciones para interactuar con el mapa,
    // como onZoomChanged, onUserLocationDetected, etc.*/

    // ✅ AÑADE LAS FUNCIONES PARA MANEJAR LA SELECCIÓN
    fun onShelterSelected(shelter: Shelter) {
        _uiState.update { it.copy(selectedShelter = shelter, selectedRiskZone = null) }
    }

    fun onZoneRiskSelected(zone: RiskZone) {
        _uiState.update { it.copy(selectedRiskZone = zone, selectedShelter = null) }
    }

    fun onBottomSheetDismissed() {
        _uiState.update { it.copy(selectedShelter = null, selectedRiskZone = null) }
    }





    // ✅ Función que simula la carga de datos
/*    private fun loadMapDataFromBackend() {
        viewModelScope.launch {
            // En una app real, aquí harías una llamada de red con Retrofit
            // Por ahora, creamos una lista de zonas a mano
            val simulatedZones = listOf(
                // --- ZONA DE PRECAUCIÓN (Warning) - AMARILLA ---
                RiskZone(
                    id = "zone_warning_1",
                    state = BannerState.Warning,
                    area = listOf(
                        LatLng(19.45, -99.15),
                        LatLng(19.45, -99.14),
                        LatLng(19.44, -99.14),
                        LatLng(19.44, -99.15)
                    )
                ),
                // --- ZONA DE PELIGRO (Danger) - ROJA ---
                RiskZone(
                    id = "zone_danger_1",
                    state = BannerState.Danger,
                    area = listOf(
                        LatLng(19.42, -99.17),
                        LatLng(19.42, -99.16),
                        LatLng(19.41, -99.16),
                        LatLng(19.41, -99.17)
                    )
                ),
                // --- ZONA DE PELIGRO (Danger) - ROJA ---
                RiskZone(
                    id = "zone_danger_2",
                    state = BannerState.Warning,
                    area = listOf(
                        LatLng(19.44, -99.14),
                        LatLng(19.44, -99.13),
                        LatLng(19.43, -99.13),
                        LatLng(19.43, -99.14)
                    )
                )


            )

            // ✅ Crea una lista simulada de refugios
            val simulatedShelters = listOf(
                Shelter(
                    id = "shelter_1",
                    name = "Refugio Deportivo Benito Juárez",
                    position = LatLng(19.428, -99.155),
                    isOpen = true,
                    address = "Av. de los Insurgentes Sur 300",
                    capacity = 150,
                    currentOccupancy = 95
                ),
                Shelter(
                    id = "shelter_2",
                    name = "Centro Comunitario Roma",
                    position = LatLng(19.419, -99.162),
                    isOpen = true,
                    address = "Av. de los Insurgentes Sur 300",
                    capacity = 150,
                    currentOccupancy = 95
                ),
                Shelter(
                    id = "shelter_3",
                    name = "Escuela Primaria (Cerrado)",
                    position = LatLng(19.44, -99.13),
                    isOpen = false,
                    address = "Av. de los Insurgentes Sur 300",
                    capacity = 150,
                    currentOccupancy = 95
                )
            )

            val simulatedStreets = listOf(
                FloodedStreet(
                    id = "street_1",
                    path = listOf(
                        LatLng(19.435, -99.140),
                        LatLng(19.435, -99.135),
                        LatLng(19.436, -99.135)
                    )
                ),
                FloodedStreet(
                    id = "street_2",
                    path = listOf(
                        LatLng(19.410, -99.165),
                        LatLng(19.415, -99.168),
                        LatLng(19.418, -99.170)
                    )
                )
            )


            // Actualiza el UiState con los datos recibidos
            _uiState.update { currentState ->
                currentState.copy(riskZones = simulatedZones, shelters = simulatedShelters, floodedStreets = simulatedStreets, currentLocationName = "Mérida, YUC")
            }
        }
    }*/
    private fun loadMapDataFromBackend() {
        _uiState.update { it.copy(isLoading = true) } // Opcional: para mostrar un spinner

        viewModelScope.launch {
            try {
                // Llama a la única función del repositorio
                val mapData = mapRepository.getMapData()

                // Actualiza el estado con los datos recibidos
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        riskZones = mapData.riskZones,
                        shelters = mapData.shelters,
                        floodedStreets = mapData.floodedStreets
                    )
                }
            } catch (e: Exception) {
                // Aquí manejas los errores de red (ej. no hay internet)
                _uiState.update { it.copy(isLoading = false /*, podrías tener un errorState */) }
                // Log.e("MapViewModel", "Error al cargar datos del mapa", e)
            }
        }
    }


    private fun listenForRealTimeUpdates() {
        viewModelScope.launch {
            mapRepository.getRiskZoneUpdates().collect { newRiskZone ->
                // 3. ✅ Se ha recibido una nueva RiskZone desde el WebSocket
                _uiState.update { currentState ->
                    // Lógica para añadir o actualizar la zona de riesgo en la lista
                    val updatedZones = currentState.riskZones.filterNot { it.id == newRiskZone.id } + newRiskZone

                    // Actualiza el estado de la UI
                    currentState.copy(riskZones = updatedZones)
                }
            }
        }
    }

    // ✅ AÑADE ESTA NUEVA FUNCIÓN PÚBLICA
    fun checkUserLocationAgainstZones(userLocation: LatLng) {
        viewModelScope.launch {
            val currentZones = uiState.value.riskZones
            if (currentZones.isEmpty()) return@launch // No hacer nada si las zonas no han cargado

            // 1. Busca la zona de mayor riesgo que contenga la ubicación del usuario.
            //    maxByOrNull usa el orden natural del enum (Danger > Warning > Safe).
            val highestRiskZone = currentZones
                .filter { zone -> containsLocation(userLocation, zone.area, false) }
                .maxByOrNull { it.state.ordinal }

            // 2. Determina el nuevo estado. Si no se encontró ninguna zona, es 'Safe'.
            val newBannerState = highestRiskZone?.state ?: BannerState.Safe

            // 3. Actualiza el UiState SOLO si el estado ha cambiado, para evitar redibujos innecesarios.
            if (newBannerState != uiState.value.bannerState) {
                _uiState.update { it.copy(bannerState = newBannerState) }
            }
        }
    }

    //Filtros
    fun toggleRiskZonesVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showRiskZones = !it.filters.showRiskZones)
            it.copy(filters = updatedFilters)
        }
    }

    fun toggleSheltersVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showShelters = !it.filters.showShelters)
            it.copy(filters = updatedFilters)
        }
    }

    fun toggleFloodedStreetsVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showFloodedStreets = !it.filters.showFloodedStreets)
            it.copy(filters = updatedFilters)
        }
    }

    //Busqueda

    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        // ✅ LLAMA AL FILTRO AQUÍ
        filterResults(newQuery.text)
    }

    fun onSearchActive() {
        _uiState.update { it.copy(isSearching = true) }
        // ✅ Y LLAMA AL FILTRO AQUÍ TAMBIÉN
        filterResults("") // Pasa una cadena vacía para mostrar todos los resultados iniciales
    }

    fun onSearchInactive() {
        // Al desactivar, limpiamos el texto y el estado de búsqueda
        _uiState.update { it.copy(isSearching = false, searchQuery = TextFieldValue(""), searchResults = emptyList()) }
    }

    private fun filterResults(query: String) {
        // Tomamos la lista completa de refugios y zonas del estado actual
        val allItems = buildList {
            addAll(_uiState.value.shelters.map {
                SearchResult(
                    id = it.id, type = "Refugio", name = it.name, address = it.address,
                    icon = Lucide.House, iconColor = Color(0xFF0EA5E9)
                )
            })
            addAll(_uiState.value.riskZones.map {
                SearchResult(
                    id = it.id, type = "Zona de Riesgo", name = it.state.name,
                    address = "Área con nivel ${it.state}", icon = Lucide.TriangleAlert,
                    iconColor = if (it.state == BannerState.Danger) Color(0xFFEF4444) else Color(0xFFF59E0B)
                )
            })
        }

        // Aplicamos el filtro si la query no está vacía
        val filteredList = if (query.isBlank()) {
            allItems // Si no hay búsqueda, muestra todo
        } else {
            allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
        }
        // Actualizamos el estado con la lista filtrada
        _uiState.update { it.copy(searchResults = filteredList) }
    }
}
